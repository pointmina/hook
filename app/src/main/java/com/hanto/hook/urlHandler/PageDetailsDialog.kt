package com.hanto.hook.urlHandler

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import com.hanto.hook.R
import com.hanto.hook.databinding.ActivityUrlHandlingBinding

class PageDetailsDialog(context: Context, val title: String, val url: String) :
    Dialog(context, R.style.DialogTheme) {

    private lateinit var binding: ActivityUrlHandlingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding = ActivityUrlHandlingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val tagBox = binding.tvTag


        with(binding) {
            tvBookmark.text = " 훅 저장하기"
            editTextUrl.setText(url)
            editTextTitle.setText(title)
            editTextDescription.setText("")

            checkboxExpand.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    tagBox.visibility = View.VISIBLE
                } else {
                    tagBox.visibility = View.GONE
                }
            }

            btnCancel.setOnClickListener {
                dismiss()
                (context as? Sharing)?.finishAffinity()
                forceQuit()
            }

            btnCreate.setOnClickListener {
                val inputUrl = editTextUrl.text.toString()
                val inputTitle = editTextTitle.text.toString()
                val inputTag = tagBox.text.toString()
                val inputDescription = editTextDescription.text.toString()

                val converter = DataToJsonConverter()

                val jsonString = converter.convertToJson(inputTitle, inputDescription, inputUrl, inputTag)

                dismiss()
                (context as? Sharing)?.finishAffinity()
                forceQuit()
            }


        }

    }
    @SuppressLint("ServiceCast")
    private fun forceQuit() {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.killBackgroundProcesses(context.packageName)
        System.exit(0)
    }
}
