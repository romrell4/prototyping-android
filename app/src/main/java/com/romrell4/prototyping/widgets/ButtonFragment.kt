package com.romrell4.prototyping.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.romrell4.prototyping.Event
import com.romrell4.prototyping.R
import com.romrell4.prototyping.support.hideKeyboard
import kotlinx.android.synthetic.main.fragment_button.view.*

class ButtonFragment : BaseFragment() {
    override val displayName = "Button"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_button, container, false).apply {
            button.setOnClickListener {
                hideKeyboard()
                sendEvent(edit_text.text.toString())
            }
        }
    }

    override fun handleEvent(event: Event) {

    }
}
