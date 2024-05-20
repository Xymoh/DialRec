package com.example.dialrec

import android.content.Intent
import android.os.Bundle
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    companion object {
        private const val REQUEST_CODE_SET_DEFAULT_DIALER = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, DialerActivity::class.java))
        checkDefaultDialer()
    }

    private fun checkDefaultDialer() {
        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        if (telecomManager.defaultDialerPackage != packageName) {
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
            startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER)
        } else {
            Toast.makeText(this, "Already default dialer", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SET_DEFAULT_DIALER) {
            checkSetDefaultDialerResult(resultCode)
        }
    }

    private fun checkSetDefaultDialerResult(resultCode: Int) {
        val message = when (resultCode) {
            RESULT_OK -> "User accepted request to become default dialer"
            RESULT_CANCELED -> "User declined request to become default dialer"
            else -> "Unexpected result code $resultCode"
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}