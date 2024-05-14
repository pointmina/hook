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
import android.widget.ImageView
import com.hanto.hook.R
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat.finishAffinity
import com.bumptech.glide.Glide



class PageDetailsDialog(context: Context, val sharingActivity: Sharing, val title: String, val url: String) : Dialog(context, R.style.DialogTheme) {

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

        val editTextTag = findViewById<EditText>(R.id.tv_tag)
        val checkboxExpand = findViewById<CheckBox>(R.id.checkboxExpand)

        checkboxExpand?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                editTextTag?.visibility = View.VISIBLE
            } else {
                editTextTag?.visibility = View.GONE
            }
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
            // DataToJsonConverter 객체 생성
            val converter = DataToJsonConverter()

            // JSON 문자열로 변환
            val jsonString = converter.convertToJson(inputTitle, inputDescription, inputUrl, inputTag)

            // 출력
            dismiss()
            println(jsonString)

        }
    }
}

