package com.romrell4.prototyping.widgets

import androidx.fragment.app.Fragment
import com.romrell4.prototyping.Event
import com.romrell4.prototyping.MainActivity

abstract class BaseFragment: Fragment() {
    abstract val displayName: String
    abstract val widgetType: String

    abstract fun handleEvent(event: Event)

    protected fun sendEvent(type: String, message: String? = null) {
        (activity as? MainActivity)?.sendEvent(type, message)
    }
}