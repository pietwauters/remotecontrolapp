package com.example.remotecontrolfull
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class TimerInputActivity : AppCompatActivity() {

    private lateinit var timerEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer_input)

        timerEditText = findViewById(R.id.timerEditText)
        saveButton = findViewById(R.id.saveButton)

        // Inflate the custom keyboard
        val customKeyboard = layoutInflater.inflate(R.layout.custom_keyboard, null) as View
        timerEditText.showSoftInputOnFocus = false // Disable the default keyboard

        // Add the custom keyboard to the FrameLayout container
        val keyboardContainer = findViewById<FrameLayout>(R.id.keyboardContainer)
        keyboardContainer.addView(customKeyboard)

        // Set up button click listeners
        setupKeyboardButtons(customKeyboard)

        saveButton.setOnClickListener {
            val timerValue = timerEditText.text.toString()
            if (isValidTimerFormat(timerValue)) {
                val resultIntent = Intent()
                resultIntent.putExtra("TIMER_VALUE", timerValue)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                timerEditText.error = "Invalid format. Use M:SS or S.HH"
            }
        }
    }

    private fun setupKeyboardButtons(keyboardView: View) {
        // Map button IDs to their corresponding characters
        val buttonMap = mapOf(
            R.id.button1 to "1",
            R.id.button2 to "2",
            R.id.button3 to "3",
            R.id.button4 to "4",
            R.id.button5 to "5",
            R.id.button6 to "6",
            R.id.button7 to "7",
            R.id.button8 to "8",
            R.id.button9 to "9",
            R.id.button0 to "0",
            R.id.buttonDot to ".",
            R.id.buttonColon to ":"
        )

        // Set click listeners for all buttons
        buttonMap.forEach { (id, value) ->
            keyboardView.findViewById<Button>(id).setOnClickListener {
                timerEditText.append(value)
            }
        }

        // Handle backspace button
        keyboardView.findViewById<Button>(R.id.buttonBackspace).setOnClickListener {
            val text = timerEditText.text.toString()
            if (text.isNotEmpty()) {
                timerEditText.setText(text.substring(0, text.length - 1))
                timerEditText.setSelection(timerEditText.text.length) // Move cursor to the end
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