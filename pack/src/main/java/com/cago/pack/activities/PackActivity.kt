package com.cago.pack.activities

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.cago.core.dialogs.alerts.QuestionDialog
import com.cago.pack.R
import com.cago.pack.di.provider.PackComponentProvider
import com.cago.pack.viewmodels.PackViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import javax.inject.Inject

class PackActivity : AppCompatActivity() {

    @Inject lateinit var viewModel: PackViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        (application as PackComponentProvider).getPackComponent().inject(this)
        setContentView(R.layout.activity_pack)
    }

    override fun onStart() {
        super.onStart()
        navController = findNavController(R.id.nav_host_fragment)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.message(com.cago.core.R.string.restart_app)
    }

    @DelicateCoroutinesApi
    override fun onBackPressed() { 
        if (navController.currentDestination?.id == R.id.packFragment) {
            var save = false
            if (viewModel.changed && viewModel.isOwnUser() == true) {
                QuestionDialog({
                    save = true              
                }, getString(R.string.close_pack_question)).also { 
                    it.setOnAccepted {
                        viewModel.closePack(save) {
                            setResult(
                                Activity.RESULT_OK,
                                Intent().apply {
                                    putExtra("name", viewModel.pack.value)
                                    putExtra("actual", save)
                                }
                            )
                            super.onBackPressed()   
                        }
                    }
                }
                    .show(supportFragmentManager, getString(R.string.close_question))
            } else {
                viewModel.closePack(false){
                    super.onBackPressed()
                }
            }
        } else super.onBackPressed()
    }
}