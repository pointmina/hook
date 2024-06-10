package com.hanto.hook.urlHandler


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.util.Timer
import java.util.TimerTask


class Sharing : AppCompatActivity() {
    private lateinit var timer: Timer
    private val timeout = 5000L // 5초

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIncomingSharedContent()
    }

    //Intent 데이터 null 확인
    private fun handleIncomingSharedContent() {
        val receivedIntent: Intent? = intent
        val receivedAction: String? = receivedIntent?.action
        val receivedType: String? = receivedIntent?.type

        if (receivedAction == Intent.ACTION_SEND && receivedType == "text/plain") {
            val sharedText: String? = receivedIntent.getStringExtra(Intent.EXTRA_TEXT)
            val url = sharedText
            url?.let {
                startAutoCloseTimer() // 타이머 시작
                fetchDataFromUrl(it)
            } ?: run {
                println("Received URL is null")
            }
        } else {
            println("Unexpected intent received")
        }
    }

    //비동기 작업 완료 및 예외 처리
    private fun fetchDataFromUrl(url: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val (title, thumbnailUrl) = withContext(Dispatchers.IO) {
                    fetchOpenGraphInfoFromUrl(url)
                }
                title?.let {
                    openPageDetailsDialog(it, url)
                } ?: run {
                    println("Failed to fetch title")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //Exception 처리
    private suspend fun fetchOpenGraphInfoFromUrl(url: String): Pair<String?, String?> {
        var title: String? = null
        var thumbnailUrl: String? = null

        try {
            val document = Jsoup.connect(url).get()

            // Get page title
            val ogTitleElement = document.select("meta[property=og:title]").first()
            title = ogTitleElement?.attr("content")

            // Get thumbnail URL
            val ogImageElement = document.select("meta[property=og:image]").first()
            thumbnailUrl = ogImageElement?.attr("content")
        } catch (e: Exception) {
            // Handle exceptions appropriately, such as showing error messages
            e.printStackTrace()
            println("Error fetching OpenGraph info: ${e.message}")
        }

        return Pair(title, thumbnailUrl)
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

    private fun openPageDetailsDialog(title: String, url: String) {
        runOnUiThread {
            val dialog = PageDetailsDialog(this, title, url)
            dialog.setOnDismissListener {
                this.finish() // Sharing 액티비티 종료
            }
            dialog.show()
        }
    }
}