package com.hanto.hook.view

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.databinding.ActivityHookDetailBinding
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory

class HookDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHookDetailBinding
    private lateinit var viewModel: MainViewModel

    private val multiChoiceList = linkedMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ApiServiceManager 인스턴스 생성 (필요에 따라서)
        val apiServiceManager = ApiServiceManager()

        // HookViewModel 인스턴스 생성
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiServiceManager)
        ).get(MainViewModel::class.java)

        viewModel.loadFindMyTags()



        binding = ActivityHookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("item_title")
        val url = intent.getStringExtra("item_url")
        val description = intent.getStringExtra("item_description")
        val tags = intent.getStringArrayListExtra("item_tag_list")
        val backButton = binding.ivAppbarUrlHookDetailBackButton
        val tvTag = binding.tvTag

        tags?.forEach { tag ->
            multiChoiceList[tag] = true
        }


        viewModel.tagDisplayNames.observe(this, Observer { tagDisplayNames ->
            tagDisplayNames?.let {
                for (tag in tagDisplayNames) {
                    // 중복되는 태그가 있으면 해당 태그를 true로 설정
                    if (multiChoiceList.containsKey(tag)) {
                        multiChoiceList[tag] = true
                    } else {
                        multiChoiceList[tag] = false
                    }
                }
            }
        })


        backButton.setOnClickListener {
            onBackPressed()
        }

        binding.tvHandedTitle.setText(title)
        binding.tvHandedUrl.setText(url)
        binding.tvHandedDesc.setText(description)

        // 태그 리스트를 문자열로 변환
        val tagString = tags?.joinToString(" ") { "#$it " }

        binding.tvTag.text = tagString



        binding.tvLimit1.text = "${binding.tvHandedTitle.text.length} / 80"
        binding.tvLimit2.text = "${binding.tvHandedDesc.text.length} / 80"


        binding.tvHandedTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int, count:
                Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    binding.tvLimit1.text = "${s.length} / 80"
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.tvHandedDesc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    binding.tvLimit2.text = "${s.length} / 80"
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        tvTag.setOnClickListener {
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
                tvTag.text = selectedTags.joinToString("  ")
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
                            tvTag.text = selectedTags.joinToString("  ")
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

    }
}
