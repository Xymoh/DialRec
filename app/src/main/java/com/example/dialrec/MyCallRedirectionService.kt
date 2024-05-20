package com.example.dialrec

import android.annotation.SuppressLint
import android.net.Uri
import android.telecom.CallRedirectionService
import android.telecom.PhoneAccountHandle
import android.util.Log

class MyCallRedirectionService : CallRedirectionService() {

    @SuppressLint("LogNotTimber")
    override fun onPlaceCall(handle: Uri, initialPhoneAccount: PhoneAccountHandle, allowInteractiveResponse: Boolean) {
        Log.d("AppLog", "handle:$handle , initialPhoneAccount:$initialPhoneAccount , allowInteractiveResponse:$allowInteractiveResponse")
        cancelCall()
    }
}