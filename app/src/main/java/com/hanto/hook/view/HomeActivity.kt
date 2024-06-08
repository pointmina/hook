package com.hanto.hook.view

import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hanto.hook.BaseActivity
import com.hanto.hook.R
import com.hanto.hook.databinding.ActivityHomeBinding

class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainFAB.setOnClickListener {
            startActivity(Intent(this, AddHookActivity::class.java))
        }
        setBottomNavigation()
    }

    private fun setBottomNavigation() {
        //액티비티 홈 안의 bottom_navigation_home에 접근 요기는 fragment에 관한 접근이기때문에 viewBinding x
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_home) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationHome.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}