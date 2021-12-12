package com.cago.home.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.cago.core.R
import com.cago.core.databinding.ActivityHomeBinding
import com.cago.home.di.providers.HomeComponentProvider
import com.cago.home.viewmodels.HomeViewModel
import com.cago.pack.PackActivity
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: HomeViewModel
    
    private var binding: ActivityHomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as HomeComponentProvider).getHomeComponent().inject(this)
        if (!viewModel.isLoggedIn()) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        } else {
            viewModel.initUserInfo()
        }
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setUpUI()
    }
    
    private fun setUpUI() {
        binding?.apply {
            setSupportActionBar(toolbar)
            navView.inflateHeaderView(R.layout.nav_header)
                .apply {
                    findViewById<AppCompatTextView>(R.id.email).text = viewModel.userInfo["email"]
                }
            navView.setupWithNavController(
                (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
                    .navController
            )
        }
        supportActionBar?.apply{
            setHomeAsUpIndicator(R.drawable.ic_burger)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                binding?.apply { 
                    if(!drawerLayout.isDrawerOpen(navView)) drawerLayout.openDrawer(navView)
                    else drawerLayout.closeDrawer(navView)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}