package com.example.dialrec

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.telecom.Connection
import android.telecom.ConnectionService
import android.telecom.PhoneAccountHandle
import android.telecom.ConnectionRequest
import android.telecom.TelecomManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MyConnectionService : ConnectionService() {
    companion object {
        var currentConnection: Connection? = null
    }

    override fun onCreate() {
        super.onCreate()

        // Send a broadcast to indicate that the LocalBroadcastManager is ready
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("LocalBroadcastManagerReady"))
    }

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle,
        request: ConnectionRequest
    ): Connection {
        val connection = MyConnection(this) { state ->
            // Update the call status based on the new state
            val status = when (state) {
                Connection.STATE_RINGING -> "Ringing..."
                Connection.STATE_DIALING -> "Dialing..."
                Connection.STATE_ACTIVE -> "Call Active"
                else -> "Call Ended"
            }

            // Use LocalBroadcastManager to send the status to CallActivity
            val intent = Intent("CallStatusChanged")
            intent.putExtra("status", status)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }

        connection.setAddress(request.address, TelecomManager.PRESENTATION_ALLOWED)

        Handler(Looper.getMainLooper()).post {
            connection.setInitializing()
            connection.setActive()
        }

        currentConnection = connection

        val intent = Intent(this, CallActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

        return connection
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val connection = MyConnection(this) { state ->
            // Update the call status based on the new state
            val status = when (state) {
                Connection.STATE_RINGING -> "Ringing..."
                Connection.STATE_DIALING -> "Dialing..."
                Connection.STATE_ACTIVE -> "Call Active"
                else -> "Call Ended"
            }

            // Use LocalBroadcastManager to send the status to CallActivity
            val intent = Intent("CallStatusChanged")
            intent.putExtra("status", status)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }

        connection.setAddress(request?.address, TelecomManager.PRESENTATION_ALLOWED)
        connection.setRinging()
        return connection
    }
}

