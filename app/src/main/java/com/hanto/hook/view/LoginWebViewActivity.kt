package com.hanto.hook.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.hanto.hook.BuildConfig
import com.hanto.hook.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class LoginWebViewActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_URL = "EXTRA_URL"
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val webView = findViewById<WebView>(R.id.mainWebView)
        val initURL = intent.getStringExtra(EXTRA_URL) ?: return

        webView.apply {
            settings.javaScriptEnabled = true

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url.toString()
                    if (url.startsWith(BuildConfig.KAKAO_REDIRECT)) {
                        handleRedirect(url)
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }
            loadUrl(initURL)
        }
    }

    private fun handleRedirect(url: String) {
        lifecycleScope.launch {
            try {
                // 네트워크 작업을 Dispatchers.IO에서 실행
                val response = withContext(Dispatchers.IO) {
                    URL(url).readText()
                }
                val jsonObject = JSONObject(response)
                val accessToken = jsonObject.getString("accessToken")
                val refreshToken = jsonObject.getString("refreshToken")


                saveToken(accessToken, refreshToken)

                withContext(Dispatchers.Main) {
                    clearWebViewData()

                    val intent = Intent(this@LoginWebViewActivity, SplashView::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun saveToken(accessToken: String, refreshToken: String) {
        val accessTokenKey = stringPreferencesKey("access_token")
        val refreshTokenKey = stringPreferencesKey("refresh_token")

        applicationContext.dataStore.edit { preferences ->
            preferences[accessTokenKey] = accessToken
            preferences[refreshTokenKey] = refreshToken
        }
    }

    private fun clearWebViewData() {
        val webView = findViewById<WebView>(R.id.mainWebView)
        webView.apply {
            clearHistory() // 방문 기록 삭제
            clearCache(true) // 캐시 삭제
            clearFormData() // 양식 데이터 삭제
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearWebViewData() // 액티비티가 종료될 때 웹뷰 데이터 삭제
    }
}

