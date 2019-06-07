package com.romrell4.prototyping.widgets

import androidx.fragment.app.Fragment
import com.romrell4.prototyping.Event
import com.romrell4.prototyping.MainActivity

abstract class BaseFragment: Fragment() {
    abstract val displayName: String

    abstract fun handleEvent(event: Event)

    protected fun sendEvent(message: String) {
        (activity as? MainActivity)?.sendEvent(message)
    }
}