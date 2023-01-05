package com.chaplianski.bcard.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card(
    val id: Long = 0,
    val userId: Long = 0,
    val name: String = "",
    val surname: String = "",
    val photo: String = "",
    val phone: String = "",
    val linkedin: String = "",
    val email: String = "",
    val speciality: String = "",
    val organization: String = "",
    val town: String = "",
    val country: String = "",
    val profileInfo: String = "",
    val education: String = "",
    val professionalSkills: String = "",
    val workExperience: String = "",
    val reference: String = "",
    val cardColor: String = "#FFCDD2",
    val strokeColor: String = "#0097A7",
    val cornerRound: Float = 0F,
    val formPhoto: String = "oval",
    var isChecked: Boolean = false
    ): Parcelable