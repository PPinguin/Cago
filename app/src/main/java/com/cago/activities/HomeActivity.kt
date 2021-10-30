package com.cago.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.cago.CagoApplication
import com.cago.R
import com.cago.viewmodels.HomeViewModel

class HomeActivity : AppCompatActivity() {

    val viewModel: HomeViewModel by viewModels { 
        HomeViewModel.HomeViewModelFactory((application as CagoApplication).repository)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }
    
    fun openPack(name: String, extra: Bundle? = null){
        startActivity(
            Intent(this, PackActivity::class.java).apply { 
                putExtras(
                    bundleOf("name" to name).also {
                        if(extra != null) it.putAll(extra)
                    }
                )
            },
        )
    }
}