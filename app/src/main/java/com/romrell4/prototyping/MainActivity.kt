package com.romrell4.prototyping

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.romrell4.prototyping.widgets.DisplayFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, DisplayFragment())
            .commit()
    }
}
