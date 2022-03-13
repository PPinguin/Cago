package com.cago.home.activities

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.cago.core.databinding.ActivityAuthBinding
import com.cago.core.R
import com.cago.home.di.providers.AuthComponentProvider
import com.cago.home.viewmodels.AuthViewModel
import javax.inject.Inject

class AuthActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: AuthViewModel
    private var binding: ActivityAuthBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        (application as AuthComponentProvider).getAuthComponent().inject(this)
        viewModel.isLoggedIn()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val link = intent.data?.toString()
        if (link != null) {
            binding?.apply {
                email.isVisible = false
                privacy.isVisible = false
                msg.isVisible = false
                progress.isVisible = true
            }
            viewModel.logIn(link)
        } else setupUI()
        
        viewModel.logLiveData.observe(this) {
            if (it) {
                startActivity(Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            }
        }
    }
    
    private fun setupUI(){
        binding?.apply {
            go.setOnClickListener {
                viewModel.sendLink(email.text.toString())
                email.isVisible = false
                privacy.isVisible = false
                progress.isVisible = true
            }
            privacy.setOnClickListener {
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(getString(com.cago.pack.R.string.link_privacy_policy)))
                )
            }
            email.addTextChangedListener {
                go.isVisible = it?.isBlank() != true
            }
        }

        viewModel.sendLiveData.observe(this) {
            binding?.apply {
                progress.isVisible = false
                email.isVisible = !it
                privacy.isVisible = !it
                msg.isVisible = it
                go.text = getString(
                    if (it) R.string.repeat
                    else R.string.go
                )
            }
        }
    }
}