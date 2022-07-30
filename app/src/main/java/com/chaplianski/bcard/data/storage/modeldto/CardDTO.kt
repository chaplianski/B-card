package com.chaplianski.bcard.data.storage.modeldto

data class CardDTO(
    val id: Long,
    val name: String,
    val photo: String,
    val phone: String,
    val additionPhone: String,
    val email: String,
    val speciality: String,
    val location: String
)
