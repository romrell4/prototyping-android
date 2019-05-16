package com.romrell4.prototyping.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.romrell4.prototyping.R

class AudioFragment : BaseFragment() {
    override val displayName = "Audio"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_audio, container, false)
    }
}