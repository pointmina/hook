package com.hanto.hook.urlHandler


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.coroutines.cancellation.CancellationException

class Sharing : AppCompatActivity() {
    private val apiServiceManager by lazy { ApiServiceManager() }
    private val viewModelFactory by lazy { ViewModelFactory(apiServiceManager) }
    private val viewModel: MainViewModel by viewModels { viewModelFactory }
    private val _pageTitle = MutableLiveData<String?>()
    private val pageTitle: LiveData<String?>
        get() = _pageTitle

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
                pageTitle.observe(this) { pageTitle ->
                    pageTitle?.let { title ->
                        openPageDetailsDialog(title, originUrl)
                    }
                }
            }
        }
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
                    //크롤링 오류 케이스 - userAgent가 명시 안되어 있으면 요청 거부하는 url
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
//                Log.e("SharingHassan", "Coroutine 캔슬 오류", e)
            } catch (e: Exception) {
//                Log.e("SharingHassan", "예상치 못한 오류", e)
            }
        }
    }

    private fun isLoginPage(document: Document): Boolean {
        return document.select("input[type=password]").isNotEmpty() ||
                document.title().contains("로그인", ignoreCase = true) ||
                document.body().text().contains("로그인", ignoreCase = true)
    }
}