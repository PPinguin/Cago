package com.cago.pack

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.cago.core.dialogs.alerts.QuestionDialog
import com.cago.pack.di.provider.PackComponentProvider
import com.cago.pack.viewmodels.PackViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import javax.inject.Inject

class PackActivity : AppCompatActivity() {

    @Inject lateinit var viewModel: PackViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        (application as PackComponentProvider).getPackComponent().inject(this)
        setContentView(R.layout.activity_pack)
    }

    @DelicateCoroutinesApi
    override fun onDestroy() {
        super.onDestroy()
    }

    fun help() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_repo))))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.message(com.cago.core.R.string.restart_app)
    }

    @DelicateCoroutinesApi
    override fun onBackPressed() { 
        if(supportFragmentManager.backStackEntryCount == 0) {
            var save = false
            if (viewModel.changed && viewModel.isOwnUser() == true) {
                QuestionDialog({
                    save = true
                }, getString(R.string.close_pack_question)).also { 
                    it.setOnAccepted {
                        viewModel.closePack(save)
                        super.onBackPressed()
                    }
                }
                    .show(supportFragmentManager, getString(R.string.close_question))
            } else {
                viewModel.closePack(save)
                super.onBackPressed()
            }
        }
    }
}