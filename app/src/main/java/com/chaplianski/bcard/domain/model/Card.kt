package com.chaplianski.bcard.domain.model

data class Card(
    val id: Long = 0,
    val name: String = "",
    val photo: String = "",
    val phone: String = "",
    val linkedin: String = "",
    val email: String = "",
    val speciality: String = "",
    val location: String = "",
    val profilInfo: String = "",
    val education: String = "",
    val professionalSkills: String = "",
    val workExperience: String = "",
    val reference: String = "",
    val cardColor: String = "#FFCDD2",
    val strokeColor: String = "#0097A7",
    val cornerRound: Float = 0F,
    val formPhoto: String = "oval"



    )