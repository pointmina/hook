package com.hanto.hook.view

import android.content.ClipboardManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.hanto.hook.BaseActivity
import com.hanto.hook.R
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.data.TagSelectionListener
import com.hanto.hook.databinding.ActivityAddHookBinding
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory

@Suppress("DEPRECATION")
class AddHookActivity : BaseActivity(), TagSelectionListener {
    private lateinit var binding: ActivityAddHookBinding

    private val apiServiceManager by lazy { ApiServiceManager() }
    private val viewModelFactory by lazy { ViewModelFactory(apiServiceManager) }
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory
        )[MainViewModel::class.java]
    }

    private var isUrlValid = false
    private var isTitleValid = false
    private var isExpanded = false

    private var selectedTags: List<String> = emptyList()
    private val multiChoiceList = linkedMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHookBinding.inflate(layoutInflater)
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

        val tags = intent.getStringArrayListExtra("item_tag_list")
        tags?.forEach { tag ->
            multiChoiceList[tag] = true
        }

        setContentView(view)
        updateButtonState()

        binding.ivAppbarBackButton.setOnClickListener {
            finish()
        }

        // 클립보드에서 바로 붙여넣기
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
        }

        val limitString1 = "${binding.tvUrlTitle.text.length} / 120"
        val limitString2 = "${binding.tvUrlDescription.text.length} / 80"
        binding.tvLimit1.text = limitString1
        binding.tvLimit2.text = limitString2

        //content에 따라 버튼 active
        binding.tvUrlLink.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                isUrlValid = input.isNotBlank() && !input.contains(" ")

                if (!isUrlValid) {
                    binding.tvGuide.visibility = View.VISIBLE
                } else {
                    binding.tvGuide.visibility = View.GONE
                    updateButtonState() // 버튼 상태 업데이트
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.tvUrlLink.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.tvUrlTitle.requestFocus()
                true
            } else {
                false
            }
        }

        binding.tvUrlTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    val innerLimitString1 = "${s.length} / 120"
                    binding.tvLimit1.text = innerLimitString1

                    isTitleValid = s.toString().trim().isNotEmpty()
                    if (!isTitleValid) {
                        binding.tvGuideTitle.visibility = View.VISIBLE
                    } else {
                        binding.tvGuideTitle.visibility = View.GONE
                        updateButtonState()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.tvUrlTitle.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT && isExpanded) {
                binding.tvUrlDescription.requestFocus()
                true
            } else if (actionId == EditorInfo.IME_ACTION_NEXT && !isExpanded) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.tvUrlTitle.windowToken, 0)
                true
            } else {
                false
            }
        }

        binding.tvUrlDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    val innerLimitString2 = "${s.length} / 80"
                    binding.tvLimit2.text = innerLimitString2
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 태그 선택
        binding.containerTag.setOnClickListener {
            val fragment = TagListFragment.newInstance(multiChoiceList)
            fragment.setTagSelectionListener(this)
            Log.d("minamina", "Sending tags to TagListFragment: $multiChoiceList")
            fragment.show(supportFragmentManager, "TagListFragment")
        }


        // 더보기 뷰
        binding.containerLinkInfoEtc.setOnClickListener {
            val tvUrlDescription = binding.tvUrlDescription
            val tvTag = binding.tvTag
            val containerTag = binding.containerTag
            val downArrow = binding.ivDownArrow
            val tvLimit2 = binding.tvLimit2
//            toggleExpandCollapse(tvUrlDescription, tvTag, containerTag, downArrow, tvLimit2)
        }

        binding.ivAddNewHook.setOnClickListener {
            val tags = ArrayList(binding.containerTag.text.split(" ")
                .map { it.trim().replace("#", "") }
                .filter { it.isNotEmpty() })
            val title = binding.tvUrlTitle.text.toString()
            val description = binding.tvUrlDescription.text.toString()
            val url = binding.tvUrlLink.text.toString()

            viewModel.loadCreateHook(title, description, url, tags, suggestTags = false)
            viewModel.createHookSuccessData.observe(this) { createHookSuccessData ->
                if (createHookSuccessData != null) {
                    Toast.makeText(this, "훅이 추가됐어요!", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    }, 300)
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

    private fun updateButtonState() {
        val isValid = isUrlValid && isTitleValid
        val finishButton = binding.ivAddNewHook
        finishButton.isEnabled = isValid

        if (isValid) {
            finishButton.setBackgroundColor(getResources().getColor(R.color.purple))
        } else {
            finishButton.setBackgroundColor(getResources().getColor(R.color.gray_100))
        }
    }

    //펼쳐져있는게 낫다는 피드백이 있어서 일단 주석처리해놈
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

    override fun onTagsSelected(tags: List<String>) {
        binding.containerTag.text = tags.joinToString(" ") { "#$it" }
        Log.d("minamina", "Received tags from TagListFragment: $tags")
    }
}