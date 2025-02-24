package com.example.remotecontrolfull

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.remotecontrolfull.databinding.ActivityDeviceSettingsBasicBinding
import com.example.remotecontrolfull.databinding.ActivityExpertSettingsBinding

val UI_START_WIFI_PORTAL = byteArrayOf(0x00,0x00,0x02,0x06)
val UI_START_OTA_PORTAL = byteArrayOf(0x00,0x00,0x03,0x06)
val UI_CONNECT_TO_WIFI = byteArrayOf(0x00,0x00,0x04,0x06)
val UI_FULL_RESET = byteArrayOf(0x00,0x00,0x05,0x06)


class ExpertSettings : AppCompatActivity() {

    private lateinit var binding: ActivityExpertSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expert_settings)
        binding = ActivityExpertSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle("Expert Settings");
        binding.btnStartWifiPortal.setOnLongClickListener {
            sendUDP(UI_START_WIFI_PORTAL)
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("http://192.168.4.1")
            startActivity(openURL)
            true
        }

        binding.btnStartWifiPortal.setOnClickListener {
            Toast.makeText(
                applicationContext,
                "Do a LONG Press to start the Wifi Portal!",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.btnConnectWiFi.setOnLongClickListener {
            sendUDP(UI_CONNECT_TO_WIFI)
            true
        }
        binding.btnStartOTAPortal.setOnLongClickListener {
            sendUDP(UI_START_OTA_PORTAL)
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("http://192.168.4.1/update")
            startActivity(openURL)
            true
        }

        binding.btnStartOTAPortal.setOnClickListener {
            Toast.makeText(
                applicationContext,
                "Do a LONG Press to start the OTA Update Portal!",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnResetDevice.setOnClickListener {
            Toast.makeText(
                applicationContext,
                "Do a LONG Press to perform a full reset!",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.btnResetDevice.setOnLongClickListener {
            sendUDP(UI_FULL_RESET)
            true
        }

    }
}