package com.hanto.hook.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanto.hook.R
import com.hanto.hook.adapter.SelectedTagHookListAdapter
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.databinding.ActivitySelectedTagBinding
import com.hanto.hook.model.Tag
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory

@Suppress("DEPRECATION")
class SelectedTagActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectedTagBinding
    private lateinit var selectedTagHookListAdapter: SelectedTagHookListAdapter

    private val apiServiceManager by lazy { ApiServiceManager() }
    private val viewModelFactory by lazy { ViewModelFactory(apiServiceManager) }
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(
        MainViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectedTagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로부터 데이터 받기
        val selectedTagName = intent.getStringExtra("selectedTagName")
        val selectedTagId = intent.getIntExtra("selectedTagId", -1) // 아이디 (기본값 -1으로 설정)
        binding.tvSelectedTag.text = selectedTagName



        val ivTagChange = binding.ivTagChange
        ivTagChange.setOnClickListener {
            val changeTagFragment = ChangeTagFragment().apply {
                arguments = Bundle().apply {
                    putString("selectedTag", selectedTagName)
                }
            }
            changeTagFragment.show(supportFragmentManager, "ChangeTagFragment")
        }

        //homeFragment참고! 해당 태그와 관련된 항목 목록이어야 합니다.
        selectedTagHookListAdapter = SelectedTagHookListAdapter(hooks = ArrayList())
        binding.rvUrlHookList.adapter = selectedTagHookListAdapter
        binding.rvUrlHookList.layoutManager = LinearLayoutManager(this)


        // DividerItemDecoration에 대한 설정
        val dividerItemDecoration =
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
            dividerItemDecoration.setDrawable(it)
        }

        binding.rvUrlHookList.addItemDecoration(dividerItemDecoration)

        if (selectedTagId != -1 ) {
            viewModel.loadFindMyHookByTag(tagID = selectedTagId) }
        viewModel.tagFilteredHooks.observe(this) { tagFilteredHooks ->
            if (tagFilteredHooks != null) {
                selectedTagHookListAdapter.updateData(tagFilteredHooks)
                binding.tvTagCount.text = "${tagFilteredHooks.count}개의 훅"
            } else {
                Toast.makeText(this, "불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivAppbarSelectedTagBackButton.setOnClickListener {
            onBackPressed()
        }
    }
}