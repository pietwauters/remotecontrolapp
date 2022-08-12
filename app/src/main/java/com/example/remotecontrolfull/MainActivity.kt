package com.example.remotecontrolfull

import android.R.attr.port
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import app.com.kotlinapp.OnSwipeTouchListener
import com.example.remotecontrolfull.databinding.ActivityMainBinding
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetAddress.getLocalHost


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

    open fun ProcessUDPMessage(EFPMessage: String)
    {
        //Do Something interesting
        //Log.d("Received data", EFPMessage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)
        println("Create Thread to Listen to UDP .")
        val threadWithRunnable = Thread(ClientListen())
        threadWithRunnable.start()

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
    }
}

open class ClientListen : Runnable , MainActivity() {
    override fun run() {
        var run = true
        try {
            val udpSocket = DatagramSocket(50112)
            val message = ByteArray(512)
            val packet = DatagramPacket(message, message.size)
            //Log.i("UDP client: ", "about to wait to receive")
            while (run) {
                try {
                    //Log.i("UDP client: ", "about to wait to receive")
                    udpSocket.receive(packet)
                    val text = String(message, 0, packet.length)
                    //Log.d("Received data", text)
                    ProcessUDPMessage(text)
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