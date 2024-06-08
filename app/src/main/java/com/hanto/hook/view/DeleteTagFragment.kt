package com.hanto.hook.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.hanto.hook.databinding.FragmentDeleteTagBinding
import com.hanto.hook.model.Hook
import com.hanto.hook.model.Tag
import com.hanto.hook.viewmodel.MainViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.viewmodel.ViewModelFactory

class DeleteTagFragment(private val onTagDeleted: () -> Unit) : DialogFragment() {

    private var _binding: FragmentDeleteTagBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private var selectedTagId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeleteTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun deleteTag(tagId: Int) {
        viewModel.loadDeleteTag(tagId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화
        val apiServiceManager = ApiServiceManager()
        val viewModelFactory = ViewModelFactory(apiServiceManager)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(MainViewModel::class.java)

        // 선택된 태그 ID 가져오기
        selectedTagId = arguments?.getInt("selectedTagId", -1) ?: -1

        // 태그 삭제
        binding.btnDeleteTag.setOnClickListener {
            if (selectedTagId != -1) {
                deleteTag(selectedTagId)
            } else {
                Toast.makeText(requireContext(), "태그 ID가 유효하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 결과 관찰
        viewModel.successData.observe(viewLifecycleOwner) { successResponse ->
            successResponse?.let {
                Toast.makeText(requireContext(), "태그가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                Log.d("DeleteTagFragment", "태그 삭제 성공, 콜백 호출")
                onTagDeleted() // 태그 삭제 성공 시 콜백 호출
                dismiss()
            }
        }

        viewModel.errorData.observe(viewLifecycleOwner) { errorResponse ->
            errorResponse?.let {
                Toast.makeText(requireContext(), "태그 삭제에 실패했습니다: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
