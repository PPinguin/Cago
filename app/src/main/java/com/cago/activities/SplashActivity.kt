package com.cago.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cago.home.activities.HomeActivity
import java.io.File

class SplashActivity: AppCompatActivity() {
    
    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Level 23
            val permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            }
        }
        File(getExternalFilesDir(null)?.absolutePath + "/cache").mkdir()
        startActivity(
            Intent(
                this,
                    HomeActivity::class.java
            )
        )
        finish()
    }
    
}