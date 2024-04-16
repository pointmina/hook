package com.hanto.Hook

import Tag_xl_Adapter
import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hanto.Hook.databinding.FragmentTagBinding


class TagFragment : Fragment() {

    private val dummy = listOf(
        "#블로그", "#테크", "#취업", "#종합설계", "#학교", "#자격증", "#공부", "#강아지", "#쇼핑", "#사이드 프로젝트",
    )

    private var _binding: FragmentTagBinding? = null
    private val binding get() = _binding!!

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

        val recyclerView = binding.rvTagTag
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.CENTER
        recyclerView.layoutManager = layoutManager

        // 어댑터 설정
        binding.rvTagTag.adapter = Tag_xl_Adapter(dummy)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
