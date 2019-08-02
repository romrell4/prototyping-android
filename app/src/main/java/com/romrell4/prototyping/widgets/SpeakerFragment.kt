package com.romrell4.prototyping.widgets

import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.romrell4.prototyping.Event
import com.romrell4.prototyping.R
import java.util.*

class SpeakerFragment : BaseFragment() {
    override val displayName = "Speaker"
    private lateinit var tts: TextToSpeech

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_speaker, container, false).apply {
            tts = TextToSpeech(requireContext()) {
                tts.language = Locale.US
            }
        }
    }

    override fun handleEvent(event: Event) {
        when (event.type) {
            "SPEAK" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts.speak(event.message, TextToSpeech.QUEUE_FLUSH, null, null)
                } else {
                    tts.speak(event.message, TextToSpeech.QUEUE_FLUSH, null)
                }
            }
            else -> println("Unhandled event: $event")
        }
    }
}