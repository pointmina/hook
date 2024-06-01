package com.hanto.hook.view

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.hanto.hook.databinding.ActivityWebviewBinding

class WebviewActivity : AppCompatActivity() {
    private lateinit var webView: WebView // 변수명 수정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.mainWebView
        val url = intent.getStringExtra(EXTRA_URL) ?: return

        webView.apply {
            loadUrl(url)
            webChromeClient = WebChromeClient()
            webViewClient = WebViewClient()
            settings.let {
                it.javaScriptEnabled = true
                it.setSupportZoom(true)
                it.builtInZoomControls = true
                it.displayZoomControls = false
            }
        }
    }

    companion object {
        const val EXTRA_URL = "EXTRA_URL"
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            // 뒤로 갈 페이지가 있다면, 웹사이트 뒤로 가기
            webView.goBack()
        } else {
            // 그렇지 않다면, 안드로이드 기본 뒤로가기
            super.onBackPressed()
        }
    }
}
