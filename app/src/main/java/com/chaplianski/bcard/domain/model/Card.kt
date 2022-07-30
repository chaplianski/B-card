package com.chaplianski.bcard.domain.model

data class Card(
    val id: Long,
    val name: String,
    val photo: String,
    val phone: String,
    val additionPhone: String,
    val email: String,
    val speciality: String,
    val location: String


    )