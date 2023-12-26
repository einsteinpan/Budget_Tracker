package com.example.budgettracker

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class question : AppCompatActivity() {

    private lateinit var feedbackContentEditText: EditText
    private lateinit var contactInfoEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.questionfrom)

        val myWebView: WebView = findViewById(R.id.webview)
        val webSettings: WebSettings = myWebView.settings
        webSettings.javaScriptEnabled = true
        myWebView.loadUrl("https://docs.google.com/forms/d/e/1FAIpQLSc1FwYNrWSf5gnpvdeIFAFKWTw9IGtzMoVzm_YoLQVXBshchg/viewform?usp=sf_link")


    }





}