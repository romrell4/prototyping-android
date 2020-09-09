package com.romrell4.prototyping.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.romrell4.prototyping.Event
import com.romrell4.prototyping.R
import kotlinx.android.synthetic.main.fragment_button.*
import kotlinx.android.synthetic.main.fragment_button.view.*

class ButtonFragment : BaseFragment() {
    override val displayName = "Button"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_button, container, false).apply {
            button.setOnClickListener {
                sendEvent("BUTTON_TAPPED")
            }
        }
    }

    override fun handleEvent(event: Event) {
        when (event.type) {
            "UPDATE_BUTTON_TEXT" -> button.text = event.message
            else -> println("Unhandled event: $event")
        }
    }
}
