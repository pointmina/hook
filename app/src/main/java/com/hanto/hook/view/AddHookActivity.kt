package com.hanto.hook.view

import android.content.ClipboardManager
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    private var isUrlValid = false
    private var isTitleValid = false
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

        binding.ivUrlLink.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboard.hasPrimaryClip()) {
                val clipData = clipboard.primaryClip
                if (clipData != null && clipData.itemCount > 0) {
                    // 클립보드의 첫 번째 항목의 텍스트 데이터
                    val item = clipData.getItemAt(0)
                    val pasteData = item.text

                    if (pasteData != null && (pasteData.startsWith("http://") || pasteData.startsWith(
                            "https://"
                        ))
                    ) {
                        binding.tvUrlLink.setText(pasteData)
                        Toast.makeText(this, "가장 최근에 복사한 URL을 가져왔어요!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "클립보드에 유효한 URL이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "클립보드가 비어 있습니다.", Toast.LENGTH_SHORT).show()
            }
        } // 클립보드에서 바로 붙여넣기

        binding.ivAppbarBackButton.setOnClickListener {
            finish()
        } // 앱바 - 뒤로 가기 버튼

        binding.tvLimit1.text = "${binding.tvUrlTitle.text.length} / 80"
        binding.tvLimit2.text = "${binding.tvUrlDescription.text.length} / 80"

        binding.tvUrlTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    binding.tvLimit1.text = "${s.length} / 80"
                }
            }

            override fun afterTextChanged(s: Editable?) {
                isTitleValid = s.toString().trim().isNotEmpty()
                updateButtonState()
            }
        })

        binding.tvUrlDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    binding.tvLimit2.text = "${s.length} / 80"
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.tvUrlLink.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                isUrlValid =
                    input.isNotBlank() && (input.startsWith("http://") || input.startsWith("https://")) && !input.contains(
                        " "
                    )
                if (!isUrlValid) {
                    binding.tvGuide.visibility = View.VISIBLE
                } else {
                    binding.tvGuide.visibility = View.GONE
                }
                updateButtonState() // 버튼 상태 업데이트
            }
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
            viewModel.createHookSuccessData.observe(this) { createHookSuccessData ->
                if (createHookSuccessData != null) {
                    Toast.makeText(this, "훅이 추가됐어요!", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    }, 500)
                }
            }
            viewModel.createFailData.observe(this) { createFailData ->
                if (createFailData != null) {
                    val errorMessage = createFailData.message.joinToString(separator = "\n")
                    Toast.makeText(this@AddHookActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    /*        binding.tvUrlLink.setOnClickListener {
    showKeyboardAndFocus()
}*/
    private fun updateButtonState() {
        val isValid = isUrlValid && isTitleValid
        val finishButton = binding.ivAddNewHook
        finishButton.isEnabled = isValid

        if (isValid) {
            finishButton.setBackgroundColor(getResources().getColor(R.color.purple))
            /*finishButton.setBackgroundResource(R.drawable.button_border)*/
        } else {
            finishButton.setBackgroundColor(getResources().getColor(R.color.gray_100))
//            finishButton.setBackgroundResource(R.drawable.button_border)
        }
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