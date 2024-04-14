package com.hanto.Hook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.hanto.Hook.databinding.FragmentTagBinding

class TagFragment : Fragment() {

    private val dummy = listOf(
        "#블로그", "#테크", "#취업" , "#종합설계" , "#학교" , "#자격증", "#공부" , "#강아지" , "#쇼핑", "#사이드 프로젝트"
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

        // 부모 뷰의 폭을 가져와서 그리드 열의 수 계산
        binding.rvTagTag.post {
            val parentWidth = binding.rvTagTag.width
            val columnWidth = resources.getDimensionPixelSize(R.dimen.grid_column_width)
            val spanCount = parentWidth / columnWidth

            // 그리드 레이아웃 매니저 설정
            val layoutManager = GridLayoutManager(requireContext(), 4, GridLayoutManager.HORIZONTAL, false)
            binding.rvTagTag.layoutManager = layoutManager

            // 어댑터 설정
            binding.rvTagTag.adapter = Tag_xl_Adapter(dummy)

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
