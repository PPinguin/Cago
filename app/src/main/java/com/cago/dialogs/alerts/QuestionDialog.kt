package com.cago.dialogs.alerts

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import com.cago.R
import com.cago.dialogs.BaseDialog

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
            }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}