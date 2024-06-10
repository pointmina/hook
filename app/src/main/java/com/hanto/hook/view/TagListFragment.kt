package com.hanto.hook.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanto.hook.adapter.TagListAdapter
import com.hanto.hook.data.TagSelectionListener
import com.hanto.hook.databinding.FragmentTagListBinding

class TagListFragment : DialogFragment() {

    private var tagSelectionListener: TagSelectionListener? = null
    private var _binding: FragmentTagListBinding? = null
    private val binding get() = _binding!!
    private lateinit var multiChoiceList: LinkedHashMap<String, Boolean>
    private lateinit var adapter: TagListAdapter

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

        // arguments로부터 multiChoiceList 값을 받아옵니다.
        arguments?.let {
            multiChoiceList = it.getSerializable("multiChoiceList") as LinkedHashMap<String, Boolean>
        }

        adapter = TagListAdapter(requireContext(), multiChoiceList)
        binding.lvTags.adapter = adapter
        binding.lvTags.layoutManager = LinearLayoutManager(requireContext())

        // btn_add_tag 클릭 리스너 설정
        binding.btnAddTag.setOnClickListener {
            val newTag = binding.tvAddNewTag.text.toString().trim()
            if (newTag.isEmpty()) {
                // 입력값이 공백인 경우 알림 메시지 표시
                Toast.makeText(requireContext(), "태그를 입력하세요.", Toast.LENGTH_SHORT).show()
            } else if (multiChoiceList.containsKey(newTag)) {
                // 이미 존재하는 태그인 경우 아무 작업도 하지 않음
                Toast.makeText(requireContext(), "이미 존재하는 태그입니다.", Toast.LENGTH_SHORT).show()
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
            Log.d("minaminamina", "Selected tags: $selectedTags")
            // 인터페이스를 통해 선택된 태그들을 전달
            tagSelectionListener?.onTagsSelected(selectedTags)
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

    companion object {
        fun newInstance(multiChoiceList: LinkedHashMap<String, Boolean>): TagListFragment {
            val fragment = TagListFragment()
            val args = Bundle()
            args.putSerializable("multiChoiceList", multiChoiceList)
            fragment.arguments = args
            return fragment
        }
    }
}
