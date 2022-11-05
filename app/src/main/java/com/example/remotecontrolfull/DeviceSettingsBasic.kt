package com.example.remotecontrolfull

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ScrollView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import app.com.kotlinapp.OnSwipeTouchListener
import com.example.remotecontrolfull.databinding.ActivityCyranoBinding
import com.example.remotecontrolfull.databinding.ActivityDeviceSettingsBasicBinding
import android.content.Intent
import android.net.Uri

val UI_START_WIFI_PORTAL = byteArrayOf(0x00,0x00,0x02,0x06)

class DeviceSettingsBasic : AppCompatActivity() {
    private lateinit var layout: ScrollView
    private lateinit var binding: ActivityDeviceSettingsBasicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_settings_basic)

        binding = ActivityDeviceSettingsBasicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle("Basic Device Settings");
        //supportActionBar?.setHomeButtonEnabled(true);
        //supportActionBar?.setDisplayHomeAsUpEnabled(true);
        binding.btnCycleIntensity.setOnClickListener {
            sendUDP(UI_INPUT_CYCLE_BRIGHTNESS)
        }
        binding.btnCycleWeapon.setOnClickListener {
            sendUDP(UI_INPUT_CYCLE_WEAPON)
        }
        binding.btnCycleMatchType.setOnClickListener {
            sendUDP(UI_INPUT_ROUND)
        }

        binding.btnStartWifiPortal.setOnLongClickListener {
            sendUDP(UI_START_WIFI_PORTAL)
            val openURL = Intent(android.content.Intent.ACTION_VIEW)
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
        layout = findViewById(R.id.DeviceSettingsBasicLayout)
        layout.setOnTouchListener(object : OnSwipeTouchListener(this@DeviceSettingsBasic) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
            }
            override fun onSwipeRight() {
                super.onSwipeRight()
            }
            override fun onSwipeUp() {
                super.onSwipeUp()
            }
            override fun onSwipeDown() {
                super.onSwipeDown()
                finish()
            }
        })
    }
}