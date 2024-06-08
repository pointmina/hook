package com.hanto.hook.urlHandler

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hanto.hook.R
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.databinding.ActivityUrlHandlingBinding
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PageDetailsDialog(val activity: AppCompatActivity, val title: String, val url: String) : Dialog(activity, R.style.DialogTheme) {
    private val multiChoiceList = linkedMapOf<String, Boolean>()
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityUrlHandlingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding = ActivityUrlHandlingBinding.inflate(activity.layoutInflater)
        val view = binding.root
        setContentView(view)

        val apiServiceManager = ApiServiceManager()
        val viewModelFactory = ViewModelFactory(apiServiceManager)
        viewModel = ViewModelProvider(activity, viewModelFactory).get(MainViewModel::class.java)

        viewModel.loadFindMyTags()

        val tagBox = binding.tvTag

        with(binding) {
            tvBookmark.text = " 훅 저장하기"
            editTextUrl.setText(url)
            editTextTitle.setText(title)
            editTextDescription.setText("")

            checkboxExpand.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    tagBox.visibility = android.view.View.VISIBLE
                } else {
                    tagBox.visibility = android.view.View.GONE
                }
            }

            tagBox.setOnClickListener {
                showTagSelectionDialog(tagBox)
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
            dismiss()
        }
    }

    private fun reload(){
        viewModel.loadFindMyTags()
        viewModel.tagData.observe(activity) { tagData ->
            tagData?.let {
                for (tag in tagData.tag) {
                    tag.displayName?.let { displayName ->
                        multiChoiceList[displayName] = false
                    }
                }
            }
        }
    }

    private fun showTagSelectionDialog(editTextTag: TextView) {
        val tags = multiChoiceList.keys.toTypedArray()
        val tagArray = Array(tags.size) { i -> tags[i] }

        val builder = AlertDialog.Builder(context)
        builder.setTitle("태그 선택")

        builder.setMultiChoiceItems(
            tagArray,
            multiChoiceList.values.toBooleanArray()
        ) { _, which, isChecked ->
            val selectedTag = multiChoiceList.keys.toTypedArray()[which]
            multiChoiceList[selectedTag] = isChecked
        }

        builder.setPositiveButton("OK") { dialog, _ ->
            val selectedTags = mutableListOf<String>()
            for ((tag, selected) in multiChoiceList) {
                if (selected) {
                    selectedTags.add("#$tag")
                }
            }
            editTextTag.text = selectedTags.joinToString("  ")
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.setNeutralButton("Add") { _, _ ->
            val editText = EditText(context)
            editText.hint = "태그 입력"
            val dialogBuilder = AlertDialog.Builder(context)
                .setTitle("태그 추가")
                .setView(editText)
                .setPositiveButton("추가") { _, _ ->
                    val newTag = editText.text.toString().trim()
                    if (newTag.isNotEmpty()) {
                        GlobalScope.launch(Dispatchers.Main) {
                            val response = viewModel.loadCreateTag(newTag)
                            reload()
                            multiChoiceList[newTag] = true
                            val selectedTags = mutableListOf<String>()
                            for ((tag, selected) in multiChoiceList) {
                                if (selected) {
                                    selectedTags.add("#$tag")
                                }
                            }
                            editTextTag.text = selectedTags.joinToString("  ")
                            showTagSelectionDialog(editTextTag)
                        }
                    } else {
                        Toast.makeText(context, "태그를 입력하세요.", Toast.LENGTH_SHORT).show()
                        showTagSelectionDialog(editTextTag)
                    }
                }
                .setNegativeButton("취소") { _, _ ->
                    showTagSelectionDialog(editTextTag)
                }
            val addDialog = dialogBuilder.create()
            addDialog.show()
        }

        val dialog = builder.create()
        dialog.show()
    }

    @SuppressLint("ServiceCast")
    private fun forceQuit() {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.killBackgroundProcesses(context.packageName)
        System.exit(0)
    }
}