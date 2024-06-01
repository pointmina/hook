package com.hanto.hook.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hanto.hook.R
import com.hanto.hook.databinding.FragmentHomeBinding
import com.hanto.hook.adapter.HookAdapter
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.model.Hook
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var hookAdapter: HookAdapter

    private val apiServiceManager by lazy { ApiServiceManager() }
    private val viewModelFactory by lazy { ViewModelFactory(apiServiceManager) }
    private val hookViewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

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

        val btSetting = view.findViewById<ImageButton>(R.id.bt_setting)
        btSetting.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_home_to_settingActivity)
        }

        binding.swipeLayout.setOnRefreshListener {
            hookViewModel.loadFindMyHooks()
            binding.swipeLayout.isRefreshing = false
        }


        hookAdapter = HookAdapter(
            hooks = ArrayList(),
            tag = ArrayList(),
            object : HookAdapter.OnItemClickListener {
                override fun onClick(position: Int) {
                    val selectedHook = hookAdapter.getItem(position)
                    Intent(requireContext(), HookDetailActivity::class.java).apply {
                        putExtra("item_title", selectedHook.title)
                        putExtra("item_url", selectedHook.url)
                        putExtra("item_description", selectedHook.description)
                        selectedHook.tags?.map { it.displayName }?.let {
                            putStringArrayListExtra("item_tag_list", ArrayList(it))
                        }
                        startActivity(this)
                    }
                }

                override fun onOptionButtonClick(position: Int) {
                    val selectedHook = hookAdapter.getItem(position)
                    showBottomSheetDialog(selectedHook)
                }

            })

        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        binding.rvHome.addItemDecoration(dividerItemDecoration)

        binding.rvHome.adapter = hookAdapter
        hookViewModel.loadFindMyHooks()

        val shimmerContainer = binding.sfLoading
        hookViewModel.hookData.observe(viewLifecycleOwner) { hookData ->
            if (hookData != null) {
                hookAdapter.updateData(hookData)
                shimmerContainer.stopShimmer()
                shimmerContainer.visibility = View.GONE
                Toast.makeText(requireActivity(), "${hookData.count}개의 훅을 가져왔습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireActivity(), "불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

/*    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }*/

    private fun showBottomSheetDialog(selectedItem: Hook) {
        val dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_dialog_home, null)
        dialog.setContentView(view)
        dialog.setCancelable(true)

        val btonWeb = view.findViewById<Button>(R.id.bt_onWeb)
        btonWeb.setOnClickListener {
            Intent(requireContext(), WebviewActivity::class.java).also { intent ->
                intent.putExtra(WebviewActivity.EXTRA_URL, selectedItem?.url)
                startActivity(intent)
            }
            dialog.dismiss()
        }

        val btHookDelete = view.findViewById<Button>(R.id.bt_HookDelete)
        btHookDelete.setOnClickListener {
            selectedItem.id?.let { it1 -> hookViewModel.loadDeleteHook(it1) }
            dialog.dismiss()
        }
        // 성공 메시지 관찰
        hookViewModel.successData.observe(viewLifecycleOwner) { successResponse ->
            successResponse?.let {
                Toast.makeText(requireActivity(), it.result?.message ?: "성공쓰", Toast.LENGTH_SHORT).show()
            }
        }

        // 에러 메시지 관찰
        hookViewModel.errorData.observe(viewLifecycleOwner) { errorResponse ->
            errorResponse?.let {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}

