package com.romrell4.prototyping.widgets

import androidx.fragment.app.Fragment
import com.romrell4.prototyping.Event

abstract class BaseFragment: Fragment() {
    abstract val displayName: String

    abstract fun handleEvent(event: Event)
}