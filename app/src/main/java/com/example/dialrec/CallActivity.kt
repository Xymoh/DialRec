package com.example.dialrec

import android.annotation.SuppressLint
import android.content.Context
import android.telecom.Call
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.dialrec.databinding.ActivityCallBinding
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import android.content.Intent
import android.os.Bundle

class CallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCallBinding
    private lateinit var disposables: CompositeDisposable
    private lateinit var number: String
    private lateinit var ongoingCall: OngoingCall
    private lateinit var callRecorder: CallRecorder
    private lateinit var outputFile: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ongoingCall = OngoingCall()
        disposables = CompositeDisposable()
        callRecorder = CallRecorder(this)

        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

        number = intent.data?.schemeSpecificPart ?: ""

        binding.answer.setOnClickListener { onAnswerClicked() }
        binding.hangup.setOnClickListener { onHangupClicked() }
    }

    fun onAnswerClicked() {
        ongoingCall.answer()
    }

    fun onHangupClicked() {
        ongoingCall.hangup()
    }

    override fun onStart() {
        super.onStart()

        callRecorder.startRecording()

        updateUi(-1)
        disposables.add(
                OngoingCall.state.subscribe { state ->
                    updateUi(state)
                })

        disposables.add(
                OngoingCall.state.filter { state ->
                    state == Call.STATE_DISCONNECTED
                }
                        .delay(1, TimeUnit.SECONDS)
                        .firstElement()
                        .subscribe { _ ->
                            finish()
                        })
    }

    override fun onStop() {
        super.onStop()
        callRecorder.stopRecording()
        disposables.clear()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi(state: Int) {
        binding.callInfo.text = "${Constants.asString(state)}\n$number"

        binding.answer.visibility = if (state != Call.STATE_RINGING) View.GONE else View.VISIBLE

        binding.hangup.visibility = if (listOf(
                        Call.STATE_DIALING,
                        Call.STATE_RINGING,
                        Call.STATE_ACTIVE).contains(state)) View.VISIBLE else View.GONE
    }

    companion object {
        fun start(context: Context, call: Call) {
            val intent = Intent(context, CallActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setData(call.details.handle)
            context.startActivity(intent)
        }
    }
}