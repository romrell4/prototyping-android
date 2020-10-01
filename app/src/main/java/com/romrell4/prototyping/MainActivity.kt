package com.romrell4.prototyping

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.romrell4.prototyping.support.showToast
import com.romrell4.prototyping.widgets.ButtonFragment
import com.romrell4.prototyping.widgets.SliderFragment
import com.romrell4.prototyping.widgets.SpeakerFragment
import com.romrell4.prototyping.widgets.TextFragment
import kotlinx.android.synthetic.main.activity_main.*

private const val SHARED_PREFS_NAME = "com.romrell4.prototyping"
private const val SP_WIDGET_ID = "widget_id"

class MainActivity : AppCompatActivity() {

    private val fragments = listOf(
        TextFragment(),
        SpeakerFragment(),
        ButtonFragment(),
        SliderFragment()
    )
    private var currentFragment = fragments[0]
        set(value) {
            field = value
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, value)
                .commitAllowingStateLoss()
        }
    private val systemsRef = Firebase.firestore.collection("systems")
    private val serverRef = systemsRef.document("server")
    private var widgets = emptyList<Widget>()
        set(value) {
            field = value

            widget_spinner.adapter = ArrayAdapter(this, R.layout.spinner_item, listOf("Select a widget") + widgets.map { it.name }).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            //If they have a stored ID already, select that one
            getSharedPreferences(SHARED_PREFS_NAME, 0).getString(SP_WIDGET_ID, null)?.let { widgetId ->
                selectedWidget = widgets.first { it.id == widgetId }
            }
        }
    private var selectedWidget: Widget? = null
        @SuppressLint("DefaultLocale")
        set(value) {
            field = value

            //The "+ 1" handles the placeholder item that we place
            widget_spinner.setSelection(widgets.indexOfFirst { it.id == selectedWidget?.id } + 1)

            widget_type.text = value?.type?.capitalize()

            value?.let { widget ->
                ref = systemsRef.document(widget.id)

                //Save the selection in shared prefs
                getSharedPreferences(SHARED_PREFS_NAME, 0)
                    .edit()
                    .putString(SP_WIDGET_ID, widget.id)
                    .apply()

                //Update the displayed fragment
                fragments.firstOrNull { it.widgetType == widget.type }?.let {
                    currentFragment = it
                }

                //Update the widget image
                val resourceId = resources.getIdentifier("widget${widget.photoId}", "drawable", packageName)
                if (resourceId != 0) {
                    imageView.visibility = View.VISIBLE
                    imageView.setImageDrawable(
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
                            resources.getDrawable(resourceId, theme)
                        else resources.getDrawable(resourceId)
                    )
                } else {
                    imageView.visibility = View.GONE
                }
            }
        }
    private var ref: DocumentReference? = null
        set(value) {
            field = value
            docListener?.remove()
            docListener = field?.addSnapshotListener { querySnapshot, _ ->
                querySnapshot.getEvents().takeIf { it.isNotEmpty() }?.let {
                    //Handle each event in turn (should be sorted)
                    querySnapshot.getEvents().forEach { currentFragment.handleEvent(it) }

                    //Reset the events in the queue
                    ref?.set((querySnapshot?.data ?: mutableMapOf()).apply { this["events"] = emptyList<Event>() })
                } ?: println("Empty event list")
            }
        }
    private var docListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Listen for new widgets
        systemsRef.addSnapshotListener { collection, _ ->
            widgets = collection?.documents?.mapNotNull {
                val name = it.data?.get("name") as? String
                val type = it.data?.get("type") as? String
                val photoId = (it.data?.get("photo_id") as? Long)?.toInt()
                if (name != null && type != null) {
                    Widget(it.id, name, type, photoId)
                } else null
            }.orEmpty().sortedBy { it.name }
        }

        widget_spinner.apply {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    //The "- 1" is because of the placeholder that we have in the spinner
                    selectedWidget = widgets.getOrNull(position - 1)
                }
            }
        }
    }

    fun sendEvent(type: String, message: String?) {
        //Only send an event if they have a widget selected
        selectedWidget?.let { widget ->
            serverRef.get().addOnSuccessListener {
                serverRef.update("events", it.getEvents().toMutableList()
                    .apply { add(Event(type, widget.name, message)) })
                    .addOnSuccessListener { showToast(R.string.event_success) }
                    .addOnFailureListener { e -> showToast(getString(R.string.event_failed, e)) }
            }.addOnFailureListener { e ->
                showToast(getString(R.string.event_failed, e))
            }
        }
    }

    private fun DocumentSnapshot?.getEvents(): List<Event> {
        @Suppress("UNCHECKED_CAST")
        return (this?.get("events") as? List<HashMap<String, Any>>).orEmpty().map { Event(it) }
    }
}
