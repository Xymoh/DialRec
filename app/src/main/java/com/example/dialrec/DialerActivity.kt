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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PERMISSION_GRANTED) {
            val uri = Uri.parse("tel:" + binding.phoneNumberInput.text.toString().trim())
            startActivity(Intent(Intent.ACTION_CALL, uri))
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
        if (requestCode == REQUEST_PERMISSION && grantResults.contains(PERMISSION_GRANTED)) {
            makeCall()
        }
    }

    companion object {
        const val REQUEST_PERMISSION = 0
    }
}