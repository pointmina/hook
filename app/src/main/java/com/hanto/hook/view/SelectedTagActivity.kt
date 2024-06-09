package com.hanto.hook.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hanto.hook.BaseActivity
import com.hanto.hook.R
import com.hanto.hook.adapter.SelectedTagHookListAdapter
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.databinding.ActivitySelectedTagBinding
import com.hanto.hook.model.Hook
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory

class SelectedTagActivity : BaseActivity() {

    private lateinit var binding: ActivitySelectedTagBinding
    private lateinit var selectedTagHookListAdapter: SelectedTagHookListAdapter

    private val apiServiceManager by lazy { ApiServiceManager() }
    private val viewModelFactory by lazy { ViewModelFactory(apiServiceManager) }
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectedTagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivAppbarSelectedTagBackButton.setOnClickListener {
            finish()
        }

        // Intent 로부터 데이터 받기
        val selectedTagName = intent.getStringExtra("selectedTagName")
        val selectedTagId = intent.getIntExtra("selectedTagId", -1) // 아이디 (기본값 -1으로 설정)
        binding.tvSelectedTag.text = selectedTagName

        val ivTagChange = binding.ivTagChange
        ivTagChange.setOnClickListener {
            val changeTagFragment = ChangeTagFragment { newTagName ->
                viewModel.loadFindMyTags() // 태그 목록 새로고침
                setResult(RESULT_OK)
                binding.tvSelectedTag.text = newTagName // 텍스트 뷰 업데이트
            }.apply {
                arguments = Bundle().apply {
                    putString("selectedTag", selectedTagName)
                    putInt("selectedTagId", selectedTagId)
                }
            }
            changeTagFragment.show(supportFragmentManager, "ChangeTagFragment")
        }

        val ivTagDelete = binding.ivTagDelete
        ivTagDelete.setOnClickListener {
            deleteDialog(selectedTagId)
        }

        // 해당 태그와 관련된 항목 목록이어야 합니다.
        selectedTagHookListAdapter = SelectedTagHookListAdapter(
            hooks = ArrayList(),
            object : SelectedTagHookListAdapter.OnItemClickListener {
                override fun onOptionButtonClick(position: Int) {
                    val selectedHook = selectedTagHookListAdapter.getItem(position)
                    showBottomSheetDialog(selectedHook)
                }
            })

        binding.rvUrlHookList.adapter = selectedTagHookListAdapter
        binding.rvUrlHookList.layoutManager = LinearLayoutManager(this)

        // DividerItemDecoration에 대한 설정
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
            dividerItemDecoration.setDrawable(it)
        }

        binding.rvUrlHookList.addItemDecoration(dividerItemDecoration)

        fun loadMyHook() {
            if (selectedTagId != -1) {
                viewModel.loadFindMyHookByTag(tagID = selectedTagId)
            }
            viewModel.tagFilteredHooks.observe(this) { tagFilteredHooks ->
                if (tagFilteredHooks != null) {
                    selectedTagHookListAdapter.updateData(tagFilteredHooks)
                    val countString = "${tagFilteredHooks.count}개의 훅"
                    binding.tvTagCount.text = countString
                } else {
                    Toast.makeText(this, "불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
        loadMyHook()
//        binding.swipeLayout.setOnRefreshListener {
//            viewModel.loadFindMyHookByTag(tagID = selectedTagId)
//            binding.swipeLayout.isRefreshing = false
//        }
    }

    private fun deleteDialog(selectedTagId: Int) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("정말 삭제하시겠습니까?")
        dialogBuilder.setMessage("저장하신 태그가 삭제됩니다.")
        dialogBuilder.setPositiveButton("예") { dialog, _ ->
            viewModel.loadDeleteTag(selectedTagId)
            viewModel.successData.observe(this@SelectedTagActivity) { successData ->
                Toast.makeText(this@SelectedTagActivity, "${successData?.result?.message}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                finish()
            }
        }
        dialogBuilder.setNegativeButton("아니오") { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.show()
    }

    @SuppressLint("InflateParams")
    private fun showBottomSheetDialog(selectedItem: Hook) {
        val dialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_dialog_home, null)
        dialog.setContentView(view)
        dialog.setCancelable(true)

        val btonWeb = view.findViewById<Button>(R.id.bt_onWeb)
        btonWeb.setOnClickListener {
            Intent(this, WebviewActivity::class.java).also { intent ->
                intent.putExtra(WebviewActivity.EXTRA_URL, selectedItem.url)
                startActivity(intent)
            }
            dialog.dismiss()
        }

        val btHookDelete = view.findViewById<Button>(R.id.bt_HookDelete)
        btHookDelete.setOnClickListener {
            selectedItem.id?.let { it1 -> viewModel.loadDeleteHook(it1) }
            dialog.dismiss()
            Toast.makeText(this, "삭제 완료!", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }
    override fun onDestroy() {
        super.onDestroy()
        // ViewModel의 관찰자를 해제하여 메모리 누수 방지
        viewModel.tagFilteredHooks.removeObservers(this)
    }
}
