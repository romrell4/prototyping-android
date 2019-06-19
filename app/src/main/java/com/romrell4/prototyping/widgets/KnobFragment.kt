package com.romrell4.prototyping.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.romrell4.prototyping.Event
import com.romrell4.prototyping.R
import kotlinx.android.synthetic.main.fragment_knob.*
import kotlinx.android.synthetic.main.fragment_knob.view.*
import java.lang.NumberFormatException

class KnobFragment : BaseFragment() {
    override val displayName = "Knob"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_knob, container, false).apply {
            seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    sendEvent("KNOB_PROGRESS_UPDATED", seekBar.progress.toString())
                }
            })
        }
    }

    override fun handleEvent(event: Event) {
        when(event.type) {
            "UPDATE_KNOB_PROGRESS" -> try {
                event.message?.toInt()?.let { seek_bar.progress = it }
            } catch (e: NumberFormatException) {
                println("Invalid number format: ${event.message}")
            }
            else -> println("Unhandled event: $event")
        }
    }
}