package com.cago.dialogs

import android.content.DialogInterface
import androidx.fragment.app.DialogFragment
import com.cago.R

abstract class BaseDialog: DialogFragment() {
    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.drawable._background)
    }

    private var onAccepted: (() -> Unit)? = null
    fun setOnAccepted(l:()->Unit){
        onAccepted = l
    }

    override fun onDismiss(dialog: DialogInterface) {
        onAccepted?.invoke()
        super.onDismiss(dialog)
    }
}