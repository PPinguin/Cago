package com.cago.home.activities

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.cago.core.R
import com.cago.core.databinding.ActivityHomeBinding
import com.cago.core.dialogs.alerts.QuestionDialog
import com.cago.home.di.providers.HomeComponentProvider
import com.cago.home.viewmodels.HomeViewModel
import com.cago.pack.activities.PackActivity
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: HomeViewModel
    private var binding: ActivityHomeBinding? = null
    
    private val activityForResult = 
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                result.data?.extras?.let { 
                    if(it.getBoolean("actual")) 
                        viewModel.deactualizatePack(it.getString("name", ""))
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        (application as HomeComponentProvider).getHomeComponent().inject(this)
        viewModel.init()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setupUI()
    }

    private fun setupUI() {
        viewModel.loggedIn.observe(this) {
            if (it == false) {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            } else {
                viewModel.initUserInfo()
                binding?.navView?.getHeaderView(0)
                    ?.findViewById<AppCompatTextView>(R.id.email)?.text =
                    viewModel.userInfo?.get("email") ?: getString(R.string.unknown)
            }
        }
        binding?.apply {
            setSupportActionBar(toolbar)
            navView.inflateHeaderView(R.layout.nav_header)
                .findViewById<AppCompatTextView>(R.id.email).text =
                viewModel.userInfo?.get("email") ?: getString(R.string.unknown)
            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.search -> toSearch()
                    R.id.sync -> sync()
                    R.id.docs -> docs()
                    R.id.contact -> contact()
                    R.id.privacy -> privacy()
                }
                drawerLayout.closeDrawer(navView)
                true
            }
            logout.setOnClickListener {
                logOut()
            }
        }
        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.ic_burger)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
    }

    private fun sync() {
        viewModel.sync()
    }

    private fun toSearch() {
        findNavController(R.id.nav_host_fragment).apply {
            if (currentDestination?.id != R.id.searchFragment)
                navigate(R.id.action_menuFragment_to_searchFragment)
        }
    }

    private fun contact() {
        startActivity(
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, "pinguin.dev3712@gmail.com")
            }
        )
    }

    private fun privacy() {
        startActivity(
            Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(com.cago.pack.R.string.link_privacy_policy)))
        )
    }

    fun openPack(name: String, extra: Bundle? = null) {
        activityForResult.launch(
            Intent(this, PackActivity::class.java).apply {
                putExtras(
                    bundleOf("name" to name).also {
                        if (extra != null) it.putAll(extra)
                    }
                )
            },
        )
    }

    private fun logOut() {
        QuestionDialog({
            viewModel.logOut()
        }, getString(R.string.question_logout))
            .show(supportFragmentManager, getString(R.string.log_out))
    }

    private fun docs() {
        startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(com.cago.pack.R.string.link_repo)))
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding?.apply {
                    if (!drawerLayout.isDrawerOpen(navView)) drawerLayout.openDrawer(navView)
                    else drawerLayout.closeDrawer(navView)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.message(R.string.restart_app)
    }
}