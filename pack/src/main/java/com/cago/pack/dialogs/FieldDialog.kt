package com.cago.pack.dialogs

import android.content.DialogInterface
import com.cago.core.models.logic.Field
import com.cago.core.models.logic.fields.*
import com.cago.pack.dialogs.fields.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class FieldDialog: BottomSheetDialogFragment() {
    private var onAccepted: (() -> Unit)? = null
    fun setOnAccepted(l:()->Unit){
        onAccepted = l
    }

    override fun onDismiss(dialog: DialogInterface) {
        onAccepted?.invoke()
        super.onDismiss(dialog)
    }

    companion object{
        fun buildDialog(field: Field): FieldDialog? =
            when (field) {
                is NumberInput -> NumberInputDialog(field)
                is PercentInput -> PercentInputDialog(field)
                is BooleanInput -> BooleanInputDialog(field)
                is CountInput -> CountInputDialog(field)
                is TextInput -> TextInputDialog(field)
                else -> null
            }
    }
}