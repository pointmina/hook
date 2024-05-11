package com.hanto.hook.view


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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hanto.hook.R
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.api.SuccessResponse
import com.hanto.hook.databinding.ActivityAddHookBinding
import com.hanto.hook.viewmodel.HookViewModel
import com.hanto.hook.viewmodel.ViewModelFactory

class AddHookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddHookBinding
    private var isExpanded = false
    private lateinit var viewModel: HookViewModel

    private val multiChoiceList = linkedMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ApiServiceManager 인스턴스 생성 (필요에 따라서)
        val apiServiceManager = ApiServiceManager()

        // HookViewModel 인스턴스 생성
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiServiceManager)
        ).get(HookViewModel::class.java)

        viewModel.loadFindMyDisplayName()

        viewModel.tagDisplayNames.observe(this, Observer { tagDisplayNames ->
            tagDisplayNames?.let {
                for (tag in tagDisplayNames) multiChoiceList[tag] = false
            }

        })

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
        val addNewHook = binding.ivAddNewHook
        val tvTitle = binding.tvUrlTitle

        backButton.setOnClickListener {
            onBackPressed()
        }

        tagSelect.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)

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

            //add버튼 누르면 태그 항목에 추가
            builder.setNeutralButton("Add") { dialog, which ->
                // 추가: 새로운 항목 추가 기능 구현
                val editText = EditText(this)
                editText.hint = "태그 입력"
                val dialogBuilder = AlertDialog.Builder(this)
                    .setTitle("태그 추가")
                    .setView(editText)
                    .setPositiveButton("추가") { dialog, which ->
                        val newTag = editText.text.toString().trim()
                        if (newTag.isNotEmpty()) {
                            multiChoiceList[newTag] = true
                            val selectedTags = mutableListOf<String>()
                            for ((tag, selected) in multiChoiceList) {
                                if (selected) {
                                    selectedTags.add("#$tag") // #을 붙여 선택된 태그를 리스트에 추가합니다.
                                }
                            }
                            // 추가: 선택된 태그를 containerTag에 표시
                            containerTag.text = selectedTags.joinToString("  ")
                        } else {
                            Toast.makeText(this, "태그를 입력하세요.", Toast.LENGTH_SHORT).show()
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