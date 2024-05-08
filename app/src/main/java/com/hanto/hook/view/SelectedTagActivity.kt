package com.hanto.hook.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanto.hook.R
import com.hanto.hook.adapter.SelectedTagHookListAdapter
import com.hanto.hook.databinding.ActivitySelectedTagBinding

@Suppress("DEPRECATION")
class SelectedTagActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectedTagBinding
    private lateinit var selectedTagHookListAdapter: SelectedTagHookListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectedTagBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Intent로부터 데이터 받기
        val selectedTag = intent.getStringExtra("selectedTag")

        val backButton = binding.ivAppbarSelectedTagBackButton

        backButton.setOnClickListener {
            onBackPressed()
        }

        val ivTagChange = binding.ivTagChange
        ivTagChange.setOnClickListener {
            // ChangeTagFragment를 띄우기 위한 코드
            val changeTagFragment = ChangeTagFragment()
            changeTagFragment.show(supportFragmentManager, "ChangeTagFragment")
        }

//homeFragment참고!
//        selectedTagHookListAdapter = SelectedTagHookListAdapter(getDummyData())
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