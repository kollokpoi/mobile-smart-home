package com.example.myapplication

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getIntExtra("id",0)
        val name = intent.getStringExtra("name")
        val webView = binding.main
        binding.titleTv.text = name
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("${getString(R.string.api_ip)}items/interfaces/${id}/")
    }
}