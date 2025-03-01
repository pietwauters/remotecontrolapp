package com.example.remotecontrolfull

import android.content.Intent
import android.content.Context
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.FragmentActivity
import app.com.kotlinapp.OnSwipeTouchListener

import com.example.remotecontrolfull.databinding.ActivityPenaltiesBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class Penalties : AppCompatActivity() {

    private lateinit var layout: ConstraintLayout
    lateinit var binding: ActivityPenaltiesBinding
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
    fun vibrate(duration : Long){
        val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE) )
        }else{
            @Suppress("DEPRECATION")
            vib.vibrate(duration)
        }
    }
    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    public @Subscribe(sticky = true,threadMode = ThreadMode.MAIN_ORDERED)
    open fun onU2FEvent(event: U2FEvent) {
        if(event.YellowPCardLeft != "0")
        {
            binding.textViewYellowPCardLeft.visibility = View.VISIBLE
        }
        else
            binding.textViewYellowPCardLeft.visibility = View.INVISIBLE
        if(event.YellowPCardRight != "0")
        {
            binding.textViewYellowPCardRight.visibility = View.VISIBLE
        }
        else
            binding.textViewYellowPCardRight.visibility = View.INVISIBLE
        if(event.RedPCardLeft != "0")
        {
            binding.textViewRedPCardLeft.visibility = View.VISIBLE
        }
        else
            binding.textViewRedPCardLeft.visibility = View.INVISIBLE
        if(event.RedPCardRight != "0")
        {
            binding.textViewRedPCardRight.visibility = View.VISIBLE
        }
        else
            binding.textViewRedPCardRight.visibility = View.INVISIBLE
        if(event.BlackPCardLeft != "0")
        {
            binding.textViewBlackPCardLeft.visibility = View.VISIBLE
        }
        else
            binding.textViewBlackPCardLeft.visibility = View.INVISIBLE
        if(event.BlackPCardRight != "0")
        {
            binding.textViewBlackPCardRight.visibility = View.VISIBLE
        }
        else
            binding.textViewBlackPCardRight.visibility = View.INVISIBLE

    }

        // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    public @Subscribe(sticky = true,threadMode = ThreadMode.MAIN_ORDERED)
    open fun onStatusEvent(event: StatusEvent) {

        // Update the status (deal only with score for the main activity
        if(event.YellowCardLeft != " 0")
        {
            binding.textViewYellowCardLeft.text = event.YellowCardLeft.trim()
            binding.textViewYellowCardLeft.visibility = View.VISIBLE
        }
        else
            binding.textViewYellowCardLeft.visibility = View.INVISIBLE
        if(event.YellowCardRight != " 0")
        {
            binding.textViewYellowCardRight.text = event.YellowCardRight.trim()
            binding.textViewYellowCardRight.visibility = View.VISIBLE
        }
        else
            binding.textViewYellowCardRight.visibility = View.INVISIBLE
        if(event.RedCardLeft != " 0")
        {
            binding.textViewRedCardLeft.text = event.RedCardLeft.trim()
            binding.textViewRedCardLeft.visibility = View.VISIBLE
        }
        else
            binding.textViewRedCardLeft.visibility = View.INVISIBLE
        if(event.RedCardRight != " 0")
        {
            binding.textViewRedCardRight.text = event.RedCardRight.trim()
            binding.textViewRedCardRight.visibility = View.VISIBLE
        }
        else
            binding.textViewRedCardRight.visibility = View.INVISIBLE
        if(event.BlackCardLeft != "0")
        {
            binding.textViewBlackCardLeft.text = event.BlackCardLeft
            binding.textViewBlackCardLeft.visibility = View.VISIBLE
        }
        else
            binding.textViewBlackCardLeft.visibility = View.INVISIBLE
        if(event.BlackCardRight != "0")
        {
            binding.textViewBlackCardRight.text = event.BlackCardRight
            binding.textViewBlackCardRight.visibility = View.VISIBLE
        }
        else
            binding.textViewBlackCardRight.visibility = View.INVISIBLE

        if(event.Prio == "0"){
            binding.textViewPrioLeft.visibility = View.INVISIBLE
            binding.textViewPrioRight.visibility = View.INVISIBLE
        }
        else{
            if(event.Prio == "1"){
                binding.textViewPrioLeft.visibility = View.INVISIBLE
                binding.textViewPrioRight.visibility = View.VISIBLE
            }
            else{
                binding.textViewPrioLeft.visibility = View.VISIBLE
                binding.textViewPrioRight.visibility = View.INVISIBLE
            }
        }

    }
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
        binding.btnIncrBlackCardLeft.setOnClickListener {
            sendUDP(UI_INPUT_BLACK_CARD_LEFT)
        }



        binding.btnIncrYellowCardRight.setOnClickListener {
            sendUDP(UI_INPUT_YELLOW_CARD_RIGHT)
        }
        binding.btnIncrRedCardRight.setOnClickListener {
            sendUDP(UI_INPUT_RED_CARD_RIGHT)
        }
        binding.btnIncrBlackCardRight.setOnClickListener {
            sendUDP(UI_INPUT_BLACK_CARD_RIGHT)
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
        // From here we have the reverse functions for the cards
        binding.btnIncrYellowCardLeft.setOnLongClickListener {
            sendUDP(UI_INPUT_YELLOW_CARD_LEFT_DECR)
            vibrate(150)
            true
        }

        binding.btnIncrYellowCardRight.setOnLongClickListener {
            sendUDP(UI_INPUT_YELLOW_CARD_RIGHT_DECR)
            vibrate(150)
            true
        }

        binding.btnIncrRedCardLeft.setOnLongClickListener {
            sendUDP(UI_INPUT_RED_CARD_LEFT_DECR)
            vibrate(150)
            true
        }

        binding.btnIncrRedCardRight.setOnLongClickListener {
            sendUDP(UI_INPUT_RED_CARD_RIGHT_DECR)
            vibrate(150)
            true
        }
        binding.btnIncrBlackCardLeft.setOnLongClickListener {
            sendUDP(UI_INPUT_BLACK_CARD_LEFT_DECR)
            vibrate(150)
            true
        }
        binding.btnIncrBlackCardRight.setOnLongClickListener {
            sendUDP(UI_INPUT_BLACK_CARD_RIGHT_DECR)
            vibrate(150)
            true
        }

        binding.btnUnwillingnessToFight.setOnLongClickListener {
            sendUDP(UI_INPUT_P_CARD_UNDO)
            vibrate(150)
            true
        }
    }

}