package com.cago.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cago.CagoApplication
import com.cago.R
import com.cago.viewmodels.PackViewModel

class PackActivity : AppCompatActivity() {

    val viewModel: PackViewModel by viewModels {
        PackViewModel
            .PackViewModelFactory((application as CagoApplication).packController)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pack)
    }

    override fun onDestroy() {
        if (isFinishing) viewModel.closePack()
        super.onDestroy()
    }

    fun help() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_repo))))
    }
}