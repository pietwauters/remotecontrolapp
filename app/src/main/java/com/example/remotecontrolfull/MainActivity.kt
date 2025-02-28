package com.example.remotecontrolfull

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.red
import android.os.*
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
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import android.view.View
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
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
val UI_INPUT_YELLOW_CARD_LEFT_DECR = byteArrayOf(0x13, 0xff.toByte(),0x00,0x06)
val UI_INPUT_YELLOW_CARD_RIGHT = byteArrayOf(0x14,0x00,0x00,0x06)
val UI_INPUT_YELLOW_CARD_RIGHT_DECR = byteArrayOf(0x14, 0xff.toByte(),0x00,0x06)
val UI_INPUT_RED_CARD_LEFT = byteArrayOf(0x15,0x00,0x00,0x06)
val UI_INPUT_RED_CARD_LEFT_DECR = byteArrayOf(0x15, 0xff.toByte(),0x00,0x06)
val UI_INPUT_RED_CARD_RIGHT = byteArrayOf(0x16,0x00,0x00,0x06)
val UI_INPUT_RED_CARD_RIGHT_DECR = byteArrayOf(0x16, 0xff.toByte(),0x00,0x06)
val UI_INPUT_P_CARD = byteArrayOf(0x17,0x00,0x00,0x06)
val UI_INPUT_P_CARD_UNDO = byteArrayOf(0x17,0xff.toByte(),0x00,0x06)
val UI_INPUT_RESTORE_UW2F_TIMER = byteArrayOf(0x19,0x00,0x00,0x06)
val UI_BUZZ = byteArrayOf(0x18,0x00,0x00,0x06)
val UI_MINUTE_BREAK = byteArrayOf(0x20,0x00,0x00,0x06)
val UI_NEXT_PERIOD = byteArrayOf(0x21,0x00,0x00,0x06)
val UI_INPUT_CYCLE_BRIGHTNESS = byteArrayOf(0x30,0x00,0x00,0x06)
val UI_CABLETEST_OFF = byteArrayOf(0x22,0x00,0x00,0x06)
val UI_CABLETEST_ON = byteArrayOf(0x23,0x00,0x00,0x06)

val UI_SET_MINUTES = byteArrayOf(0x40,0x00,0x00,0x06)
val UI_SET_SECONDS = byteArrayOf(0x41,0x00,0x00,0x06)
val UI_SET_HUNDREDS = byteArrayOf(0x42,0x00,0x00,0x06)
val UI_INPUT_BLACK_CARD_RIGHT = byteArrayOf(0x50,0x00,0x00,0x06)
val UI_INPUT_BLACK_CARD_LEFT = byteArrayOf(0x51,0x00,0x00,0x06)
val UI_INPUT_BLACK_CARD_RIGHT_DECR = byteArrayOf(0x50,0xff.toByte(),0x00,0x06)
val UI_INPUT_BLACK_CARD_LEFT_DECR = byteArrayOf(0x51,0xff.toByte(),0x00,0x06)

val UI_INPUT_BLACK_PCARD_RIGHT = byteArrayOf(0x52,0x00,0x00,0x06)
val UI_INPUT_BLACK_PCARD_LEFT = byteArrayOf(0x53,0x00,0x00,0x06)
val UI_INPUT_BLACK_PCARD_RIGHT_DECR = byteArrayOf(0x52,0xff.toByte(),0x00,0x06)
val UI_INPUT_BLACK_PCARD_LEFT_DECR = byteArrayOf(0x53,0xff.toByte(),0x00,0x06)

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
    companion object StoredValues{
        var UIMode = false
    }

    lateinit var binding: ActivityMainBinding
    private lateinit var layout: ConstraintLayout
    var previousType3Message = ByteArray(29)
    var Beeping = false
    var Buzzing = false
    var TimerIsRunning = false

    // Register a launcher for the TimerInputActivity
    private val timerInputLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val timerValue = data?.getStringExtra("TIMER_VALUE")
            binding.textViewTimer.text = formatTimerForDisplayandSendToDevice(timerValue ?: "0.00")
        }
    }
    private fun formatTimerForDisplayandSendToDevice(timerValue: String): String {
        return if (timerValue.contains(":")) {
            // Format is M:SS (no rounding needed)
            val parts = timerValue.split(":")
            var minutes = parts[0].toByte()
            if(minutes > 2)
                minutes = 2
            val seconds = parts[1].toByte()
            var theMessage = UI_SET_MINUTES
            theMessage[1] = minutes
            sendUDP(theMessage)
            theMessage = UI_SET_SECONDS
            theMessage[1] = seconds
            sendUDP(theMessage)
            sendUDP(UI_SET_HUNDREDS)
            timerValue
        } else {
            // Format is S.HH
            val parts = timerValue.split(".")
            var seconds = parts[0].toByte()
            val hundredths = parts[1].toByte()

            if (seconds >= 10) {
                // Round to the nearest second
                if (hundredths >= 50) {
                    seconds++
                }
                // Convert to M:SS format
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                var theMessage = UI_SET_MINUTES
                theMessage[1] = minutes.toByte()
                sendUDP(theMessage)
                theMessage = UI_SET_SECONDS
                theMessage[1] = remainingSeconds.toByte()
                sendUDP(theMessage)
                theMessage = UI_SET_HUNDREDS
                sendUDP(theMessage)

                String.format("%d:%02d", minutes, remainingSeconds)
            } else {
                // Keep as S.HH format (full precision, no rounding)

                var theMessage = UI_SET_MINUTES
                theMessage[1] = 0
                sendUDP(theMessage)
                theMessage = UI_SET_SECONDS
                theMessage[1] = seconds
                sendUDP(theMessage)
                theMessage = UI_SET_HUNDREDS
                theMessage[1] = hundredths
                sendUDP(theMessage)
                String.format("%d.%02d", seconds, hundredths)
            }
        }
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
    override fun onResume() {
        super.onResume()
        SetSimpleUIMode()

    }
    override fun onPause()
    {
        super.onPause()
        val sharedPreferences = getPreferences( MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.putBoolean("usesimpleUI", StoredValues.UIMode)
        myEdit.apply()
    }
    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
    // This method will be called when a LightsEven is posted (in the UI thread for Toast)
    public @Subscribe(sticky = true,threadMode = ThreadMode.MAIN_ORDERED)
    open fun onLightsEvent(event: LightsEvent) {

        if(!Beeping)
        {
            if((event.Red) || (event.Green))
            {
                ToneGenerator(AudioManager.STREAM_MUSIC, 100).startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_NORMAL, 1400)

                if(!Buzzing)
                    vibrate(600)
                Beeping = true
                Buzzing = true
            }
        }


        if((!event.Red) && (!event.Green))
        {
            Beeping = false
            Buzzing = false
        }


    }

    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    public @Subscribe(sticky = true,threadMode = ThreadMode.MAIN_ORDERED)
    open fun onTimerEvent(event: TimerEvent) {

        binding.textViewTimer.text = event.time
        // only react on state changes
        if(((event.Type == TimerEventType.Running) && TimerIsRunning) || ((event.Type != TimerEventType.Running) && !TimerIsRunning))
            return;
        if(event.Type == TimerEventType.Running) {
            binding.textViewTimer.setTextColor(Color.parseColor("#4c8c4a"))
            vibrate(300)
            TimerIsRunning = true
        }
        else{
            binding.textViewTimer.setTextColor(Color.parseColor("#404070"))
            if(TimerIsRunning)
            {
                ToneGenerator(AudioManager.STREAM_MUSIC, 100).startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_NORMAL, 1400)
                vibrate(600)
                Beeping = true
                Buzzing = true
            }
            TimerIsRunning = false
        }
    }

    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    public @Subscribe(sticky = true,threadMode = ThreadMode.MAIN_ORDERED)
    open fun onStatusEvent(event: StatusEvent) {

        // Update the status (deal only with score for the main activity
        binding.textViewScoreLeft.text = event.ScoreLeft
        binding.textViewScoreRight.text = event.ScoreRight
        binding.textViewRound.text = event.Round
    }
    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    public @Subscribe(sticky = true,threadMode = ThreadMode.MAIN_ORDERED)
    open fun onExtraInfoEvent(event: ExtraInfoEvent) {

        // Update the status (deal only with score for the main activity
        binding.btnstartStop.setText("Piste " +event.PistID + "\n\nStart\nStop")
    }
    fun SetSimpleUIMode()
    {
        if(StoredValues.UIMode)
        {
            binding.textViewScoreLeft.visibility = View.GONE
            binding.textViewTimer.visibility = View.GONE
            binding.textViewScoreRight.visibility = View.GONE
            binding.textViewRound.visibility = View.GONE
        }
        else
        {
            binding.textViewScoreLeft.visibility = View.VISIBLE
            binding.textViewTimer.visibility = View.VISIBLE
            binding.textViewScoreRight.visibility = View.VISIBLE
            binding.textViewRound.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val sh = getPreferences(MODE_PRIVATE)
        val a = sh.getBoolean("usesimpleUI", false)
        StoredValues.UIMode = a
        SetSimpleUIMode()

        binding.btnstartStop.setOnClickListener {
            sendUDP(UI_INPUT_TOGGLE_TIMER)
            //vibrate(150)
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
        // Set a click listener on the TextView
        binding.textViewTimer.setOnClickListener {
            Toast.makeText(applicationContext, "Do a LONG Press to change the timer!", Toast.LENGTH_SHORT).show()
        }
        binding.textViewTimer.setOnLongClickListener {
            val intent = Intent(this, TimerInputActivity::class.java)
            timerInputLauncher.launch(intent)
            true
        }
        binding.btnBNextPause.setOnLongClickListener {
            sendUDP(UI_NEXT_PERIOD)
            vibrate(150)
            true
        }

        binding.btnBNextPause.setOnClickListener {
            Toast.makeText(applicationContext, "Do a LONG Press to go break or next round!", Toast.LENGTH_SHORT).show()
        }
        binding.btnreset.setOnClickListener() {
            Toast.makeText(applicationContext, "To Reset do a LONG Press!", Toast.LENGTH_SHORT).show()
        }
        binding.btnreset.setOnLongClickListener {

            vibrate(150)
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
const val ASCII_1:Byte = 0x31


open class ClientListen : Runnable , MainActivity() {
    override fun run() {
        var run = true
        try {
            val udpSocket = DatagramSocket(50112)
            val message = ByteArray(64)
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
                        RS422_FPAMessageType.CompetitorLeftInformation -> EventBus.getDefault().postSticky( PacketToCompetitorEvent(message))
                        RS422_FPAMessageType.CompetitorRightInformation -> EventBus.getDefault().postSticky( PacketToCompetitorEvent(message))
                        RS422_FPAMessageType.RemoteControl -> EventBus.getDefault().postSticky( PacketToRemoteControlEvent(message))
                        RS422_FPAMessageType.Lights -> EventBus.getDefault().postSticky( PacketToLightsEvent(message))
                        RS422_FPAMessageType.ExtraInfo -> EventBus.getDefault().postSticky( PacketToExtraInfoEvent(message))
                        RS422_FPAMessageType.UW2F -> EventBus.getDefault().postSticky( PacketToU2FEvent(message))
                        else -> {}//Do nothing
                        }

                } catch (e: IOException) {
                    //Log.e("UDP client has IOException", "error: ", e)
                    run = false
                }
            }
        } catch (e: IOException) {
            //Log.e("UDP client has IOException", "error: ", e)
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
    if(message.elementAt(4)== STX) {// Type5-6-7-8-9-10
        val typeindicator = String(message, 2, 2)
        when (typeindicator) {
            "NL" -> return RS422_FPAMessageType.CompetitorLeftInformation
            "NR" -> return RS422_FPAMessageType.CompetitorRightInformation
            "MC" -> return RS422_FPAMessageType.CompetitionInformation
            "UF" -> return RS422_FPAMessageType.UW2F
            "FC" -> return RS422_FPAMessageType.RemoteControl
            "CS" -> return RS422_FPAMessageType.ExtraInfo
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
fun PacketToLightsEvent(message: ByteArray):LightsEvent{

    var red : Boolean = false
    var green : Boolean = false
    var whiteleft : Boolean = false
    var whiteright : Boolean = false

    if(message.elementAt(3)== ASCII_1)
        red = true
    if(message.elementAt(5)== ASCII_1)
        green = true
    if(message.elementAt(7)== ASCII_1)
        whiteleft = true
    if(message.elementAt(9)== ASCII_1)
        whiteright = true

    return LightsEvent(red,green,whiteleft,whiteright)

}


fun PacketToStatusEvent(message: ByteArray):StatusEvent{
    return StatusEvent(
        String(message, 7, 2), String(message, 4, 2),   // Score
        String(message, 16, 2),String(message, 10, 2),  // Yellow Cards
        String(message, 18, 2),String(message, 12, 2),  // RedCards
        String(message, 20, 1),String(message, 14, 1), // Black Cards
        String(message, 22, 1), // Prio
        String(message, 24, 1), )// Round
}

fun PacketToU2FEvent(message: ByteArray):U2FEvent{
    var rightcards = String(message, 10, 1)
    var lefttcards = String(message, 12, 1)
    var YellowPCardLeft = "0"
    var YellowPCardRight = "0"
    var RedPCardLeft = "0"
    var RedPCardRight = "0"
    var BlackPCardLeft = "0"
    var BlackPCardRight = "0"
    when (rightcards) {
        "1" -> YellowPCardRight = "1"
        "2" -> RedPCardRight = "1"
        "4" -> BlackPCardRight = "1"
    }
    when (lefttcards) {
        "1" -> YellowPCardLeft = "1"
        "2" -> RedPCardLeft = "1"
        "4" -> BlackPCardLeft = "1"
    }
    return U2FEvent(
         YellowPCardLeft,
     YellowPCardRight,
     RedPCardLeft,
     RedPCardRight,
     BlackPCardLeft,
     BlackPCardRight)
}

fun PacketToRemoteControlEvent(message: ByteArray):RemoteControlEvent{

    var TheValue = ""
    var i = 5
    var length = 0;
    while (i < message.size)
    {
        if(message[i] == EOT) {
            i == message.size
        }
        length++;
        i++
    }
    return RemoteControlEvent(
        String(message, 5, length) )// Value
}

fun PacketToCompetitorEvent(message: ByteArray):CompetitorEvent{

    // Extract name
    var TheName = ""
    var i = 0
    var STXCount = 0
    var SecondSTX = 0
    var ThirdSTX = 0

    while (i < message.size)
    {
        if(message[i] == STX) {
            STXCount++
            if(STXCount == 2)
                SecondSTX = i
            if(STXCount == 3)
                ThirdSTX = i
        }
        if(message[i] == EOT) {
            i == message.size
        }
        i++
    }

    val length = ThirdSTX-SecondSTX-1
    if((SecondSTX != 0) && (ThirdSTX != 0) &&  (length > 0))
        TheName = String(message, SecondSTX+1, length)

    if(String(message, 3, 1) == "L") {
        return CompetitorEvent(SideOfEvent.Left, TheName  )  }
    else{
        return CompetitorEvent(SideOfEvent.Right, TheName)}
}

fun PacketToExtraInfoEvent(message: ByteArray):ExtraInfoEvent{

    val text = String(message, 5, 3)
    val MessageText = when (message[11]){
        87.toByte() -> "Waiting for next bout"
        69.toByte() -> "Ending"
        53.toByte() -> "Incorrect end. \nCheck the score or priority"
        80.toByte() -> "Paused"
        72.toByte() -> "Halted"
        70.toByte() -> "Fencing"
        else -> "Unknown state"
    }
    //val text1 = "" + message[13].toUByte() + "." + message[15].toUByte() + "." + message[17].toUByte() + "." + message[19].toUByte()+ '\n' + message[11].toInt().toChar();
    val text1 = "" + message[13].toUByte() + "." + message[15].toUByte() + "." + message[17].toUByte() + "." + message[19].toUByte()+ '\n' + MessageText;
    return ExtraInfoEvent(text,text1)

}