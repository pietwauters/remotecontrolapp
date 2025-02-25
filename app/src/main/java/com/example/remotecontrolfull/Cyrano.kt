package com.example.remotecontrolfull

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.VIBRATOR_SERVICE
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.FragmentActivity
import app.com.kotlinapp.OnSwipeTouchListener
import com.example.remotecontrolfull.databinding.ActivityCyranoBinding
import com.example.remotecontrolfull.databinding.ActivityPenaltiesBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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
    companion object StoredValues{
        var NameLeft = "Left"
        var NameRight = "Right"
        var ScoreLeft = ""
        var ScoreRight = "";
        var CyranoStatus = "";
    }

    private lateinit var layout: ConstraintLayout
    private lateinit var binding: ActivityCyranoBinding
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
    public @Subscribe(sticky = true,threadMode = ThreadMode.MAIN_ORDERED)
    open fun onCompetitiorEvent(event: CompetitorEvent) {
        if(event.side == SideOfEvent.Left)
        {
            StoredValues.NameLeft = event.CompetitorName
            binding.textViewNameLeft.text= event.CompetitorName
        }

        else
        {
            StoredValues.NameRight = event.CompetitorName
            binding.textViewNameRight.text = event.CompetitorName
        }


    }
    public @Subscribe(sticky = true,threadMode = ThreadMode.MAIN_ORDERED)
    open fun onStatusEvent(event: StatusEvent) {
        binding.textViewScoreLeftCyrano.text = event.ScoreLeft
        binding.textViewScoreRightCyrano.text = event.ScoreRight
        StoredValues.ScoreLeft = event.ScoreLeft
        StoredValues.ScoreRight = event.ScoreRight
    }
    public @Subscribe(sticky = true,threadMode = ThreadMode.MAIN_ORDERED)
    open fun onExtraInfoEvent(event: ExtraInfoEvent) {
        binding.textViewCyranoStatus.text = event.CyranoStatus
        StoredValues.CyranoStatus = event.CyranoStatus

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cyrano)
        binding = ActivityCyranoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle("Cyrano");
        //supportActionBar?.setHomeButtonEnabled(true);
        //supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.hide()
        //binding.btnReserveLeft.visibility = View.INVISIBLE
        //binding.btnReserveRight.visibility = View.INVISIBLE
        binding.textViewNameLeft.text = StoredValues.NameLeft
        binding.textViewNameRight.text = StoredValues.NameRight
        binding.textViewScoreLeftCyrano.text = StoredValues.ScoreRight
        binding.textViewScoreRightCyrano.text = StoredValues.ScoreRight
        binding.textViewCyranoStatus.text = StoredValues.CyranoStatus

        binding.btnPrevMatch.setOnClickListener {
            sendUDP(UI_INPUT_CYRANO_PREV)
        }
        binding.btnNextMatch.setOnClickListener {
            sendUDP(UI_INPUT_CYRANO_NEXT)
        }

        binding.btnBeginMatch.setOnClickListener {
            Toast.makeText(applicationContext, "To Begin the match, do a LONG Press!", Toast.LENGTH_SHORT).show()
        }

        binding.btnBeginMatch.setOnLongClickListener {

            vibrate(100)
            sendUDP(UI_INPUT_CYRANO_BEGIN)
            finish()
            true
        }
        binding.btnEndMatch.setOnClickListener {
            Toast.makeText(applicationContext, "To end the match and confirm results, do a LONG Press!", Toast.LENGTH_SHORT).show()
        }
        binding.btnEndMatch.setOnLongClickListener {

            vibrate(100)
            sendUDP(UI_INPUT_CYRANO_END)
            true
        }
        binding.btnSwapSides.setOnClickListener {
            sendUDP(UI_SWAP_FENCERS)
        }
        binding.btnReserveLeft.setOnClickListener {
            sendUDP(UI_RESERVE_LEFT)
        }
        binding.btnReserveRight.setOnClickListener {
            sendUDP(UI_RESERVE_RIGHT)
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