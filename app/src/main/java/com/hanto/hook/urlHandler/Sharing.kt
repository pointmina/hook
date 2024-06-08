package com.hanto.hook.urlHandler

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.lang.ref.WeakReference

class Sharing : AppCompatActivity() {

    companion object {
        private var instance: WeakReference<Sharing>? = null

        fun getInstance(): Sharing? {
            return instance?.get()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = WeakReference(this)
        handleIncomingSharedContent()
    }

    private fun handleIncomingSharedContent() {
        val receivedIntent: Intent? = intent
        val receivedAction: String? = receivedIntent?.action
        val receivedType: String? = receivedIntent?.type

        if (receivedAction == Intent.ACTION_SEND && receivedType == "text/plain") {
            val sharedText: String? = receivedIntent.getStringExtra(Intent.EXTRA_TEXT)
            val url = sharedText
            url?.let { fetchDataFromUrl(it) }
        }
    }

    private fun fetchDataFromUrl(url: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val title = withContext(Dispatchers.IO) {
                fetchOpenGraphTitleFromUrl(url)
            }
            title?.let { openPageDetailsDialog(it, url) }
        }
    }

    private fun fetchOpenGraphTitleFromUrl(url: String): String? {
        var title: String? = null

        try {
            val document = Jsoup.connect(url).get()
            val ogTitleElement = document.select("meta[property=og:title]").first()
            title = ogTitleElement?.attr("content")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return title
    }

    private fun openPageDetailsDialog(title: String, url: String) {
        val dialog = PageDetailsDialog(this, title, url)
        dialog.show()
    }
}
