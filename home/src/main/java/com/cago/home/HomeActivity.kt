package com.cago.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.cago.home.di.providers.HomeComponentProvider
import com.cago.home.viewmodels.HomeViewModel
import com.cago.pack.PackActivity
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {

    @Inject lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as HomeComponentProvider).getHomeComponent().inject(this)
        setContentView(R.layout.activity_home)
    }

    fun openPack(name: String, extra: Bundle? = null) {
        startActivity(
            Intent(this, PackActivity::class.java).apply {
                putExtras(
                    bundleOf("name" to name).also {
                        if (extra != null) it.putAll(extra)
                    }
                )
            },
        )
    }
}