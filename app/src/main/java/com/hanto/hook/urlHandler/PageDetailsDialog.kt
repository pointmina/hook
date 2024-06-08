package com.hanto.hook.urlHandler


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import com.hanto.hook.R
import android.view.Window
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.api.ErrorResponse
import com.hanto.hook.api.SuccessResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog

class PageDetailsDialog(val activity: AppCompatActivity, val sharingActivity: Sharing, val title: String, val url: String) : Dialog(activity, R.style.DialogTheme) {
    private val multiChoiceList = linkedMapOf<String, Boolean>()
    private lateinit var viewModel: MainViewModel

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("다이얼로그 실행")
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.activity_url_handling, null) as ViewGroup
        setContentView(view)

        val apiServiceManager = ApiServiceManager()
        val viewModelFactory = ViewModelFactory(apiServiceManager)
        viewModel = ViewModelProvider(activity, viewModelFactory).get(MainViewModel::class.java)

        reload()

        val textViewBookmark = findViewById<TextView>(R.id.tv_bookmark)
        textViewBookmark.text = " 훅 저장하기"

        val editTextUrl = findViewById<EditText>(R.id.editTextUrl)
        editTextUrl.setText(url)

        val editTextTitle = findViewById<EditText>(R.id.editTextTitle)
        editTextTitle.setText(title)

        val editTextDescription = findViewById<EditText>(R.id.editTextDescription)
        editTextDescription.setText("")

        val editTextTag = findViewById<TextView>(R.id.tv_tag)
        val checkboxExpand = findViewById<CheckBox>(R.id.checkboxExpand)

        checkboxExpand?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                editTextTag?.visibility = View.VISIBLE
            } else {
                editTextTag?.visibility = View.GONE
            }
        }

        editTextTag.setOnClickListener {
            showTagSelectionDialog(editTextTag)
        }

        val buttonCancel: TextView? = findViewById(R.id.btn_cancel)
        buttonCancel?.setOnClickListener {
            println("취소버튼클릭")
            dismiss()
            (context as? Activity)?.finishAndRemoveTask()
        }

        val buttonCreate: TextView? = findViewById(R.id.btn_create)
        buttonCreate?.setOnClickListener {
            val inputUrl = editTextUrl.text.toString()
            val inputTitle = editTextTitle.text.toString()
            val inputTags = ArrayList(editTextTag.text.split("  ")
                .map { it.trim().replace("#", "") })
            val inputDescription = editTextDescription.text.toString()
            println("생성버튼클릭")
            (context as? AppCompatActivity)?.finish()

            GlobalScope.launch(Dispatchers.Main) {
                val apiServiceManager = ApiServiceManager()
                val response = apiServiceManager.managerCreateHook(inputTitle, inputDescription, inputUrl, inputTags)
                if (response is SuccessResponse) {
                    println("생성 성공: ${response.result?.message}")
                } else if (response is ErrorResponse) {
                    println("생성 실패: ${response.message}")
                }

                dismiss()
                (context as? Activity)?.finishAndRemoveTask()
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

}