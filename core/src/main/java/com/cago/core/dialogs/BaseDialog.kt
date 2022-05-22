package com.cago.core.dialogs

import android.content.DialogInterface
import android.view.Gravity
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.cago.core.R

abstract class BaseDialog: DialogFragment() {
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply { 
            setBackgroundDrawableResource(R.drawable._background)
            setLayout(
                resources.getDimension(R.dimen.alert_dialog_width).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.CENTER)
        }
    }

    private var onAccepted: (() -> Unit)? = null
    fun setOnAccepted(l:()->Unit){
        onAccepted = l
    }
    
    fun closeDialog(){
        onAccepted?.invoke()
        dismiss()
    }
}