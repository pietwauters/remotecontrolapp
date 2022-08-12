package com.example.remotecontrolfull

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.FragmentActivity
import app.com.kotlinapp.OnSwipeTouchListener
import com.example.remotecontrolfull.databinding.ActivityCyranoBinding
import com.example.remotecontrolfull.databinding.ActivityPenaltiesBinding
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
// Global constants
val UI_INPUT_CYRANO_NEXT = byteArrayOf(0x01,0x01,0x00,0x06)
val UI_INPUT_CYRANO_PREV = byteArrayOf(0x02,0x01,0x00,0x06)
val UI_INPUT_CYRANO_BEGIN = byteArrayOf(0x03,0x01,0x00,0x06)
val UI_INPUT_CYRANO_END = byteArrayOf(0x04,0x01,0x00,0x06)

val UI_SWAP_FENCERS = byteArrayOf(0x1a,0x00,0x00,0x06)
val UI_RESERVE_LEFT = byteArrayOf(0x1b,0x00,0x00,0x06)
val UI_RESERVE_RIGHT = byteArrayOf(0x1c,0x00,0x00,0x06)


class Cyrano : AppCompatActivity() {

    private lateinit var layout: ConstraintLayout
    private lateinit var binding: ActivityCyranoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cyrano)
        binding = ActivityCyranoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle("Cyrano");
        supportActionBar?.setHomeButtonEnabled(true);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        binding.btnPrevMatch.setOnClickListener {
            sendUDP(UI_INPUT_CYRANO_PREV)
        }
        binding.btnNextMatch.setOnClickListener {
            sendUDP(UI_INPUT_CYRANO_NEXT)
        }

        binding.btnBeginMatch.setOnClickListener {
            sendUDP(UI_INPUT_CYRANO_BEGIN)
        }
        binding.btnEndMatch.setOnClickListener {
            sendUDP(UI_INPUT_CYRANO_END)
        }

        layout = findViewById(R.id.CyranoLayout)
        layout.setOnTouchListener(object : OnSwipeTouchListener(this@Cyrano) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                //val intent = Intent(this@Cyrano, MainActivity::class.java)
                //startActivity(intent)
                finish()
            }
            override fun onSwipeRight() {
                super.onSwipeRight()
            }
            override fun onSwipeUp() {
                super.onSwipeUp()
            }
            override fun onSwipeDown() {
                super.onSwipeDown()
            }
        })
    }

}