package com.cago.pack

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import com.cago.pack.di.provider.PackComponentProvider
import com.cago.pack.viewmodels.PackViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import javax.inject.Inject

class PackActivity : AppCompatActivity() {

    @Inject lateinit var viewModel: PackViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        (application as PackComponentProvider).getPackComponent().inject(this)
        setContentView(R.layout.activity_pack)
        
    }

    override fun onStart() {
        super.onStart()
        findNavController(R.id.nav_host_fragment)
            .addOnDestinationChangedListener { _, destination, _ ->
                if(destination.id == R.id.packFragment) viewModel.run()
            }
    }

    @DelicateCoroutinesApi
    override fun onDestroy() {
        if (isFinishing) viewModel.closePack()
        super.onDestroy()
    }

    fun help() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_repo))))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.message(com.cago.core.R.string.restart_app)
    }
}