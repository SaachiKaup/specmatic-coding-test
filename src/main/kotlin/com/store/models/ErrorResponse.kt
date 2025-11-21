package com.store.models

data class ErrorResponseBody(
    val timestamp: String,
    val status: Int,
    val error: String,
    val path: String
)
