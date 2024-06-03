package com.hanto.hook.view

import android.os.Bundle
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.hanto.hook.BuildConfig
import com.hanto.hook.api.RetroServer
import com.hanto.hook.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
// LoginActivity : 사용자 로그인을 처리하는 액티비티
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataStore = applicationContext.dataStore

        // 네트워크 작업을 백그라운드 스레드에서 실행
        Thread {
            try {
                val accessToken = loadAccessToken(dataStore)

                runOnUiThread {
                    if (accessToken.isNotEmpty()) {
                        RetroServer.accessToken = accessToken
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        setupLoginButton()
                    }
                }
            } catch (e: Exception) {
                // 에러 로그 기록
                Log.e("LoginActivity", "Error loading access token", e)
            }
        }.start()

        /*binding.btWithoutlogin.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }*/
    }

    private fun loadAccessToken(dataStore: DataStore<Preferences>): String {
        val accessTokenKey = stringPreferencesKey("access_token")
        return runBlocking {
            dataStore.data.map { preferences ->
                preferences[accessTokenKey] ?: ""
            }.first()
        }
    }

    private fun setupLoginButton() {
        binding.btKakaologin.setOnClickListener {
            Intent(this, LoginWebViewActivity::class.java).also { intent ->
                intent.putExtra(LoginWebViewActivity.EXTRA_URL, BuildConfig.KAKAO_LOGIN_URL)
                startActivity(intent)
            }
        }
    }
}

