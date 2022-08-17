package com.chaplianski.bcard.domain.model

import ezvcard.property.Email

data class User(

    val id: Long = 0,
    val email: String,
    val password: String
)