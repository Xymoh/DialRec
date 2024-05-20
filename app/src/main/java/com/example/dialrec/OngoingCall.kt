package com.example.dialrec

import android.telecom.Call
import android.telecom.VideoProfile
import io.reactivex.rxjava3.subjects.BehaviorSubject

class OngoingCall {
    private val callback: Any = object : Call.Callback() {
        override fun onStateChanged(call: Call, newState: Int) {
            super.onStateChanged(call, newState)
            state.onNext(newState)
        }
    }

    fun setCall(value: Call?) {
        if (call != null) {
            call!!.unregisterCallback(callback as Call.Callback)
        }
        if (value != null) {
            value.registerCallback(callback as Call.Callback)
            state.onNext(value.state)
        }
        call = value
    }

    fun answer() {
        assert(call != null)
        call!!.answer(VideoProfile.STATE_AUDIO_ONLY)
    }

    fun hangup() {
        assert(call != null)
        call!!.disconnect()
    }

    companion object {
        var state: BehaviorSubject<Int> = BehaviorSubject.create()
        private var call: Call? = null
    }
}