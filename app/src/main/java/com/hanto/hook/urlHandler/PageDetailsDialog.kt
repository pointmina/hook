package com.hanto.hook.urlHandler

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
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

class PageDetailsDialog : Dialog {

    private lateinit var activity: AppCompatActivity
    private lateinit var title: String
    private lateinit var url: String
    private val multiChoiceList = linkedMapOf<String, Boolean>()
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityUrlHandlingBinding

    constructor(context: AppCompatActivity) : super(context, R.style.DialogTheme)

    constructor(context: AppCompatActivity, title: String, url: String) : super(
        context,
        R.style.DialogTheme
    ) {
        this.activity = context
        this.title = title
        this.url = url
    }

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        if (!::activity.isInitialized || !::title.isInitialized || !::url.isInitialized) {
            return
        }

        binding = ActivityUrlHandlingBinding.inflate(activity.layoutInflater)
        val view = binding.root
        setContentView(view)

        val apiServiceManager = ApiServiceManager()
        val viewModelFactory = ViewModelFactory(apiServiceManager)
        viewModel = ViewModelProvider(activity, viewModelFactory)[MainViewModel::class.java]

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

        var isAutoTagTure = false

        with(binding) {
            tvBookmark.text = " 훅 저장하기"
            editTextUrl.setText(url)
            editTextTitle.setText(title)
            editTextDescription.setText("")

            checkboxExpand.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    tvTag.visibility = android.view.View.VISIBLE
                } else {
                    tvTag.visibility = android.view.View.GONE
                }
            }

            checkBoxAutoTag.setOnCheckedChangeListener { _, isChecked ->
                isAutoTagTure = isChecked
            }

            tvTag.setOnClickListener {
                showTagSelectionDialog(tvTag)
            }

            btnCancel.setOnClickListener {
                dismiss()
                (context as? Activity)?.finishAndRemoveTask()
            }

            val buttonCreate: TextView? = findViewById(R.id.btn_create)
            buttonCreate?.setOnClickListener {
                val inputUrl = editTextUrl.text.toString()
                val inputTitle = editTextTitle.text.toString()
                val inputTags = ArrayList(tvTag.text.split("  ")
                    .map { it.trim().replace("#", "") }
                    .filter { it.isNotEmpty() })
                val inputDescription = editTextDescription.text.toString()

                val inputSuggestTag = isAutoTagTure
                println("생성버튼클릭")

                viewModel.loadWebCreateHook(
                    inputTitle,
                    inputDescription,
                    inputUrl,
                    inputTags,
                    inputSuggestTag
                )
                viewModel.successData.observe(activity) { successData ->
                    successData?.let {
                        Log.d("인브라우저 훅 생성 페이지", "요청완료,자동생성:${inputSuggestTag}")
                        Toast.makeText(activity, "저장 완료!", Toast.LENGTH_SHORT).show()
                    }
                }
                dismiss()
                (context as? Activity)?.finishAndRemoveTask()
            }
        }
    }

    private fun showTagSelectionDialog(editTextTag: TextView) {
        val tags = multiChoiceList.keys.sorted().toTypedArray()
        val checkedItems = BooleanArray(tags.size) { i -> multiChoiceList[tags[i]] == true }

        val builder = AlertDialog.Builder(context)
        builder.setTitle("태그 선택")

        builder.setMultiChoiceItems(
            tags,
            checkedItems
        ) { _, which, isChecked ->
            multiChoiceList[tags[which]] = isChecked
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
                            multiChoiceList[newTag] = true
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
}
