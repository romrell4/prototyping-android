package com.romrell4.prototyping.widgets

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.romrell4.prototyping.Event
import com.romrell4.prototyping.R
import kotlinx.android.synthetic.main.fragment_text.*
import kotlinx.android.synthetic.main.fragment_text.view.edit_text
import java.util.*
import kotlin.concurrent.schedule

class TextFragment : BaseFragment() {
    override val displayName = "Text"

    private var timer: Timer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_text, container, false).apply {
            edit_text.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    sendTextUpdated()
                }
            }
            edit_text.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    timer?.cancel()
                    timer = Timer().apply {
                        schedule(750) {
                            sendTextUpdated()
                        }
                    }
                }
            })
        }
    }

    override fun handleEvent(event: Event) {
        when (event.type) {
            "UPDATE_TEXT" -> edit_text.setText(event.message)
            else -> println("Unhandled event: $event")
        }
    }

    private fun sendTextUpdated() {
        sendEvent("TEXT_UPDATED", edit_text.text.toString())
    }
}
