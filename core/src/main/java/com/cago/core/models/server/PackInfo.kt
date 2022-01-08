package com.cago.core.models.server

data class PackInfo(
    val name: String? = null, 
    val path: String? = null, 
    var public: Boolean = false
    )