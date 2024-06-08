package com.hanto.hook.view

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory

class AddHookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddHookBinding

    private val apiServiceManager by lazy { ApiServiceManager() }
    private val viewModelFactory by lazy { ViewModelFactory(apiServiceManager) }
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java) }

    private var isExpanded = false
    private val multiChoiceList = linkedMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHookBinding.inflate(layoutInflater)
        val view = binding.root


        viewModel.loadFindMyTags()
        viewModel.tagData.observe(this, Observer { tagData ->
            tagData?.let {
                for (tag in tagData.tag) {
                    tag.displayName?.let { displayName ->
                        multiChoiceList[displayName] = false
                    }
                }
            }
        })

        setContentView(view)

        binding.ivAppbarBackButton.setOnClickListener {
            finish()
        } // 앱바 - 뒤로 가기 버튼

        // 59~80: 글자 수 확인
        binding.tvLimit1.text = "${binding.tvUrlTitle.text.length} / 80"
        binding.tvLimit2.text = "${binding.tvUrlDescription.text.length} / 80"

        binding.tvUrlTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    binding.tvLimit1.text = "${s.length} / 80"
                }
            }
            override fun afterTextChanged(s: Editable?) { }
        })

        binding.tvUrlDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    binding.tvLimit2.text = "${s.length} / 80"
                }
            }
            override fun afterTextChanged(s: Editable?) { }
        })

        // 82~148: 태그 선택
        binding.containerTag.setOnClickListener {
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
                binding.containerTag.text = selectedTags.joinToString("  ")
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
                            binding.containerTag.text = selectedTags.joinToString("  ")
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

        // 더보기 뷰
        binding.containerLinkInfoEtc.setOnClickListener {
            val tvUrlDescription = binding.tvUrlDescription
            val tvTag = binding.tvTag
            val containerTag = binding.containerTag
            val downArrow = binding.ivDownArrow
            val tvLimit2 = binding.tvLimit2
            toggleExpandCollapse(tvUrlDescription, tvTag, containerTag, downArrow, tvLimit2)
        }

        binding.ivAddNewHook.setOnClickListener {
            val title = binding.tvUrlTitle.text.toString()
            val description = binding.tvUrlDescription.text.toString()
            val url = binding.tvUrlLink.text.toString()
            val tags = ArrayList(binding.containerTag.text.split("  ")
                .map { it.trim().replace("#", "") })

            viewModel.loadCreateHook(title, description, url, tags)
            finish()
        }

/*        binding.tvUrlLink.setOnClickListener {
            showKeyboardAndFocus()
        }*/
    }
    private fun toggleExpandCollapse(
        tvUrlDescription: TextView,
        tvTag: TextView,
        containerTag: TextView,
        downArrow: ImageView,
        tvLimit2: TextView
    ) {
        isExpanded = !isExpanded

        if (isExpanded) {
            tvUrlDescription.visibility = View.VISIBLE
            tvTag.visibility = View.VISIBLE
            containerTag.visibility = View.VISIBLE
            downArrow.setImageResource(R.drawable.ic_up_arrow)
            tvLimit2.visibility = View.VISIBLE
        } else {
            tvUrlDescription.visibility = View.INVISIBLE
            tvTag.visibility = View.INVISIBLE
            containerTag.visibility = View.INVISIBLE
            downArrow.setImageResource(R.drawable.ic_down_arrow)
            tvLimit2.visibility = View.INVISIBLE
        }
    }

/*    private fun showKeyboardAndFocus(editText: EditText) {
        editText.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }*/
}