package com.hanto.hook.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hanto.hook.R
import com.hanto.hook.adapter.SelectedTagHookListAdapter
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.databinding.ActivitySelectedTagBinding
import com.hanto.hook.model.Hook
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory

@Suppress("DEPRECATION")
class SelectedTagActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectedTagBinding
    private lateinit var selectedTagHookListAdapter: SelectedTagHookListAdapter

    private val apiServiceManager by lazy { ApiServiceManager() }
    private val viewModelFactory by lazy { ViewModelFactory(apiServiceManager) }
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectedTagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로부터 데이터 받기
        val selectedTagName = intent.getStringExtra("selectedTagName")
        val selectedTagId = intent.getIntExtra("selectedTagId", -1) // 아이디 (기본값 -1으로 설정)
        binding.tvSelectedTag.text = selectedTagName

//        val ivTagChange = binding.ivTagChange
//        ivTagChange.setOnClickListener {
//            val changeTagFragment = ChangeTagFragment().apply {
//                arguments = Bundle().apply {
//                    putString("selec`tedTag", selectedTagName)
//                    putInt("selectedTagId", selectedTagId)
//                }
//            }
//            changeTagFragment.show(supportFragmentManager, "ChangeTagFragment")
//        }
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
            val deleteTagFragment = DeleteTagFragment {
                Log.d("SelectedTagActivity", "태그 삭제 후 태그 목록 새로고침")
                viewModel.loadFindMyTags() // 태그 목록 새로고침
                setResult(RESULT_OK) // 결과 설정
                finish() // 태그가 삭제된 후 액티비티를 종료

            }.apply {
                arguments = Bundle().apply {
                    putInt("selectedTagId", selectedTagId)
                }
            }
            deleteTagFragment.show(supportFragmentManager, "DeleteTagFragment")

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

        if (selectedTagId != -1) {
            viewModel.loadFindMyHookByTag(tagID = selectedTagId)
        }

        viewModel.tagFilteredHooks.observe(this) { tagFilteredHooks ->
            if (tagFilteredHooks != null) {
                selectedTagHookListAdapter.updateData(tagFilteredHooks)
                binding.tvTagCount.text = "${tagFilteredHooks.count}개의 훅"
            } else {
                Toast.makeText(this, "불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }

        binding.swipeLayout.setOnRefreshListener {
            viewModel.loadFindMyHookByTag(tagID = selectedTagId)
            binding.swipeLayout.isRefreshing = false
        }

        binding.ivAppbarSelectedTagBackButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showBottomSheetDialog(selectedItem: Hook) {
        val dialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_dialog_home, null)
        dialog.setContentView(view)
        dialog.setCancelable(true)

        val btonWeb = view.findViewById<Button>(R.id.bt_onWeb)
        btonWeb.setOnClickListener {
            Intent(this, HookDetailActivity::class.java).also { intent ->
                intent.putExtra("item_id", selectedItem.id.toString())
                intent.putExtra("item_title", selectedItem.title)
                intent.putExtra("item_url", selectedItem.url)
                intent.putExtra("item_description", selectedItem.description)
                selectedItem.tags?.map { it.displayName }?.let {
                    intent.putStringArrayListExtra("item_tag_list", ArrayList(it))
                }
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
