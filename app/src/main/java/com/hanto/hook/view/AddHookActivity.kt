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
import com.hanto.hook.databinding.ActivityAddHookBinding
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory

class AddHookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddHookBinding
    private var isExpanded = false
    private lateinit var viewModel: MainViewModel

    private val multiChoiceList = linkedMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHookBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // ApiServiceManager 인스턴스 생성 (필요에 따라서)
        val apiServiceManager = ApiServiceManager()

        // HookViewModel 인스턴스 생성
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiServiceManager)
        ).get(MainViewModel::class.java)

        viewModel.loadFindMyTags()

        viewModel.tagDisplayNames.observe(this, Observer { tagDisplayNames ->
            tagDisplayNames?.let {
                for (tag in tagDisplayNames) multiChoiceList[tag] = false
            }

        })

        val downArrow = binding.ivDownArrow
        val containerTag = binding.containerTag
        val containerInfoEtc = binding.containerLinkInfoEtc
        val tagSelect = binding.containerTag
        val tvLimit2 = binding.tvLimit2
        val backButton = binding.ivAppbarBackButton

        val tvTitle = binding.tvUrlTitle
        val tvUrlDescription = binding.tvUrlDescription
        val urlLink = binding.tvUrlLink
        val tvTag = binding.tvTag

        binding.ivAddNewHook.setOnClickListener {
            val title = tvTitle.text.toString()
            val description = tvUrlDescription.text.toString()
            val url = urlLink.text.toString()
            val tagString = containerTag.text.toString()
            val tagList = tagString.trim()
                .split(",")
                .filter { it.isNotEmpty() }
                .map { it.replace("#", "").trim() }
            val tag = ArrayList<String>(tagList)

            viewModel.loadCreateHook(title, description, url, tag)
            Toast.makeText(this, tag.joinToString(", "), Toast.LENGTH_LONG).show()
            finish()
        }


        backButton.setOnClickListener {
            onBackPressed()
        }

        binding.tvLimit1.text = "${tvTitle.text.length} / 120"
        tvLimit2.text = "${tvUrlDescription.text.length} / 80"

        tvTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    binding.tvLimit1.text = "${s.length} / 120"
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        tvUrlDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    tvLimit2.text = "${s.length} / 80"
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

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
                val dialogBuilder = AlertDialog.Builder(this,R.style.MyCheckBox)
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
            toggleExpandCollapse(tvUrlDescription, tvTag, containerTag, downArrow, tvLimit2)
        }

        urlLink.setOnClickListener {
            showKeyboardAndFocus(urlLink)
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

    private fun showKeyboardAndFocus(editText: EditText) {
        editText.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
}