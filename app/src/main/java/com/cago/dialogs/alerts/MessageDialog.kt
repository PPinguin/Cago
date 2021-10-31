package com.cago.dialogs.alerts

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.cago.R


class MessageDialog(
    private val listener: ()->Unit, 
    private val message: String
    ):DialogFragment()  {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it).apply { 
                setTitle(getString(R.string.warning))
                setMessage(message)
                isCancelable = false
                setPositiveButton(R.string.positive_btn){ _, _ -> listener()}
            }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.drawable._background)
    }
}