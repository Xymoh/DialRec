package com.example.dialrec

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telecom.TelecomManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.dialrec.databinding.ActivityDialerBinding
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.widget.Toast
import androidx.core.app.ActivityCompat

class DialerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDialerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialer)
        binding = ActivityDialerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.data?.let {
            binding.phoneNumberInput.setText(it.schemeSpecificPart)
        }
    }

    override fun onStart() {
        super.onStart()
        offerReplacingDefaultDialer()
        binding.phoneNumberInput.setOnEditorActionListener { _, _, _ ->
            makeCall()
            true
        }
    }

    private fun makeCall() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED) {
            val uri = Uri.parse("tel:" + binding.phoneNumberInput.text.toString().trim())
            startActivity(Intent(Intent.ACTION_CALL, uri))
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSION)
        }
    }

    private fun offerReplacingDefaultDialer() {
        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        if (packageName != telecomManager.defaultDialerPackage) {
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
                makeCall()
            } else {
                Toast.makeText(this, "Permissions are required to make a call.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val REQUEST_PERMISSION = 0
    }
}