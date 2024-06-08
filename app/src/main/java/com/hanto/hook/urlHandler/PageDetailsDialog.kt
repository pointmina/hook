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
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.hanto.hook.viewmodel.MainViewModel
import androidx.core.app.ActivityCompat.finishAffinity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.api.ErrorResponse
import com.hanto.hook.api.SuccessResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog


class PageDetailsDialog(context: Context, val sharingActivity: Sharing, val title: String, val url: String) : Dialog(context, R.style.DialogTheme) {
    private val multiChoiceList = linkedMapOf<String, Boolean>()


    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("다이얼로그 실행")
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.activity_url_handling, null) as ViewGroup
        setContentView(view)



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
            val tags = multiChoiceList.keys.toTypedArray()
            val tagArray = Array(tags.size) { i -> tags[i] }

            val builder = AlertDialog.Builder(context)
            builder.setTitle("태그 선택")

            builder.setMultiChoiceItems(
                tagArray,
                multiChoiceList.values.toBooleanArray()
            ) { dialogInterface, which, isChecked ->
                val selectedTag = multiChoiceList.keys.toTypedArray()[which]
                multiChoiceList[selectedTag] = isChecked
            }

            builder.setPositiveButton("OK") { dialog, which ->
                val selectedTags = mutableListOf<String>()
                for ((tag, selected) in multiChoiceList) {
                    if (selected) {
                        selectedTags.add("#$tag")
                    }
                }
                editTextTag.setText(selectedTags.joinToString("  "))
                dialog.dismiss()
            }

            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }

            builder.setNeutralButton("Add") { dialog, which ->
                val editText = EditText(context)
                editText.hint = "태그 입력"
                val dialogBuilder = AlertDialog.Builder(context)
                    .setTitle("태그 추가")
                    .setView(editText)
                    .setPositiveButton("추가") { dialog, which ->
                        val newTag = editText.text.toString().trim()
                        if (newTag.isNotEmpty()) {
                            multiChoiceList[newTag] = true
                            val selectedTags = mutableListOf<String>()
                            for ((tag, selected) in multiChoiceList) {
                                if (selected) {
                                    selectedTags.add("#$tag")
                                }
                            }
                            editTextTag.setText(selectedTags.joinToString("  "))
                        } else {
                            Toast.makeText(context, "태그를 입력하세요.", Toast.LENGTH_SHORT).show()
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton("취소") { dialog, which ->
                        dialog.dismiss()
                    }
                val dialog = dialogBuilder.create()
                dialog.show()
            }

            val dialog = builder.create()
            dialog.show()
        }




        val buttonCancel: TextView? = findViewById(R.id.btn_cancel)
        buttonCancel?.setOnClickListener {
            println("취소버튼클릭")
            dismiss()
            (context as? Activity)?.finishAffinity()
        }


        val buttonCreate: TextView? = findViewById(R.id.btn_create)
        buttonCreate?.setOnClickListener {
            val inputUrl = editTextUrl.text.toString()
            val inputTitle = editTextTitle.text.toString()
            val inputTag = editTextTag.text.toString()
            val inputDescription = editTextDescription.text.toString()
            println("생성버튼클릭")
            (context as? AppCompatActivity)?.finish()






            GlobalScope.launch(Dispatchers.Main) {
                val apiServiceManager = ApiServiceManager()
                val response = apiServiceManager.managerCreateHook(inputTitle, inputDescription, inputUrl, arrayListOf(inputTag))
                if (response is SuccessResponse) {
                    println("생성 성공: ${response.result?.message}")

                } else if (response is ErrorResponse) {
                    println("생성 실패: ${response.message}")
                }

                dismiss()
                (context as? AppCompatActivity)?.finish()
            }
            // 출력
            dismiss()


        }
    }

}

