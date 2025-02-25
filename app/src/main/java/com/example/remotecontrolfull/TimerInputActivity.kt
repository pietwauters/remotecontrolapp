package com.example.remotecontrolfull


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class TimerInputActivity : AppCompatActivity() {

    private lateinit var timerEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer_input)

        timerEditText = findViewById(R.id.timerEditText)
        saveButton = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            val timerValue = timerEditText.text.toString()
            if (isValidTimerFormat(timerValue)) {
                val resultIntent = Intent()
                resultIntent.putExtra("TIMER_VALUE", timerValue)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                timerEditText.error = "Invalid format. Use M:SS.HH"
            }
        }
    }


    private fun isValidTimerFormat(timerValue: String): Boolean {
        // Regex for M:SS (M > 0 or M = 0 and SS > 9)
        val regexMSS = Regex("^\\d{1,2}:[0-5]\\d$")
        // Regex for S.HH (M = 0 and SS <= 9)
        val regexSHH = Regex("^\\d{1,2}\\.\\d{2}$")

        return regexMSS.matches(timerValue) || regexSHH.matches(timerValue)
    }
}