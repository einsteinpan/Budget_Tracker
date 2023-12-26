package com.example.budgettracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class question : AppCompatActivity() {

    private lateinit var feedbackContentEditText: EditText
    private lateinit var contactInfoEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.questionfrom)

        feedbackContentEditText = findViewById(R.id.feedbackContentEditText)
        contactInfoEditText = findViewById(R.id.contactInfoEditText)
        val submitButton: Button = findViewById(R.id.submitButton)

        // 設置按鈕點擊監聽器
        submitButton.setOnClickListener {
            submitFeedback()
        }
    }

    private fun submitFeedback() {
        val feedbackContent = feedbackContentEditText.text.toString()
        val contactInfo = contactInfoEditText.text.toString()

        // 在這裡處理反饋提交的邏輯，你可以將反饋內容和聯繫信息發送到服務器或執行其他操作

        // 這裡只是一個示例，你可以根據實際需求進行修改
        if (feedbackContent.isNotEmpty()) {
            // 提交反饋
            // TODO: 在這裡處理反饋的邏輯

            // 跳轉回 MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // 結束當前的 Activity，以防止用戶返回到 question Activity

            // 顯示成功傳送的提示
            showToast("成功傳送")
        } else {
            // 提示用戶輸入反饋內容
            feedbackContentEditText.error = "請輸入您的反饋內容"
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
