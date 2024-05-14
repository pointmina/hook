package com.hanto.hook.urlHandler


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup


class Sharing : AppCompatActivity() {

    companion object {
        lateinit var instance: Sharing
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        instance = this
        println("handleIncomingSharedContent실행")
        handleIncomingSharedContent()
    }

    private fun handleIncomingSharedContent() {
        val receivedIntent: Intent? = intent
        val receivedAction: String? = receivedIntent?.action
        val receivedType: String? = receivedIntent?.type

        if (receivedAction == Intent.ACTION_SEND && receivedType == "text/plain") {
            val sharedText: String? = receivedIntent.getStringExtra(Intent.EXTRA_TEXT)
            val url = sharedText
            println("fetchDataFromUrl 실행")
            url?.let { fetchDataFromUrl(it) }
        }
    }

    private fun fetchDataFromUrl(url: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val (title, thumbnailUrl) = withContext(Dispatchers.IO) {
                println("fetchOpenGraphInfoFromUrl 실행")
                fetchOpenGraphInfoFromUrl(url)
            }
            println("openPageDetailsDialog 실행")
            title?.let { openPageDetailsDialog(it, url) }
        }
    }

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
        }

        return Pair(title, thumbnailUrl)
    }

    private fun openPageDetailsDialog(title: String, url: String) {
        val dialog = PageDetailsDialog(this, this, title, url)
        dialog.show()

    }
}
