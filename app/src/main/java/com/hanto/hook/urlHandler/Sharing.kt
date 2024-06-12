package com.hanto.hook.urlHandler

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.hanto.hook.api.RetroServer
import com.hanto.hook.view.LoginActivity
import com.hanto.hook.view.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.Timer
import java.util.TimerTask
import kotlin.coroutines.cancellation.CancellationException

class Sharing : AppCompatActivity() {

    private lateinit var timer: Timer
    private val timeout = 5000L

    private val _pageTitle = MutableLiveData<String?>()
    private val pageTitle: LiveData<String?>
        get() = _pageTitle

    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStore = applicationContext.dataStore
        val accessToken = loadAccessToken(dataStore)

        if (accessToken.isNotEmpty()) {
            RetroServer.accessToken = accessToken
        } else {
            val intent = Intent(this@Sharing, LoginActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "로그인이 필요한 서비스 입니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        webCrawlingHandler()
    }

    private fun loadAccessToken(dataStore: DataStore<Preferences>): String {
        val accessTokenKey = stringPreferencesKey("access_token")
        return runBlocking {
            dataStore.data.map { preferences ->
                preferences[accessTokenKey] ?: ""
            }.first()
        }
    }

    private fun extractUrl(sharedText: String?): String? {
        return sharedText?.substringAfter("http")?.substringBefore(" ")?.let { "http$it" }
    }

    private fun webCrawlingHandler() {
        val receivedIntent: Intent? = intent
        val receivedAction: String? = receivedIntent?.action
        val receivedType: String? = receivedIntent?.type

        if (receivedAction == Intent.ACTION_SEND && receivedType == "text/plain") {
            val sharedText: String? = receivedIntent.getStringExtra(Intent.EXTRA_TEXT)
            val url = extractUrl(sharedText)
            url?.let { originUrl ->
                startAutoCloseTimer()
                lifecycleScope.launch {
                    loadWebTitle(originUrl)
                }
                showToastMessage("훅 생성 시작!")
                pageTitle.observe(this) { pageTitle ->
                    pageTitle?.let { title ->
                        toast?.cancel() // 토스트 메시지 취소
                        openPageDetailsDialog(title, originUrl)
                        cancelAutoCloseTimer()
                    } ?: run {
                        toast?.cancel()
                        showToastMessage("페이지 제목을 불러올 수 없습니다.")
                        openPageDetailsDialog(null, originUrl)
                        cancelAutoCloseTimer()
                    }
                }
            } ?: run {
                println("Received URL is null")
                finish()
            }
        } else {
            println("Unexpected intent received")
            finish()
        }
    }

    private fun showToastMessage(message: String) {
        runOnUiThread {
            toast?.cancel() // 이전 토스트 메시지 취소
            toast = Toast.makeText(this@Sharing, message, Toast.LENGTH_LONG)
            toast?.show()
        }
    }

    private fun startAutoCloseTimer() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (!isFinishing) {
                        println("Dialog not opened in time, closing Sharing activity")
                        finish()
                    }
                }
            }
        }, timeout)
    }

    private fun cancelAutoCloseTimer() {
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }

    private fun openPageDetailsDialog(title: String?, url: String) {
        val dialog = PageDetailsDialog(this, title ?: "", url) // "default_title"을 기본값으로 설정
        dialog.setOnDismissListener {
            this.finishAffinity()
        }
        dialog.show()
    }

    private suspend fun loadWebTitle(url: String) {
        withContext(Dispatchers.IO) {
            println("loadWebtitle시작")
            try {
                val document: Document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .timeout(3000) // 10초 타임아웃 설정
                    .get()
                val titleElement = document.select("meta[property=og:title]").first()
                val title = titleElement?.attr("content") ?: document.title()
                val limitedUrl = if (url.length > 30) url.substring(0, 30) + "..." else url
                if (isLoginPage(document)) {
                    _pageTitle.postValue(limitedUrl)
                } else {
                    _pageTitle.postValue(title)
                }
            } catch (e: CancellationException) {
                Log.e("SharingHassan", "Coroutine 캔슬 오류", e)
                withContext(Dispatchers.Main) {
                    showToastMessage("페이지 제목을 불러올 수 없습니다.")
                }
                _pageTitle.postValue(null)
            } catch (e: Exception) {
                Log.e("SharingHassan", "예상치 못한 오류", e)
                withContext(Dispatchers.Main) {
                    showToastMessage("페이지 제목을 불러올 수 없습니다.")
                }
                _pageTitle.postValue(null)
            }
        }
    }

    private fun isLoginPage(document: Document): Boolean {
        return document.select("input[type=password]").isNotEmpty() ||
                document.title().contains("로그인", ignoreCase = true) ||
                document.body().text().contains("로그인", ignoreCase = true)
    }
}