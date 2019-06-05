package com.romrell4.prototyping

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.romrell4.prototyping.widgets.AudioFragment
import com.romrell4.prototyping.widgets.BaseFragment
import com.romrell4.prototyping.widgets.DisplayFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val fragments = listOf(
        DisplayFragment(),
        AudioFragment()
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
                ref = systemsRef.document(v.text.toString())
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
    }

    private fun DocumentSnapshot?.getEvents(): List<Event> {
        return (this?.get("events") as? List<HashMap<String, Any>>).orEmpty().map { Event(it) }
    }
}
