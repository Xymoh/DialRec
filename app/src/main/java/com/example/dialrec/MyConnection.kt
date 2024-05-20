package com.example.dialrec

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telecom.Connection
import android.telecom.DisconnectCause
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MyConnection(private val context: Context, private val onStateChanged: (Int) -> Unit) : Connection() {
    private var isReady = false

    init {
        // Use a BroadcastReceiver to set isReady to true when the LocalBroadcastManager is ready
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                isReady = true
            }
        }

        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, IntentFilter("LocalBroadcastManagerReady"))
    }

    override fun onAnswer() {
        setDialing()
        // Here you would typically start the process of connecting the call.
    }

    override fun onDisconnect() {
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
        // Here you would typically tear down the call.
    }

    override fun onHold() {
        setOnHold()
        // Here you would typically put the call on hold.
    }

    override fun onUnhold() {
        setActive()
        // Here you would typically take the call off hold.
    }

    override fun onStateChanged(state: Int) {
        super.onStateChanged(state)
        if (isReady) {
            try {
                onStateChanged(state)
            } catch (e: Exception) {
                Log.e("MyConnection", "Error in onStateChanged", e)
            }
        }
    }
}