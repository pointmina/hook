package com.hanto.Hook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hanto.Hook.databinding.FragmentTagBinding

@Suppress("DEPRECATION")
class TagFragment : Fragment() {

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

//        // "전체 보기" 텍스트를 클릭했을 때 ViewAllTagsFragment를 추가하는 코드
//        binding.tvViewAll.setOnClickListener {
//            addViewAllTagsFragment()
//        }
    }

    private fun addViewAllTagsFragment() {
        childFragmentManager.beginTransaction()
            .add(R.id.fragment_container, ViewAllTagsFragment())
            .setReorderingAllowed(true)
            .addToBackStack("ViewAllTagsFragment") // 백 스택에 추가할 때 태그를 지정합니다.
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
