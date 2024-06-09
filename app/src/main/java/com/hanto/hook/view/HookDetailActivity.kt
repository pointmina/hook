package com.hanto.hook.view

import android.content.ClipboardManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.hanto.hook.BaseActivity
import com.hanto.hook.R
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.databinding.ActivityHookDetailBinding
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory

class HookDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityHookDetailBinding

    private val apiServiceManager by lazy { ApiServiceManager() }
    private val viewModelFactory by lazy { ViewModelFactory(apiServiceManager) }
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java] }

    private var isUrlValid = true
    private var isTitleValid = true
    private val multiChoiceList = linkedMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHookDetailBinding.inflate(layoutInflater)
        val view = binding.root

        viewModel.loadFindMyTags()
        viewModel.tagData.observe(this) { tagData ->
            tagData?.let {
                for (tag in tagData.tag) {
                    tag.displayName?.let { displayName ->
                        if (!multiChoiceList.containsKey(displayName)) {
                            multiChoiceList[displayName] = false
                        }
                    }
                }
            }
        }
        setContentView(view)

        updateButtonState()

        binding.btPasteLink.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboard.hasPrimaryClip()) {
                val clipData = clipboard.primaryClip
                if (clipData != null && clipData.itemCount > 0) {
                    // 클립보드의 첫 번째 항목의 텍스트 데이터
                    val item = clipData.getItemAt(0)
                    val pasteData = item.text

                    if (pasteData != null && (pasteData.startsWith("http://") || pasteData.startsWith("https://"))) {
                        binding.tvHandedUrl.setText(pasteData)
                        Toast.makeText(this, "가장 최근에 복사한 URL을 가져왔어요!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "클립보드에 유효한 URL이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "클립보드가 비어 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivAppbarUrlHookDetailBackButton.setOnClickListener {
            finish()
        }

        val id = intent.getStringExtra("item_id")
        val title = intent.getStringExtra("item_title")
        val url = intent.getStringExtra("item_url")
        val description = intent.getStringExtra("item_description")
        val tags = intent.getStringArrayListExtra("item_tag_list")

        binding.tvHandedTitle.setText(title)
        binding.tvHandedUrl.setText(url)
        binding.tvHandedDesc.setText(description)
        binding.testId.text = id

        tags?.forEach { tag ->
            multiChoiceList[tag] = true
        }

        val tvTag = binding.tvTag
        val tagString = tags?.joinToString(" ") { "#$it " }
        binding.tvTag.text = tagString

        val limitString1 = "${binding.tvHandedTitle.text.length} / 120"
        val limitString2 = "${binding.tvHandedDesc.text.length} / 80"
        binding.tvLimit1.text = limitString1
        binding.tvLimit2.text = limitString2

        binding.tvHandedTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    binding.tvLimit1.text = "${s.length} / 120"

                    isTitleValid = s.toString().trim().isNotEmpty()
                    if (!isTitleValid) {
                        binding.tvGuideTitle.visibility = View.VISIBLE
                    } else {
                        binding.tvGuideTitle.visibility = View.GONE
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {
                updateButtonState()
            }
        })
        binding.tvHandedTitle.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.tvHandedDesc.requestFocus()
                true
            } else {
                false
            }
        }

        binding.tvHandedDesc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    binding.tvLimit2.text = "${s.length} / 80"
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.tvHandedUrl.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                isUrlValid = input.isNotBlank() && !input.contains(" ")

                if (!isUrlValid) {
                    binding.tvGuideUrl.visibility = View.VISIBLE
                } else {
                    binding.tvGuideUrl.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {
                updateButtonState()
            }
        })
        binding.tvHandedUrl.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.tvHandedUrl.windowToken, 0)
            } else {
                false
            }
        }

        tvTag.setOnClickListener {
            showTagSelectionDialog()
        }

        binding.hookEdit.setOnClickListener {
            val intId = (binding.testId.text as String?)?.toInt()
            val newTitle = binding.tvHandedTitle.text.toString()
            val newDesc = binding.tvHandedDesc.text.toString()
            val newUrl = binding.tvHandedUrl.text.toString()
            val selectedTags = binding.tvTag.text.split("  ").map { it.trim().replace("#", "") }
            val newTag = if (selectedTags.isEmpty() || selectedTags[0].isEmpty()) arrayListOf<String>() else ArrayList(selectedTags)

            if (intId != null) {
                viewModel.loadUpdateHook(intId, newTitle, newDesc, newUrl, newTag, suggestTags = false)
            }
            finish()
        }
    }

    private fun updateButtonState() {
        val isValid = isUrlValid && isTitleValid
        val finishButton = binding.hookEdit
        finishButton.isEnabled = isValid
        if (isValid) {
            finishButton.setBackgroundColor(resources.getColor(R.color.purple))
        } else {
            finishButton.setBackgroundColor(resources.getColor(R.color.gray_100))
        }
    }

    private fun showTagSelectionDialog() {
        val tags = multiChoiceList.keys.sorted().toTypedArray()
        val selectedItems = BooleanArray(tags.size) { i -> multiChoiceList[tags[i]] == true }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("태그 선택")

        builder.setMultiChoiceItems(tags, selectedItems) { _, which, isChecked ->
            val selectedTag = tags[which]
            multiChoiceList[selectedTag] = isChecked
        }

        builder.setPositiveButton("OK") { dialog, _ ->
            updateTagsInView()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.setNeutralButton("Add") { dialog, _ ->
            dialog.dismiss()
            showAddTagDialog()
        }

        builder.create().show()
    }

    private fun showAddTagDialog() {
        val editText = EditText(this)
        editText.hint = "태그 입력"

        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("태그 추가")
            .setView(editText)
            .setPositiveButton("추가") { dialog, _ ->
                val newTag = editText.text.toString().trim()
                if (newTag.isNotEmpty()) {
                    multiChoiceList[newTag] = true
                    updateTagsInView()
                } else {
                    Toast.makeText(this, "태그를 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }

        dialogBuilder.create().show()
    }

    private fun updateTagsInView() {
        val selectedTags = mutableListOf<String>()
        for ((tag, selected) in multiChoiceList) {
            if (selected) {
                selectedTags.add(tag)
            }
        }
        val sortedSelectedTags = selectedTags.sorted().map { "#$it" }
        binding.tvTag.text = sortedSelectedTags.joinToString("  ")
    }
}