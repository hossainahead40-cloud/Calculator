package com.itsolution.calculator

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : ComponentActivity() {

    private lateinit var display: TextView

    private var firstNumber: Double? = null
    private var operator: String? = null
    private var newNumber = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(247, 247, 247)
        window.navigationBarColor = Color.rgb(247, 247, 247)

        createCalculator()
    }

    private fun createCalculator() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            setBackgroundColor(Color.rgb(247, 247, 247))
        }

        val title = TextView(this).apply {
            text = "Calculator"
            textSize = 22f
            setTextColor(Color.rgb(30, 30, 30))
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(50)
            )
        )

        display = TextView(this).apply {
            text = "0"
            textSize = 52f
            setTextColor(Color.rgb(20, 20, 20))
            gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            maxLines = 1
            setPadding(dp(8), 0, dp(8), 0)
        }

        val displayParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1.3f
        )

        root.addView(display, displayParams)

        val grid = GridLayout(this).apply {
            columnCount = 4
            rowCount = 5
            alignmentMode = GridLayout.ALIGN_BOUNDS
            useDefaultMargins = false
        }

        val buttons = arrayOf(
            "AC", "⌫", "%", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "−",
            "1", "2", "3", "+",
            "00", "0", ".", "="
        )

        buttons.forEach { text ->
            val button = createButton(text)

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = 0
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(dp(5), dp(5), dp(5), dp(5))
            }

            grid.addView(button, params)
        }

        root.addView(
            grid,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                5f
            )
        )

        val company = TextView(this).apply {
            text = "IT Solution Limited"
            textSize = 12f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
        }

        root.addView(
            company,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(35)
            )
        )

        setContentView(root)
    }

    private fun createButton(text: String): Button {

        return Button(this).apply {

            this.text = text
            textSize = 22f
            isAllCaps = false
            setTextColor(Color.rgb(20, 20, 20))
            stateListAnimator = null

            background = roundedBackground(
                when {
                    text == "=" ->
                        Color.rgb(37, 99, 235)

                    text in listOf("+", "−", "×", "÷") ->
                        Color.rgb(232, 238, 249)

                    text in listOf("AC", "⌫", "%") ->
                        Color.rgb(229, 231, 235)

                    else ->
                        Color.WHITE
                }
            )

            if (text == "=") {
                setTextColor(Color.WHITE)
            }

            setOnClickListener {
                buttonPressed(text)
            }
        }
    }

    private fun buttonPressed(value: String) {

        when (value) {

            "AC" -> {
                display.text = "0"
                firstNumber = null
                operator = null
                newNumber = true
            }

            "⌫" -> {
                if (display.text.length > 1) {
                    display.text = display.text.dropLast(1)
                } else {
                    display.text = "0"
                }
            }

            "%" -> {
                val number = display.text.toString().toDoubleOrNull()

                if (number != null) {
                    display.text = formatNumber(number / 100)
                }
            }

            "+", "−", "×", "÷" -> {

                val number = display.text.toString().toDoubleOrNull()

                if (number != null) {
                    firstNumber = number
                    operator = value
                    newNumber = true
                }
            }

            "=" -> calculateResult()

            "." -> {

                if (newNumber || display.text == "Error") {
                    display.text = "0."
                    newNumber = false
                } else if (!display.text.contains(".")) {
                    display.text = "${display.text}."
                }
            }

            else -> {

                if (newNumber || display.text == "Error") {
                    display.text = value
                    newNumber = false
                } else {
                    display.text = "${display.text}$value"
                }
            }
        }
    }

    private fun calculateResult() {

        val first = firstNumber ?: return
        val second = display.text.toString().toDoubleOrNull() ?: return
        val op = operator ?: return

        val result = when (op) {

            "+" -> first + second

            "−" -> first - second

            "×" -> first * second

            "÷" -> {
                if (second == 0.0) {
                    Double.NaN
                } else {
                    first / second
                }
            }

            else -> second
        }

        display.text = if (result.isNaN() || result.isInfinite()) {
            "Error"
        } else {
            formatNumber(result)
        }

        firstNumber = null
        operator = null
        newNumber = true
    }

    private fun formatNumber(number: Double): String {

        return try {
            BigDecimal(number.toString())
                .setScale(10, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString()
        } catch (e: Exception) {
            number.toString()
        }
    }

    private fun roundedBackground(color: Int): GradientDrawable {

        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(22).toFloat()
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
