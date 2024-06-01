package com.hanto.hook.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanto.hook.R
import com.hanto.hook.adapter.SelectedTagHookListAdapter
import com.hanto.hook.databinding.ActivitySelectedTagBinding
import com.hanto.hook.model.Tag

@Suppress("DEPRECATION")
class SelectedTagActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectedTagBinding
    private lateinit var selectedTagHookListAdapter: SelectedTagHookListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectedTagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로부터 데이터 받기
        val selectedTag = intent.getStringExtra("selectedTag") // displayName만 가져옴
        binding.tvSelectedTag.text = selectedTag

        val backButton = binding.ivAppbarSelectedTagBackButton

        backButton.setOnClickListener {
            onBackPressed()
        }

        val ivTagChange = binding.ivTagChange
        ivTagChange.setOnClickListener {
            val changeTagFragment = ChangeTagFragment().apply {
                arguments = Bundle().apply {
                    putString("selectedTag", selectedTag)
                }
            }
            changeTagFragment.show(supportFragmentManager, "ChangeTagFragment")
        }

        //homeFragment참고! 해당 태그와 관련된 항목 목록이어야 합니다.
        selectedTagHookListAdapter = SelectedTagHookListAdapter(listOf())
        binding.rvUrlHookList.adapter = selectedTagHookListAdapter
        binding.rvUrlHookList.layoutManager = LinearLayoutManager(this)


        // DividerItemDecoration에 대한 설정
        val dividerItemDecoration =
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
            dividerItemDecoration.setDrawable(it)
        }



        binding.rvUrlHookList.addItemDecoration(dividerItemDecoration)
    }
}
