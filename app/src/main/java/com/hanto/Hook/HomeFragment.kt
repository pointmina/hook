package com.hanto.Hook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hanto.Hook.databinding.FragmentHomeBinding

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // "전체 보기" 텍스트를 클릭했을 때 ViewAllTagsFragment를 추가하는 코드
        binding.tvViewAll.setOnClickListener {
            addViewAllTagsFragment()
        }
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

    fun onBackPressed() {
        // 현재 백 스택의 상단에 있는 프래그먼트를 가져옵니다.
        val fragment = childFragmentManager.findFragmentByTag("ViewAllTagsFragment")

        // ViewAllTagFragment가 있다면 제거합니다.
        if (fragment != null) {
            childFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        } else {
            // ViewAllTagFragment가 없다면 기본 뒤로가기 동작을 수행합니다.
            requireActivity().onBackPressed()
        }
    }
}
