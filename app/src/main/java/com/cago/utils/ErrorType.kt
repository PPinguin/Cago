package com.cago.utils

import com.cago.R

enum class ErrorType(private val resource: Int) {
    ERROR_CREATE(R.string.error_create),
    ERROR_RENAME(R.string.error_rename),
    ERROR_DELETE(R.string.error_delete),
    ERROR_OPEN(R.string.error_open),
    ERROR_RESULTS(R.string.error_results),
    ERROR_UPLOAD(R.string.error_upload),
    ERROR_DOWNLOAD(R.string.error_download),
    ERROR_SHARE(R.string.error_share),
    ERROR_FIELD_NOT_CHOSEN(R.string.error_field_not_chosen);
    
    fun getResource() = resource 
}