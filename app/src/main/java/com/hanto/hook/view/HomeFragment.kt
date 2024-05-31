package com.hanto.hook.view


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hanto.hook.R
import com.hanto.hook.databinding.FragmentHomeBinding
import com.hanto.hook.adapter.HookAdapter
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.model.Hook
import com.hanto.hook.viewmodel.HookViewModel
import com.hanto.hook.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var hookAdapter: HookAdapter

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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 세팅 버튼 클릭 시
        binding.ivSetting.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_settingActivity)
        }

        // 추가 버튼 클릭 시
//        binding.add.setOnClickListener {
//            findNavController().navigate(R.id.action_navigation_home_to_addHookActivity)
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        hookViewModel.loadFindMyHooks()
        binding.rvHome.adapter = hookAdapter

        // DividerItemDecoration 설정
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
            dividerItemDecoration.setDrawable(it)
        }

        binding.rvHome.addItemDecoration(dividerItemDecoration)

        val shimmerContainer = binding.sfLoading
        hookViewModel.successData.observe(viewLifecycleOwner, Observer { successData ->
            if (successData != null) {
                hookAdapter.updateData(successData)
                shimmerContainer.stopShimmer()
                shimmerContainer.visibility = View.GONE
            } else {

            }
        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


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

//        val btHookShare = view.findViewById<Button>(R.id.bt_HookShare)
//        btHookShare.setOnClickListener {
//            // Share 기능 구현
//            dialog.dismiss()
//        }

        val btHookDelete = view.findViewById<Button>(R.id.bt_HookDelete)
        btHookDelete.setOnClickListener {
            // Delete 기능 구현
            dialog.dismiss()
        }

        dialog.show()
    }
}

