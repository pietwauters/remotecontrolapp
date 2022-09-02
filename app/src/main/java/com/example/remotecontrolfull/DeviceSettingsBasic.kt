package com.example.remotecontrolfull

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import app.com.kotlinapp.OnSwipeTouchListener
import com.example.remotecontrolfull.databinding.ActivityCyranoBinding
import com.example.remotecontrolfull.databinding.ActivityDeviceSettingsBasicBinding

class DeviceSettingsBasic : AppCompatActivity() {
    private lateinit var layout: ConstraintLayout
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