package com.hanto.hook.view

import TagAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hanto.hook.databinding.FragmentTagBinding
import com.hanto.apitest.HookViewModel

class TagFragment : Fragment() {

    private var _binding: FragmentTagBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HookViewModel

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

        viewModel = ViewModelProvider(this).get(HookViewModel::class.java)
        viewModel.getAllData()

//        tagAdapter = TagAdapter(requireContext(), listOf()) // Initialize with an empty list
//        binding.rvTagViewTagContainer.adapter = tagAdapter

        viewModel.result.observe(viewLifecycleOwner, Observer {
            val tagAdapter = TagAdapter(requireContext(),it)
            binding.rvTagViewTagContainer.adapter = tagAdapter
        })

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.rvTagViewTagContainer
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.CENTER
        recyclerView.layoutManager = layoutManager
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
