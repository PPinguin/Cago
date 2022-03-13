package com.cago.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.cago.home.activities.HomeActivity

class SplashActivity: AppCompatActivity() {
    
    override fun onStart() {
        super.onStart()
        startActivity(
            Intent(
                this,
                    HomeActivity::class.java
            )
        )
        finish()
    }
    
}