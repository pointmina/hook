package com.hanto.Hook

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hanto.Hook.databinding.ActivityAddHookBinding

@Suppress("DEPRECATION")
class AddHookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddHookBinding
    private var isExpanded = false

    private val multiChoiceList = linkedMapOf(
        "블로그" to false,
        "음식" to false,
        "학교" to false,
        "응애" to false,
        "게임" to false,
        "모델" to false,
        "시험" to false,
        "프로젝트" to false,
        "테크" to false,
        "개발" to false,
        "운동" to false,
        "쇼핑" to false,
        "여행" to false,
        "정보" to false,
        "강의" to false,
        "자격증" to false,
        "햄스터" to false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddHookBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val downArrow = binding.ivDownArrow
        val tvUrlDescription = binding.tvUrlDescription
        val tvTag = binding.tvTag
        val containerTag = binding.containerTag
        val containerInfoEtc = binding.containerLinkInfoEtc
        val urlLink = binding.tvUrlLink
        val tagSelect = binding.containerTag
        val backButton = binding.ivAppbarBackButton


        backButton.setOnClickListener {
            onBackPressed()

        }

        tagSelect.setOnClickListener {
            val builder = AlertDialog.Builder(this)



            builder.setTitle("태그 선택")


            builder.setMultiChoiceItems(
                multiChoiceList.keys.toTypedArray(),
                multiChoiceList.values.toBooleanArray()
            ) { dialogInterface: DialogInterface, which: Int, isChecked: Boolean ->
                multiChoiceList[multiChoiceList.keys.toTypedArray()[which]] = isChecked
            }




            builder.setPositiveButton("ok") { dialog, id ->
                val selectedTags = mutableListOf<String>()
                for ((tag, selected) in multiChoiceList) {
                    if (selected) {
                        selectedTags.add(tag)
                    }
                }
                tvTag.text = selectedTags.joinToString(", ")
                // 추가: 선택된 태그를 containerTag에 표시
                containerTag.text = selectedTags.joinToString(", ")
                Log.d("Selected Items", multiChoiceList.toString())
                dialog.dismiss()
            }

            builder.setNegativeButton("cancel") { dialog, id ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        containerTag.setOnClickListener {
            val tags = multiChoiceList.keys.toTypedArray()
            val tagArray = Array(tags.size) { i -> tags[i] }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("태그 선택")

            builder.setMultiChoiceItems(
                tagArray,
                multiChoiceList.values.toBooleanArray()
            ) { dialogInterface: DialogInterface, which: Int, isChecked: Boolean ->
                val selectedTag = multiChoiceList.keys.toTypedArray()[which]
                multiChoiceList[selectedTag] = isChecked
            }

            builder.setPositiveButton("OK") { dialog, which ->
                val selectedTags = mutableListOf<String>()
                for ((tag, selected) in multiChoiceList) {
                    if (selected) {
                        selectedTags.add("#$tag") // #을 붙여 선택된 태그를 리스트에 추가합니다.
                    }
                }
                // 추가: 선택된 태그를 containerTag에 표시
                containerTag.text = selectedTags.joinToString("  ")
                dialog.dismiss()
            }

            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()


            }



            val dialog = builder.create()
            dialog.show()
        }

        containerInfoEtc.setOnClickListener {
            toggleExpandCollapse(tvUrlDescription, tvTag, containerTag, downArrow)
        }

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
            tvUrlDescription.visibility = View.VISIBLE
            tvTag.visibility = View.VISIBLE
            containerTag.visibility = View.VISIBLE
            downArrow.setImageResource(R.drawable.ic_up_arrow)
        } else {
            tvUrlDescription.visibility = View.INVISIBLE
            tvTag.visibility = View.INVISIBLE
            containerTag.visibility = View.INVISIBLE
            downArrow.setImageResource(R.drawable.ic_down_arrow)
        }
    }


    private fun showKeyboardAndFocus(editText: EditText) {
        editText.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
}
