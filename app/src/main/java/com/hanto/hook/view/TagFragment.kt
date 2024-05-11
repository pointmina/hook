package com.hanto.hook.view

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

        tagAdapter = TagAdapter(
            tag = emptyList(),
            object : TagAdapter.OnItemClickListener {
                override fun onClick(position: Int) {
                    val selectedTag = tagAdapter.getItem(position)

                    val name = selectedTag.displayName
                    if (name != null) {

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

//    private fun setupRecyclerView() {
//        val recyclerView = binding.rvTagViewTagContainer
//        val layoutManager = FlexboxLayoutManager(context)
//        layoutManager.flexDirection = FlexDirection.ROW
//        layoutManager.justifyContent = JustifyContent.CENTER
//        recyclerView.layoutManager = layoutManager
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
