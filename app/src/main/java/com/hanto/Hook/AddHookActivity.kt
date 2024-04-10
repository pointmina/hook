package com.hanto.Hook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.hanto.Hook.databinding.ActivityAddHookBinding

class AddHookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddHookBinding
    private var isExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHookBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 뷰 바인딩을 통해 뷰들을 참조
        val downArrow = binding.ivDownArrow
        val tvUrlDescription = binding.tvUrlDescription
        val tvTag = binding.tvTag
        val containerTag = binding.containerTag
        val containerInfoEtc = binding.containerLinkInfoEtc
        val urlLink = binding.tvUrlLink

        // down_arrow 이미지뷰 클릭 리스너 설정 -> containerInfoEtc로 바꿈 터치 부분이 너무 작아서 터치가 힘듦..
        containerInfoEtc.setOnClickListener {
            toggleExpandCollapse(tvUrlDescription, tvTag, containerTag, downArrow)
        }

        //urlLink 클릭 리스너 설정
        urlLink.setOnClickListener {
            showKeyboardAndFocus(urlLink)
        }


    }

    private fun toggleExpandCollapse(
        tvUrlDescription: TextView,
        tvTag: TextView,
        containerTag: TextView,
        downArrow: ImageView
    ) {
        isExpanded = !isExpanded

        if (isExpanded) {
            // 확장 상태일 때
            tvUrlDescription.visibility = View.VISIBLE
            tvTag.visibility = View.VISIBLE
            containerTag.visibility = View.VISIBLE

            // down_arrow 이미지를 up_arrow 이미지로 변경
            downArrow.setImageResource(R.drawable.ic_up_arrow)
        } else {
            // 축소 상태일 때
            tvUrlDescription.visibility = View.INVISIBLE
            tvTag.visibility = View.INVISIBLE
            containerTag.visibility = View.INVISIBLE

            // up_arrow 이미지를 down_arrow 이미지로 변경
            downArrow.setImageResource(R.drawable.ic_down_arrow)
        }
    }

    private fun showKeyboardAndFocus(editText: EditText) {
        editText.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
}
