package com.hanto.hook.view

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.hanto.hook.BuildConfig
import com.hanto.hook.api.RetroServer
import com.hanto.hook.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btKakaologin.setOnClickListener{
            Intent(this, LoginWebViewActivity::class.java).also { intent ->
                intent.putExtra(LoginWebViewActivity.EXTRA_URL, BuildConfig.KAKAO_LOGIN_URL)
                startActivity(intent)
            }
        }

        binding.btWithoutlogin.setOnClickListener {
            RetroServer.accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIyIiwiaWF0IjoxNzE1NTg0Nzc2fQ.ll_sGqvG9CiOXvNBlX6LaOJ63hp1jrCH6ebiejL5emI"
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}

