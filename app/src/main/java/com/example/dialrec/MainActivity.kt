package com.example.dialrec

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dialrec.ui.theme.DialRecTheme
import android.telecom.TelecomManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            // Permission has been granted. Continue with your action after permission request.
            // For example, if you requested CALL_PHONE permission, you can proceed with the call:
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
        } else {
            // Explain to the user that the feature is unavailable because the features requires a permission that the user has denied.
            // You can use a Toast, Snackbar, or a dialog to show this message. Here's an example with a Toast:
            Toast.makeText(this, "Call feature requires phone call permission", Toast.LENGTH_LONG).show()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DialRecTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        var phoneNumber by remember { mutableStateOf("") }

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = { dialPhoneNumber(phoneNumber) }) {
                            Text("Dial")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = {
                            if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                callPhoneNumber(phoneNumber)
                            } else {
                                requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                            }
                        }) {
                            Text("Call")
                        }
                    }
                }
            }
        }
        registerPhoneAccount()
    }

    // Add this line to store the PhoneAccountHandle
    private lateinit var phoneAccountHandle: PhoneAccountHandle

    private fun dialPhoneNumber(phoneNumber: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(dialIntent)
    }

    private fun callPhoneNumber(phoneNumber: String) {
        val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        val uri = Uri.fromParts("tel", phoneNumber, null)
        val extras = Bundle()
        extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            telecomManager.placeCall(uri, extras)
        } else {
            // Request the necessary permission
            requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }

    private fun registerPhoneAccount() {
        // Get the TelecomManager from the system.
        val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager

        // Create a PhoneAccountHandle with the ComponentName of your ConnectionService and an identifier for the account.
        val componentName = ComponentName(this, MyConnectionService::class.java)
        // Store the PhoneAccountHandle in the property
        phoneAccountHandle = PhoneAccountHandle(componentName, "my_phone_account")

        // Create a PhoneAccount and register it with the TelecomManager.
        val phoneAccount = PhoneAccount.builder(phoneAccountHandle, "My Phone Account")
            .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED)
            .build()

        telecomManager.registerPhoneAccount(phoneAccount)
    }
}