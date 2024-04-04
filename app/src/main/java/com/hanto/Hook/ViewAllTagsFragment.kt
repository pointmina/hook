package com.hanto.Hook

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hanto.Hook.databinding.FragmentChangeTagBinding


class ViewAllTagsFragment : DialogFragment() {

    private var _binding: FragmentChangeTagBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewBack.setOnClickListener {
            dismiss()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}



