package com.hanto.hook.view

import android.content.ClipboardManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
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
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }
    private var isUrlValid = false
    private var isTitleValid = false
    private val multiChoiceList = linkedMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHookDetailBinding.inflate(layoutInflater)
        val view = binding.root

        viewModel.loadFindMyTags()
        viewModel.tagData.observe(this, Observer { tagData ->
            tagData?.let {
                for (tag in tagData.tag) {
                    tag.displayName?.let { displayName ->
                        if (!multiChoiceList.containsKey(displayName)) {
                            multiChoiceList[displayName] = false
                        }
                    }
                }
            }
        })
        setContentView(view)

        val id = intent.getStringExtra("item_id")
        val title = intent.getStringExtra("item_title")
        val url = intent.getStringExtra("item_url")
        val description = intent.getStringExtra("item_description")
        val tags = intent.getStringArrayListExtra("item_tag_list")

        val backButton = binding.ivAppbarUrlHookDetailBackButton
        val tvTag = binding.tvTag

        tags?.forEach { tag ->
            multiChoiceList[tag] = true
        }

        binding.btPasteLink.setOnClickListener {
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
                        binding.tvHandedUrl.setText(pasteData)
                        Toast.makeText(this, "가장 최근에 복사한 URL을 가져왔어요!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "클립보드에 유효한 URL이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "클립보드가 비어 있습니다.", Toast.LENGTH_SHORT).show()
            }
        } // 클립보드에서 바로 붙여넣기

        backButton.setOnClickListener {
            onBackPressed()
        }

        binding.tvHandedTitle.setText(title)
        binding.tvHandedUrl.setText(url)
        binding.tvHandedDesc.setText(description)
        binding.testId.text = id

        // 태그 리스트를 문자열로 변환
        val tagString = tags?.joinToString(" ") { "#$it " }
        binding.tvTag.text = tagString

        binding.tvLimit1.text = "${binding.tvHandedTitle.text.length} / 120"
        binding.tvLimit2.text = "${binding.tvHandedDesc.text.length} / 80"

        binding.tvHandedTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    binding.tvLimit1.text = "${s.length} / 120"
                }
            }
            override fun afterTextChanged(s: Editable?) {
                isTitleValid = s.toString().trim().isNotEmpty()
                if (!isTitleValid) {
                    binding.tvGuideTitle.visibility = View.VISIBLE
                } else {
                    binding.tvGuideTitle.visibility = View.GONE
                }
                updateButtonState()
            }
        })
        binding.tvHandedTitle.setOnEditorActionListener { v, actionId, event ->
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
        binding.tvHandedDesc.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.tvHandedUrl.requestFocus()
                true
            } else {
                false
            }
        }

        tvTag.setOnClickListener {
            showTagSelectionDialog()
        }

        binding.tvHandedUrl.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                isUrlValid =
                    input.isNotBlank() && (input.startsWith("http://") || input.startsWith("https://")) && !input.contains(
                        " "
                    )
                if (!isUrlValid) {
                    binding.tvGuideUrl.visibility = View.VISIBLE
                } else {
                    binding.tvGuideUrl.visibility = View.GONE
                }
                updateButtonState() // 버튼 상태 업데이트
            }
        })

        binding.hookEdit.setOnClickListener {
            val intId = (binding.testId.text as String?)?.toInt()
            val newTitle = binding.tvHandedTitle.text.toString()
            val newDesc = binding.tvHandedDesc.text.toString()
            val newUrl = binding.tvHandedUrl.text.toString()
            val selectedTags = binding.tvTag.text.split("  ").map { it.trim().replace("#", "") }
            val newTag = if (selectedTags.isEmpty() || selectedTags[0].isEmpty()) arrayListOf<String>() else ArrayList(selectedTags)
            //val newTag = if (selectedTags.isEmpty() || selectedTags[0].isEmpty()) null else ArrayList(selectedTags)

            if (intId != null) {
                viewModel.loadUpdateHook(intId, newTitle, newDesc, newUrl, newTag)
            }
            finish()
            Log.d("tagUpdate", "$newTitle, $newDesc, $newUrl, $newTag")
        }
    }
    private fun updateButtonState() {
        val isValid = isUrlValid && isTitleValid
        val finishButton = binding.hookEdit
        finishButton.isEnabled = isValid

        if (isValid) {
            finishButton.setBackgroundColor(getResources().getColor(R.color.purple))
            /*finishButton.setBackgroundResource(R.drawable.button_border)*/
        } else {
            finishButton.setBackgroundColor(getResources().getColor(R.color.gray_100))
//            finishButton.setBackgroundResource(R.drawable.button_border)
        }
    }

    private fun showTagSelectionDialog() {
        val tags = multiChoiceList.keys.toTypedArray()
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
                selectedTags.add("#$tag")
            }
        }
        binding.tvTag.text = selectedTags.joinToString("  ")
    }
}
