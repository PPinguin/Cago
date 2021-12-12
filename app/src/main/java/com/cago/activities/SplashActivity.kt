package com.cago.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.cago.app.CagoApplication
import com.cago.core.di.CoreModule
import com.cago.core.repository.managers.FirebaseManager
import com.cago.home.activities.AuthActivity
import com.cago.home.activities.HomeActivity
import com.cago.home.di.RepoModule
import dagger.Component
import javax.inject.Inject
import javax.inject.Singleton

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