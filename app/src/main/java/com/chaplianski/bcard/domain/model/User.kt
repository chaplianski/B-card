package com.chaplianski.bcard.domain.model

data class User(
    val id: Long = 0,
    val login: String,
    val password: String,
    val secretQuestion: String,
    val secretAnswer: String
)