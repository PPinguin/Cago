package com.cago.core.dialogs.alerts

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.cago.core.R
import com.cago.core.dialogs.BaseDialog

class QuestionDialog(
    private val listener: ()->Unit,
    private val question: String,
    private val cancelable: Boolean = false
): BaseDialog() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it).apply {
                setMessage(question)
                setPositiveButton(getString(R.string.yes)){ _, _ ->
                    listener()
                    closeDialog()
                }
                if(cancelable)
                    setNeutralButton(getString(R.string.cancel)){ d, _ ->
                        d.dismiss()
                    }
                setNegativeButton(getString(R.string.no)){ _, _ -> closeDialog()}
            }.create().apply { setCanceledOnTouchOutside(false) }
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}