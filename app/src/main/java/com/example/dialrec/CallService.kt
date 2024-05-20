package com.example.dialrec

import android.telecom.Call
import android.telecom.InCallService

class CallService : InCallService() {
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        OngoingCall().setCall(call)
        CallActivity.start(this, call)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        OngoingCall().setCall(null)
    }
}