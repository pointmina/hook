package com.hanto.Hook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hanto.Hook.databinding.FragmentSelectedTagBinding

@Suppress("DEPRECATION")
class SelectedTagFragment : Fragment() {

    private var _binding: FragmentSelectedTagBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectedTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // "수정하기" 클릭했을 때 ViewAllTagsFragment를 추가하는 코드
        binding.ivTagChange.setOnClickListener {
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

}
