package com.example.lab01_bai2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.lab01_bai2.network.GeminiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var mainContainer: LinearLayout
    private lateinit var edtInput: EditText
    private lateinit var btnSubmit: Button
    private lateinit var tvEmoji: TextView
    private lateinit var tvResult: TextView
    private lateinit var tvExplanation: TextView
    private lateinit var progressBar: ProgressBar

    private val geminiService = GeminiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainContainer = findViewById(R.id.mainContainer)
        edtInput = findViewById(R.id.edtInput)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvEmoji = findViewById(R.id.tvEmoji)
        tvResult = findViewById(R.id.tvResult)
        tvExplanation = findViewById(R.id.tvExplanation)
        progressBar = findViewById(R.id.progressBar)

        btnSubmit.setOnClickListener {
            val inputText = edtInput.text.toString().trim()

            if (inputText.isEmpty()) {
                Toast.makeText(this, "Please enter a sentence", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            analyzeSentiment(inputText)
        }
    }

    private fun analyzeSentiment(text: String) {
        progressBar.visibility = View.VISIBLE
        btnSubmit.isEnabled = false
        tvResult.text = "Analyzing..."
        tvExplanation.text = ""
        tvEmoji.text = "⏳"

        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    geminiService.analyzeSentiment(text)
                }

                updateUI(result.label, result.explanation)

            } catch (e: Exception) {
                tvResult.text = "Error"
                tvExplanation.text = e.message ?: "Unknown error"
                tvEmoji.text = "⚠️"
                Toast.makeText(this@MainActivity, e.message ?: "Request failed", Toast.LENGTH_LONG).show()
            } finally {
                progressBar.visibility = View.GONE
                btnSubmit.isEnabled = true
            }
        }
    }

    private fun updateUI(label: String, explanation: String) {
        when (label.uppercase()) {
            "POSITIVE" -> {
                mainContainer.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.positive_bg)
                )
                tvEmoji.text = "😀"
                tvResult.text = "Positive"
            }

            "NEGATIVE" -> {
                mainContainer.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.negative_bg)
                )
                tvEmoji.text = "☹️"
                tvResult.text = "Negative"
            }

            else -> {
                mainContainer.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.neutral_bg)
                )
                tvEmoji.text = "😐"
                tvResult.text = "Neutral"
            }
        }

        tvExplanation.text = explanation
    }
}