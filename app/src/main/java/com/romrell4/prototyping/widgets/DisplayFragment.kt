package com.romrell4.prototyping.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.romrell4.prototyping.Event
import com.romrell4.prototyping.R
import kotlinx.android.synthetic.main.fragment_display.*

class DisplayFragment : BaseFragment() {
    override val displayName = "Display"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_display, container, false)
    }

    override fun handleEvent(event: Event) {
        text_view.text = event.message
    }
}
