package com.romrell4.prototyping.widgets

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.romrell4.prototyping.Event
import com.romrell4.prototyping.R
import java.util.*

class AudioFragment : BaseFragment() {
    override val displayName = "Audio"
    private lateinit var tts: TextToSpeech

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_audio, container, false).apply {
            tts = TextToSpeech(requireContext()) {
                tts.language = Locale.US
            }
        }
    }

    override fun handleEvent(event: Event) {
        tts.speak(event.message, TextToSpeech.QUEUE_FLUSH, null)
    }
}