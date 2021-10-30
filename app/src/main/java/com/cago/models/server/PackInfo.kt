package com.cago.models.server

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class PackInfo(
    val name: String? = null, 
    val path: String? = null, 
    var public: Boolean = false
    )