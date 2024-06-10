package com.hanto.hook.urlHandler

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.coroutines.cancellation.CancellationException

class Sharing : AppCompatActivity() {
    private val _pageTitle = MutableLiveData<String?>()
    private val pageTitle: LiveData<String?>
        get() = _pageTitle

    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webCrawlingHandler()
    }

    private fun webCrawlingHandler() {
        val receivedIntent: Intent? = intent
        val receivedAction: String? = receivedIntent?.action
        val receivedType: String? = receivedIntent?.type

        if (receivedAction == Intent.ACTION_SEND && receivedType == "text/plain") {
            val sharedText: String? = receivedIntent.getStringExtra(Intent.EXTRA_TEXT)
            sharedText?.let { originUrl ->
                lifecycleScope.launch {
                    loadWebTitle(originUrl)

                }
                showToastMessage("자동 훅 생성 시작!")
                pageTitle.observe(this) { pageTitle ->
                    pageTitle?.let { title ->
                        toast?.cancel() // 토스트 메시지 취소
                        openPageDetailsDialog(title, originUrl)
                        Toast.makeText(this@Sharing, "현재 페이지로 훅을 만들었어요!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showToastMessage(message: String) {
        toast?.cancel() // 이전 토스트 메시지 취소
        toast = Toast.makeText(this@Sharing, message, Toast.LENGTH_LONG)
        toast?.show()
    }

    private fun openPageDetailsDialog(title: String, url: String) {
        val dialog = PageDetailsDialog(this, title, url)
        dialog.setOnDismissListener {
            this.finish()
        }
        dialog.show()
    }

    private suspend fun loadWebTitle(url: String) {
        withContext(Dispatchers.IO) {
            try {
                val document: Document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .get()
                val titleElement = document.select("meta[property=og:title]").first()
                val title = titleElement?.attr("content")
                val limitedUrl = if (url.length > 30) url.substring(0, 30) + "..." else url
                if (isLoginPage(document)) {
                    _pageTitle.postValue(limitedUrl)
                } else {
                    _pageTitle.postValue(title)
                }
            } catch (e: CancellationException) {
                Log.e("SharingHassan", "Coroutine 캔슬 오류", e)
            } catch (e: Exception) {
                Log.e("SharingHassan", "예상치 못한 오류", e)
            }
        }
    }

    private fun isLoginPage(document: Document): Boolean {
        return document.select("input[type=password]").isNotEmpty() ||
                document.title().contains("로그인", ignoreCase = true) ||
                document.body().text().contains("로그인", ignoreCase = true)
    }
}