package com.hanto.Hook.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController // findNavController 추가
import com.hanto.Hook.R
import com.hanto.Hook.databinding.ActivityHookDetailBinding

class HookDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHookDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("item_title")
        val url = intent.getStringExtra("item_url")
        val description = intent.getStringExtra("item_description")
        val backButton = binding.ivAppbarUrlHookDetailBackButton

        backButton.setOnClickListener {
            onBackPressed()
        }

        binding.tvHandedTitle.setText(title)
        binding.tvHandedUrl.setText(url)
        binding.tvHandedDesc.setText(description)

        binding.tvLimit1.text = "${binding.tvHandedTitle.text.length} / 50"
        binding.tvLimit2.text = "${binding.tvHandedDesc.text.length} / 80"


        binding.tvHandedTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    binding.tvLimit1.text = "${s.length} / 50"
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.tvHandedDesc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    binding.tvLimit2.text = "${s.length} / 80"
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }
}
