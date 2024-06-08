package com.hanto.hook.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hanto.hook.R
import com.hanto.hook.adapter.TagAdapter
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.databinding.FragmentTagBinding
import com.hanto.hook.model.Tag
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory
import java.util.ArrayList

class TagFragment : Fragment() {
    private var _binding: FragmentTagBinding? = null
    private val binding get() = _binding!!
    private lateinit var tagAdapter: TagAdapter
    private val apiServiceManager by lazy { ApiServiceManager() }
    private val viewModelFactory by lazy { ViewModelFactory(apiServiceManager) }
    private val tagViewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(
            MainViewModel::class.java
        )
    }

    private val dialog by lazy {
        Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.activity_add_tag)

            val tvChangeTagName = findViewById<EditText>(R.id.tv_change_tag_name)
            val btnChangeTagName = findViewById<Button>(R.id.btn_change_tag_name)

            btnChangeTagName.setOnClickListener {
                val name = tvChangeTagName.text.toString()
                if (name.isNotEmpty()) {
                    tagViewModel.loadCreateTag(name)
                    clearEditText(tvChangeTagName)
                    this.dismiss()
                    refreshTagList()
                } else {
                    Toast.makeText(requireContext(), "태그 이름을 입력하세요.", Toast.LENGTH_SHORT).show()
                    Toast.makeText(requireContext(), "태그 이름을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            }

            val layoutParams = WindowManager.LayoutParams().apply {
                copyFrom(window?.attributes)
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                gravity = Gravity.CENTER
            }
            window?.attributes = layoutParams
        }
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

        val btAddTag: ImageButton = view.findViewById(R.id.btAddTag)
        btAddTag.setOnClickListener {
            dialog.show()
        }

        binding.swipeLayout.setOnRefreshListener {
            tagViewModel.loadFindMyTags()
            binding.swipeLayout.isRefreshing = false
        }

        tagAdapter = TagAdapter(
            tags = ArrayList(),
            object : TagAdapter.OnItemClickListener {
                override fun onClick(position: Int) {
                    val selectedTag = tagAdapter.getItem(position)
                    val name = selectedTag.displayName
                    if (name != null) {
                        val intent = Intent(requireContext(), SelectedTagActivity::class.java).apply {
                            putExtra("selectedTagName", selectedTag.displayName)
                            putExtra("selectedTagId", selectedTag.id)
                        }
                        startActivityForResult(intent, 1) // Activity 시작
                    }
                }
            })

        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            justifyContent = JustifyContent.SPACE_EVENLY
            flexDirection = FlexDirection.ROW
        }

        binding.rvTagViewTagContainer.apply {
            layoutManager = flexboxLayoutManager
            adapter = tagAdapter
        }

        setTagData()
    }

    private fun setTagData() {
        tagViewModel.loadFindMyTags()
        tagViewModel.tagData.observe(viewLifecycleOwner) { tagData ->
            if (tagData != null) {
                tagAdapter.updateData(tagData.tag)
//                Toast.makeText(
//                    requireActivity(),
//                    "${tagData.count}개의 태그를 가져왔어요.",
//                    Toast.LENGTH_SHORT
//                ).show()
            }
        }
//        tagViewModel.errorData.observe(viewLifecycleOwner) {errorData ->
//            if (errorData != null) {
//                Toast.makeText(requireContext(), "오류: ${errorData.message}", Toast.LENGTH_LONG).show()
//            }
//        }
    }
    private fun clearEditText(editText: EditText) {
        editText.text.clear()
    }

    private fun refreshTagList() {
        tagViewModel.loadFindMyTags()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            // SelectedTagActivity 종료 후 태그 목록 새로고침zz
            refreshTagList()
        }
    }
}