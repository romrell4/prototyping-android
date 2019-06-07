package com.romrell4.prototyping

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.romrell4.prototyping.widgets.AudioFragment
import com.romrell4.prototyping.widgets.BaseFragment
import com.romrell4.prototyping.widgets.ButtonFragment
import com.romrell4.prototyping.widgets.DisplayFragment
import kotlinx.android.synthetic.main.activity_main.*

private const val SHARED_PREFS_NAME = "com.romrell4.prototyping"
private const val SP_WIDGET_NAME = "widget_name"

class MainActivity : AppCompatActivity() {

    private val fragments = listOf(
        DisplayFragment(),
        AudioFragment(),
        ButtonFragment()
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
    private var widgetName: String = ""
        set(value) {
            field = value
            widget_name.setText(value)
            ref = systemsRef.document(value)
        }
    private var ref: DocumentReference? = null
        set(value) {
            field = value
            field?.addSnapshotListener { querySnapshot, _ ->
                querySnapshot.getEvents().takeIf { it.isNotEmpty() }?.let {
                    //Handle each event in turn (should be sorted)
                    querySnapshot.getEvents().forEach { currentFragment.handleEvent(it) }

                    //Reset the events in the queue
                    ref?.set(mapOf("events" to emptyList<Event>()))
                } ?: println("Empty event list")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        widget_name.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && v is EditText && v.text.isNotBlank()) {
                v.text.toString().also {
                    getSharedPreferences(SHARED_PREFS_NAME, 0)
                        .edit()
                        .putString(SP_WIDGET_NAME, it)
                        .apply()
                    widgetName = it
                }
            }
            //Allow the button to still dismiss the keyboard
            false
        }

        spinner.apply {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    currentFragment = fragments[position]
                }
            }

            adapter = ArrayAdapter<String>(
                this@MainActivity, R.layout.spinner_item, fragments.map(BaseFragment::displayName)
            ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        }

        getSharedPreferences(SHARED_PREFS_NAME, 0).getString(SP_WIDGET_NAME, null)?.also {
            widgetName = it
        }
    }

    fun sendEvent(message: String) {
        serverRef.get().addOnSuccessListener {
            serverRef.update("events", it.getEvents().toMutableList().apply { add(Event(widgetName, message)) })
        }.addOnFailureListener {
            print(it)
        }
    }

    private fun DocumentSnapshot?.getEvents(): List<Event> {
        return (this?.get("events") as? List<HashMap<String, Any>>).orEmpty().map { Event(it) }
    }
}
