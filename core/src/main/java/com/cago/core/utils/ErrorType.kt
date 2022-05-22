package com.cago.core.utils

import com.cago.core.R

enum class ErrorType(private val resource: Int) {
    ERROR_CREATE(R.string.error_create),
    ERROR_DELETE(R.string.error_delete),
    ERROR_OPEN(R.string.error_open),
    ERROR_FIELD_NOT_CHOSEN(R.string.error_field_not_chosen),
    ERROR_ADD(R.string.error_file_open);

    fun getResource() = resource 
}