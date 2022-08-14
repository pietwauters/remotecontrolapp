package com.example.remotecontrolfull

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.red
import android.os.Bundle
import android.os.StrictMode
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.com.kotlinapp.OnSwipeTouchListener
import com.example.remotecontrolfull.databinding.ActivityMainBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


public class SoftOptions {
    var RemoteHost: String = "192.168.4.1"
    var RemotePort: Int = 1234

    constructor()
    init{}
}


// Global
val mySettings = SoftOptions()
val CyranoSettings =  SoftOptions()

val UI_INPUT_START_TIMER = byteArrayOf(0x01,0x00,0x00,0x06)
val UI_INPUT_STOP_TIMER = byteArrayOf(0x02,0x00,0x00,0x06)
val UI_INPUT_RESET = byteArrayOf(0x03,0x00,0x00,0x06)
val UI_INPUT_INCR_SCORE_LEFT = byteArrayOf(0x05,0x00,0x00,0x06)
val UI_INPUT_DECR_SCORE_LEFT = byteArrayOf(0x06,0x00,0x00,0x06)
val UI_INPUT_INCR_SCORE_RIGHT = byteArrayOf(0x07,0x00,0x00,0x06)
val UI_INPUT_DECR_SCORE_RIGHT = byteArrayOf(0x08,0x00,0x00,0x06)
val UI_INPUT_ROUND = byteArrayOf(0x0a,0x00,0x00,0x06)
val UI_INPUT_PRIO = byteArrayOf(0x10,0x00,0x00,0x06)
val UI_INPUT_TOGGLE_TIMER = byteArrayOf(0x11,0x00,0x00,0x06)
val UI_INPUT_CYCLE_WEAPON = byteArrayOf(0x12,0x00,0x00,0x06)
val UI_INPUT_YELLOW_CARD_LEFT = byteArrayOf(0x13,0x00,0x00,0x06)
val UI_INPUT_YELLOW_CARD_RIGHT = byteArrayOf(0x14,0x00,0x00,0x06)
val UI_INPUT_RED_CARD_LEFT = byteArrayOf(0x15,0x00,0x00,0x06)
val UI_INPUT_RED_CARD_RIGHT = byteArrayOf(0x16,0x00,0x00,0x06)
val UI_INPUT_P_CARD = byteArrayOf(0x17,0x00,0x00,0x06)
val UI_INPUT_RESTORE_UW2F_TIMER = byteArrayOf(0x19,0x00,0x00,0x06)
val UI_BUZZ = byteArrayOf(0x18,0x00,0x00,0x06)

fun sendUDP(cmd: ByteArray) {
    // Hack Prevent crash (sending should be done using an async task)
    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
    try {
        //Open a port to send the package
        val socket = DatagramSocket()
        socket.broadcast = true
        val sendPacket = DatagramPacket(cmd, cmd.size, InetAddress.getByName(mySettings.RemoteHost), mySettings.RemotePort)
        socket.send(sendPacket)
    } catch (e: IOException) {
        //Log.e(null, "IOException: " + e.message)
    }
}

open class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var layout: ConstraintLayout
    var previousType3Message = ByteArray(29)

    open fun ProcessUDPMessage(EFPMessage: String)
    {
        //Do Something interesting
        //Log.d("Received data", EFPMessage)
    }
    open fun ProcessUDPTimerMessage(Time: String, Type: String)
    {
        binding.textViewTimer.text = "2:57"
    }
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    public @Subscribe(sticky = true,threadMode = ThreadMode.MAIN_ORDERED)
    open fun onTimerEvent(event: TimerEvent) {

        binding.textViewTimer.text = event.time
        if(event.Type == TimerEventType.Running)
            binding.textViewTimer.setTextColor(Color.parseColor("#4c8c4a"))
        else
            binding.textViewTimer.setTextColor(Color.parseColor("#404070"))
    }

    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    public @Subscribe(sticky = true,threadMode = ThreadMode.MAIN_ORDERED)
    open fun onStatusEvent(event: StatusEvent) {

        // Update the status (deal only with score for the main activity
        binding.textViewScoreLeft.text = event.ScoreLeft
        binding.textViewScoreRight.text = event.ScoreRight
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        binding.btnstartStop.setOnClickListener {
            sendUDP(UI_INPUT_TOGGLE_TIMER)
            val vibrator: Vibrator
            vibrator = getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(150)
        }
        binding.btnIncrLeftScore.setOnClickListener {
            sendUDP(UI_INPUT_INCR_SCORE_LEFT)
        }
        binding.btnDecrLeftScore.setOnClickListener {
            sendUDP(UI_INPUT_DECR_SCORE_LEFT)
        }

        binding.btnIncrRightScore.setOnClickListener {
            sendUDP(UI_INPUT_INCR_SCORE_RIGHT)
        }
        binding.btnDecrRightScore.setOnClickListener {
            sendUDP(UI_INPUT_DECR_SCORE_RIGHT)
        }
        binding.btnreset.setOnClickListener() {
            Toast.makeText(applicationContext, "To Reset do a LONG Press!", Toast.LENGTH_SHORT).show()
        }
        binding.btnreset.setOnLongClickListener {

            val vibrator: Vibrator
            vibrator = getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(150)
            sendUDP(UI_INPUT_RESET)
            true
        }
        layout = findViewById(R.id.MainActivityLayout)
        layout.setOnTouchListener(object : OnSwipeTouchListener(this@MainActivity) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                val intent = Intent(this@MainActivity, Penalties::class.java)
                startActivity(intent)
            }
            override fun onSwipeRight() {
                super.onSwipeRight()
                val intent = Intent(this@MainActivity, Cyrano::class.java)
                startActivity(intent)
            }
            override fun onSwipeUp() {
                super.onSwipeUp()
                val intent = Intent(this@MainActivity, DeviceSettingsBasic::class.java)
                startActivity(intent)
            }
            override fun onSwipeDown() {
                super.onSwipeDown()

            }
        })
        ProcessUDPTimerMessage("","R")
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        println("Create Thread to Listen to UDP .")
        val threadWithRunnable = Thread(ClientListen())
        threadWithRunnable.start()
    }
}
const val MAX_MESSAGE_LENGTH_RS422 = 39
const val SOH:Byte  = 0x01
const val DC3:Byte  = 0x13
const val DC4:Byte  = 0x14
const val EOT:Byte  = 0x04
const val STX:Byte  = 0x02
const val ASCII_N:Byte  = 0x4e
const val ASCII_R:Byte  = 0x52
const val ASCII_D:Byte = 0x44
const val ASCII_I:Byte  = 0x49
const val ASCII_J:Byte  = 0x4a
const val ASCII_B:Byte  = 0x42


open class ClientListen : Runnable , MainActivity() {
    override fun run() {
        var run = true
        try {
            val udpSocket = DatagramSocket(50112)
            val message = ByteArray(512)
            val packet = DatagramPacket(message, message.size)
            while (run) {
                try {
                    //Log.i("UDP client: ", "about to wait to receive")
                    udpSocket.receive(packet)
                    when (MessageType(message)) {
                        RS422_FPAMessageType.Timer -> EventBus.getDefault().postSticky( PacketToTimerEvent(message))
                        RS422_FPAMessageType.CompetitorStatus -> {
                            if(!previousType3Message.contentEquals(message) ) {
                                EventBus.getDefault().postSticky( PacketToStatusEvent(message))
                                previousType3Message = message.copyOf() }}
                        else -> {}//Do nopthing
                        }

                } catch (e: IOException) {
                    Log.e("UDP client has IOException", "error: ", e)
                    run = false
                }
            }
        } catch (e: IOException) {
            Log.e("UDP client has IOException", "error: ", e)
            run = false
        }
    }
}


fun MessageType(message: ByteArray):RS422_FPAMessageType {

    if(message.elementAt(1)== DC4)
        return RS422_FPAMessageType.Lights
    if(message.elementAt(1)!= DC3)
        return RS422_FPAMessageType.Unknown

    if(message.elementAt(3)== STX) {// Type2-3-4

        when (message.elementAt(2)) {
            ASCII_D -> return RS422_FPAMessageType.CompetitorStatus
            ASCII_I -> return RS422_FPAMessageType.ApparatusStatus
            ASCII_R,ASCII_N,ASCII_J,ASCII_B -> return RS422_FPAMessageType.Timer
            else -> return RS422_FPAMessageType.Unknown}
    }
    return RS422_FPAMessageType.Unknown
}

fun PacketToTimerEvent(message: ByteArray):TimerEvent{

    val text1 = String(message, 2, 1)
    val text = String(message, 4, 5)

    val type = when (text1) {
        "R" -> TimerEventType.Running
        "N" -> TimerEventType.Stopped
        "B" -> TimerEventType.Break
        "I" -> TimerEventType.Injury
        else -> TimerEventType.Stopped
    }
    return TimerEvent(text,type)

}

fun PacketToStatusEvent(message: ByteArray):StatusEvent{
    return StatusEvent(
        String(message, 7, 2), String(message, 4, 2),   // Score
        String(message, 16, 2),String(message, 10, 2),  // Yellow Cards
        String(message, 18, 2),String(message, 12, 2),  // RedCards
        String(message, 20, 1),String(message, 14, 1), // Black Cards
        String(message, 22, 1) )// Prio
}