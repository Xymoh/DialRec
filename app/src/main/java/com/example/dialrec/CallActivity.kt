package com.example.dialrec

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telecom.Connection
import android.widget.Button
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class CallActivity : AppCompatActivity() {
    private lateinit var callStatus: TextView
    private lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        callStatus = findViewById(R.id.callStatus)
        val hangUpButton = findViewById<Button>(R.id.hangUpButton)

        hangUpButton.setOnClickListener {
            // End the call when the button is clicked
            MyConnectionService.currentConnection?.onDisconnect()
            finish()
        }

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "CallStatusChanged") {
                    callStatus.text = intent.getStringExtra("status")
                }
            }
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter("CallStatusChanged"))
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }
}