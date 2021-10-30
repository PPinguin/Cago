package com.cago.dialogs

import androidx.fragment.app.DialogFragment
import com.cago.R

abstract class AlertDialog: DialogFragment() {
    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.drawable._background)
    }
}