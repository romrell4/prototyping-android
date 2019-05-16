package com.romrell4.prototyping

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.romrell4.prototyping.widgets.AudioFragment
import com.romrell4.prototyping.widgets.BaseFragment
import com.romrell4.prototyping.widgets.DisplayFragment

class MainActivity : AppCompatActivity() {

    private val fragments = listOf(
        DisplayFragment(),
        AudioFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_menu, menu)
        (menu.findItem(R.id.switch_widget_type).actionView as? Spinner)?.apply {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    setFragment(position)
                }

            }
            adapter = ArrayAdapter<String>(
                this@MainActivity, R.layout.spinner_item, fragments.map(BaseFragment::displayName)
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun setFragment(index: Int) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragments[index])
            .commit()
    }
}
