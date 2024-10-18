package com.example.myapplication

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_web_view)
        val id = intent.getIntExtra("id",0)
        val webView = findViewById<WebView>(R.id.main)
        webView.loadUrl("${getString(R.string.api_ip)}public/interfaces/${id}")
    }
}