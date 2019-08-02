package com.romrell4.prototyping

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.romrell4.prototyping.support.showToast
import com.romrell4.prototyping.widgets.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_widget_name.view.*

private const val SHARED_PREFS_NAME = "com.romrell4.prototyping"
private const val SP_WIDGET_NAME = "widget_name"
private const val SP_WIDGET_TYPE_INDEX = "widget_index"

class MainActivity : AppCompatActivity() {

    private val fragments = listOf(
        TextFragment(),
        SpeakerFragment(),
        ButtonFragment(),
        KnobFragment()
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
    private var widgetName: String? = null
        set(value) {
            field = value
            widget_name.text = value
            value.takeIf { !it.isNullOrBlank() }?.let {
                ref = systemsRef.document(it)
            }
        }
    private var widgetTypeIndex: Int = 0
        set(value) {
            field = value
            spinner.setSelection(widgetTypeIndex)
            currentFragment = fragments[value]
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
                    ref?.set(mapOf("events" to emptyList<Event>()))
                } ?: println("Empty event list")
            }
        }
    private var docListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        widget_name.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.dialog_widget_name, null, false)
            view.widget_name.setText(widgetName)
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setView(view)
                .setPositiveButton("Save") { _, _ ->
                    view.widget_name.text.toString().takeIf { it.isNotBlank() }?.let {
                        getSharedPreferences(SHARED_PREFS_NAME, 0)
                            .edit()
                            .putString(SP_WIDGET_NAME, it)
                            .apply()
                        widgetName = it
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        spinner.apply {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    getSharedPreferences(SHARED_PREFS_NAME, 0)
                        .edit()
                        .putInt(SP_WIDGET_TYPE_INDEX, position)
                        .apply()
                    widgetTypeIndex = position
                }
            }

            adapter = ArrayAdapter<String>(
                this@MainActivity, R.layout.spinner_item, fragments.map(BaseFragment::displayName)
            ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        }

        getSharedPreferences(SHARED_PREFS_NAME, 0).apply {
            widgetName = getString(SP_WIDGET_NAME, null)
            widgetTypeIndex = getInt(SP_WIDGET_TYPE_INDEX, 0)
        }
    }

    fun sendEvent(type: String, message: String? = null) {
        serverRef.get().addOnSuccessListener {
            serverRef.update("events", it.getEvents().toMutableList().apply { add(Event(type, widgetName, message)) }).addOnSuccessListener {
                showToast(R.string.event_success)
            }.addOnFailureListener { e ->
                showToast(getString(R.string.event_failed, e))
            }
        }.addOnFailureListener { e ->
            showToast(getString(R.string.event_failed, e))
        }
    }

    private fun DocumentSnapshot?.getEvents(): List<Event> {
        @Suppress("UNCHECKED_CAST")
        return (this?.get("events") as? List<HashMap<String, Any>>).orEmpty().map { Event(it) }
    }
}
