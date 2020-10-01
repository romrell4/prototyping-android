package com.romrell4.prototyping.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.romrell4.prototyping.Event
import com.romrell4.prototyping.R
import kotlinx.android.synthetic.main.fragment_slider.*
import kotlinx.android.synthetic.main.fragment_slider.view.*

class SliderFragment : BaseFragment() {
    override val displayName = "Slider"
    override val widgetType = "slider"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_slider, container, false).apply {
            text_view.text = getString(R.string.knob_progress_text, 0)
            seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    text_view.text = getString(R.string.knob_progress_text, progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    sendEvent("PROGRESS_UPDATED", seekBar.progress.toString())
                }
            })
        }
    }

    override fun handleEvent(event: Event) {
        when(event.type) {
            "UPDATE_PROGRESS" -> {
                try {
                    event.message?.toInt()?.let { seek_bar.progress = it }
                } catch (e: NumberFormatException) {
                    println("Invalid number format: ${event.message}")
                }
            }
            else -> println("Unhandled event: $event")
        }
    }
}