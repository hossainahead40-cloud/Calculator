package com.itsolution.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CalculatorApp()
        }
    }
}

@Composable
fun CalculatorApp() {
    var display by remember { mutableStateOf("0") }
    var firstNumber by remember { mutableStateOf<Double?>(null) }
    var operation by remember { mutableStateOf<String?>(null) }
    var newNumber by remember { mutableStateOf(true) }

    fun calculate(second: Double): Double {
        val first = firstNumber ?: return second

        return when (operation) {
            "+" -> first + second
            "−" -> first - second
            "×" -> first * second
            "÷" -> if (second == 0.0) Double.NaN else first / second
            else -> second
        }
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF7F7F7)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {

                Text(
                    text = "Calculator",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF222222)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = display,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    color = Color(0xFF111111)
                )

                Spacer(modifier = Modifier.height(24.dp))

                val buttons = listOf(
                    listOf("AC", "⌫", "%", "÷"),
                    listOf("7", "8", "9", "×"),
                    listOf("4", "5", "6", "−"),
                    listOf("1", "2", "3", "+"),
                    listOf("00", "0", ".", "=")
                )

                buttons.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEach { button ->
                            Button(
                                onClick = {
                                    when (button) {

                                        "AC" -> {
                                            display = "0"
                                            firstNumber = null
                                            operation = null
                                            newNumber = true
                                        }

                                        "⌫" -> {
                                            if (display.length > 1) {
                                                display = display.dropLast(1)
                                            } else {
                                                display = "0"
                                            }
                                        }

                                        "%" -> {
                                            val value = display.toDoubleOrNull()
                                            if (value != null) {
                                                display = formatNumber(value / 100)
                                            }
                                        }

                                        "+", "−", "×", "÷" -> {
                                            firstNumber = display.toDoubleOrNull()
                                            operation = button
                                            newNumber = true
                                        }

                                        "=" -> {
                                            val second = display.toDoubleOrNull()

                                            if (second != null && firstNumber != null) {
                                                val result = calculate(second)

                                                display = if (result.isNaN()) {
                                                    "Error"
                                                } else {
                                                    formatNumber(result)
                                                }

                                                firstNumber = null
                                                operation = null
                                                newNumber = true
                                            }
                                        }

                                        "." -> {
                                            if (newNumber) {
                                                display = "0."
                                                newNumber = false
                                            } else if (!display.contains(".")) {
                                                display += "."
                                            }
                                        }

                                        else -> {
                                            if (newNumber || display == "Error") {
                                                display = button
                                                newNumber = false
                                            } else {
                                                display += button
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f),
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = when {
                                        button == "=" ->
                                            Color(0xFF2563EB)

                                        button in listOf(
                                            "+", "−", "×", "÷"
                                        ) ->
                                            Color(0xFFE8EEF9)

                                        button in listOf(
                                            "AC", "⌫", "%"
                                        ) ->
                                            Color(0xFFE5E7EB)

                                        else ->
                                            Color.White
                                    },
                                    contentColor = Color(0xFF111111)
                                )
                            ) {
                                Text(
                                    text = button,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "IT Solution Limited",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

fun formatNumber(number: Double): String {
    return if (number % 1.0 == 0.0) {
        number.toLong().toString()
    } else {
        number.toString()
    }
}
