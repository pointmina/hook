package com.hanto.hook.view

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hanto.hook.R
import com.hanto.hook.adapter.TagAdapter
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.databinding.FragmentTagBinding
import com.hanto.hook.viewmodel.HookViewModel
import com.hanto.hook.viewmodel.ViewModelFactory

class TagFragment : Fragment() {

    private var _binding: FragmentTagBinding? = null
    private val binding get() = _binding!!
    private lateinit var tagAdapter: TagAdapter

    private val apiServiceManager by lazy { ApiServiceManager() }
    private val viewModelFactory by lazy { ViewModelFactory(apiServiceManager) }
    private val hookViewModel: HookViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(HookViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // AddTag 버튼에 대한 클릭 리스너 설정
        binding.ivAddTag.setOnClickListener {
            // Dialog를 생성하고 설정
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // 타이틀 제거
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.activity_add_tag) // AddTagActivity 레이아웃을 Dialog에 설정

            // Dialog 크기 및 위치 설정
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.gravity = Gravity.CENTER
            dialog.window?.attributes = layoutParams

            // Dialog 표시
            dialog.show()
        }

        tagAdapter = TagAdapter(
            tag = emptyList(),
            object : TagAdapter.OnItemClickListener {
                override fun onClick(position: Int) {
                    val selectedTag = tagAdapter.getItem(position)
                    val name = selectedTag.displayName
                    if (name != null) {
                        // 선택된 태그에 대한 작업 수행
                        Intent(requireContext(), SelectedTagActivity::class.java).apply {
                            putExtra("selectedTag", selectedTag.displayName)
                            startActivity(this)
                        }
                    }
                }
            }
        )

        hookViewModel.loadFindMyTags()

        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            justifyContent = JustifyContent.SPACE_EVENLY
            flexDirection = FlexDirection.ROW
        }

        binding.rvTagViewTagContainer.apply {
            layoutManager = flexboxLayoutManager
            adapter = tagAdapter
        }

        hookViewModel.successData.observe(viewLifecycleOwner, Observer { successData ->
            if (successData != null) {
                tagAdapter.updateData(successData)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
