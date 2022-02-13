package com.cago.core.dialogs.alerts

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.cago.core.R
import com.cago.core.dialogs.BaseDialog

class QuestionDialog(
    private val listener: ()->Unit,
    private val question: String
): BaseDialog() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it).apply {
                setMessage(question)
                setPositiveButton(getString(R.string.yes)){ d, _ ->
                    listener()
                    d.dismiss()
                }
                setNegativeButton(getString(R.string.no)){ d, _ -> d.dismiss()}
            }.create().apply { setCanceledOnTouchOutside(false) }
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}