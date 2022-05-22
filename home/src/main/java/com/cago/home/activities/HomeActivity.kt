package com.cago.home.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import com.cago.core.R
import com.cago.core.databinding.ActivityHomeBinding
import com.cago.core.utils.ErrorType
import com.cago.home.di.providers.HomeComponentProvider
import com.cago.home.viewmodels.HomeViewModel
import com.cago.pack.activities.PackActivity
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: HomeViewModel
    private var binding: ActivityHomeBinding? = null
    
    private val getPack = registerForActivityResult(ActivityResultContracts.GetContent()){
        try {
            contentResolver.query(it,null,null,null,null)
                ?.use { cursor ->
                    val i = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    val name = cursor.getString(i)
                    if(name.takeLast(3) == ".cg") openPack(name.dropLast(3), it.toString())
                    else throw Exception()
                }
        } catch(e: Exception){
            viewModel.message(ErrorType.ERROR_ADD.getResource())  
        } 
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        (application as HomeComponentProvider).getHomeComponent().inject(this)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setupUI()
    }

    private fun setupUI() {
        binding?.apply {
            setSupportActionBar(toolbar)
            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.open -> open()
                    R.id.docs -> docs()
                    R.id.contact -> contact()
                    R.id.privacy -> privacy()
                }
                drawerLayout.closeDrawer(navView)
                true
            }
            version = getString(R.string.version, 
                packageManager.getPackageInfo(packageName, 0).versionName)
        }
        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.ic_burger)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
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

    fun openPack(name: String, path: String? = null) {
        startActivity(
            Intent(this, PackActivity::class.java).apply {
                putExtras(
                    bundleOf("name" to name).also {
                        if (path != null) it.putString("path", path)
                    }
                )
            },
        )
    }
    
    private fun open(){
        getPack.launch("application/*")
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
}