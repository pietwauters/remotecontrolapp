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
        binding.switchMode.setChecked(MainActivity.StoredValues.UIMode)
        binding.switchCableTest.setChecked(false)

        binding.btnCycleIntensity.setOnClickListener {
            sendUDP(UI_INPUT_CYCLE_BRIGHTNESS)
        }
        binding.btnCycleWeapon.setOnClickListener {
            sendUDP(UI_INPUT_CYCLE_WEAPON)
        }
        binding.btnCycleMatchType.setOnClickListener {
            sendUDP(UI_INPUT_ROUND)
        }
        binding.switchMode.setOnCheckedChangeListener { _, isChecked ->
            // do whatever you need to do when the switch is toggled here
            MainActivity.StoredValues.UIMode = isChecked
        }
        binding.switchCableTest.setOnCheckedChangeListener { _, isChecked ->
            // do whatever you need to do when the switch is toggled here
            if(isChecked) sendUDP(UI_CABLETEST_ON)
            else sendUDP(UI_CABLETEST_OFF)
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
                val intent = Intent(this@DeviceSettingsBasic, ExpertSettings::class.java)
                startActivity(intent)
            }
            override fun onSwipeDown() {
                super.onSwipeDown()
                finish()
            }
        })
    }
}