package com.hanto.hook.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hanto.hook.R
import com.hanto.hook.api.RetroServer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

@Suppress("splash_screen")
@SuppressLint("CustomSplashScreen")
class SplashView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val dataStore = applicationContext.dataStore
        val accessToken = loadAccessToken(dataStore)

        if (accessToken.isNotEmpty()) {
            RetroServer.accessToken = accessToken
//            Toast.makeText(this, "토큰 확인 성공: 자동 로그인", Toast.LENGTH_SHORT).show()
            splashToMain()
        } else {
            val intent = Intent(this@SplashView, LoginActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "저장된 토큰 없음", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    private fun splashToMain() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashView, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 500)
    }
    private fun loadAccessToken(dataStore: DataStore<Preferences>): String {
        val accessTokenKey = stringPreferencesKey("access_token")
        return runBlocking {
            dataStore.data.map { preferences ->
                preferences[accessTokenKey] ?: ""
            }.first()
        }
    }
}
