package com.hanto.hook.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanto.hook.adapter.TagListAdapter
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.data.TagSelectionListener
import com.hanto.hook.databinding.FragmentTagListBinding
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory

class tagListFragment : DialogFragment() {

    private var tagSelectionListener: TagSelectionListener? = null
    private var _binding: FragmentTagListBinding? = null
    private val apiServiceManager by lazy { ApiServiceManager() }
    private val viewModelFactory by lazy { ViewModelFactory(apiServiceManager) }
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory
        )[MainViewModel::class.java]
    }
    private val multiChoiceList = linkedMapOf<String, Boolean>()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTagListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadFindMyTags()
        viewModel.tagData.observe(viewLifecycleOwner) { tagData ->
            tagData?.let {
                for (tag in tagData.tag) {
                    tag.displayName?.let { displayName ->
                        multiChoiceList[displayName] = false
                    }
                }
                val adapter = TagListAdapter(requireContext(), multiChoiceList)
                binding.lvTags.adapter = adapter
                binding.lvTags.layoutManager = LinearLayoutManager(requireContext())
            }
        }

        // btn_add_tag에 클릭 리스너 설정
        binding.btnAddTag.setOnClickListener {
            val newTag = binding.tvAddNewTag.text.toString().trim()
            if (newTag.isEmpty()) {
                // 입력값이 공백인 경우 알림 메시지 표시
                Toast.makeText(requireContext(), "태그를 입력하세요.", Toast.LENGTH_SHORT).show()
            } else if (multiChoiceList.containsKey(newTag)) {
                // 이미 존재하는 태그를 선택한 것으로 처리
                multiChoiceList[newTag] = true
                // 어댑터에게 데이터셋이 변경되었음을 알림
                binding.lvTags.adapter?.notifyDataSetChanged()
                // 입력 필드 초기화
                binding.tvAddNewTag.text = null
            } else {
                // 새로운 태그를 multiChoiceList에 추가
                multiChoiceList[newTag] = true
                // 어댑터에게 데이터셋이 변경되었음을 알림
                binding.lvTags.adapter?.notifyDataSetChanged()
                // 입력 필드 초기화
                binding.tvAddNewTag.text = null
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        // OK 버튼에 클릭 리스너 설정
        binding.btnOk.setOnClickListener {
            // 선택된 태그들을 리스트로 변환
            val selectedTags = multiChoiceList.filterValues { it }.keys.toList()
            // 선택된 태그들을 문자열 형태로 변환하여 "#"으로 구분하여 만듦
            val formattedTags = selectedTags.joinToString(" ") { "#$it" }
            // 인터페이스를 통해 선택된 태그들을 전달
            tagSelectionListener?.onTagsSelected(formattedTags)
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun setTagSelectionListener(listener: TagSelectionListener) {
        tagSelectionListener = listener
    }
}

