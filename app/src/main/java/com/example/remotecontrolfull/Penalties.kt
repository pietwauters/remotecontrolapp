package com.example.remotecontrolfull

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.FragmentActivity
import app.com.kotlinapp.OnSwipeTouchListener
import com.example.remotecontrolfull.databinding.ActivityPenaltiesBinding
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class Penalties : AppCompatActivity() {

    private lateinit var layout: ConstraintLayout
    lateinit var binding: ActivityPenaltiesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_penalties)
        binding = ActivityPenaltiesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle("Cards, Priority, UW2F");
        //supportActionBar?.hide()

        layout = findViewById(R.id.PenaltiesLayout)
        layout.setOnTouchListener(object : OnSwipeTouchListener(this@Penalties) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
            }
            override fun onSwipeRight() {
                super.onSwipeRight()
                //val intent = Intent(this@Penalties, MainActivity::class.java);
                //startActivity(intent)
                finish()
            }
            override fun onSwipeUp() {
                super.onSwipeUp()
            }
            override fun onSwipeDown() {
                super.onSwipeDown()
            }
        })


        binding.btnIncrYellowCardLeft.setOnClickListener {
            sendUDP(UI_INPUT_YELLOW_CARD_LEFT)
        }
        binding.btnIncrRedCardLeft.setOnClickListener {
            sendUDP(UI_INPUT_RED_CARD_LEFT)
        }

        binding.btnIncrYellowCardRight.setOnClickListener {
            sendUDP(UI_INPUT_YELLOW_CARD_RIGHT)
        }
        binding.btnIncrRedCardRight.setOnClickListener {
            sendUDP(UI_INPUT_RED_CARD_RIGHT)
        }
        binding.btnUnwillingnessToFight.setOnClickListener {
            sendUDP(UI_INPUT_P_CARD)
        }
        binding.btnPriority.setOnClickListener {
            sendUDP(UI_INPUT_PRIO)
        }
        binding.btnUndoResetUW2FTimer.setOnClickListener {
            sendUDP(UI_INPUT_RESTORE_UW2F_TIMER)
        }
    }

}