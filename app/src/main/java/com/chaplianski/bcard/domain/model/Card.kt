package com.chaplianski.bcard.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card(
    val id: Long = 0,
    val userId: Long = 0,
    var name: String = "",
    var surname: String = "",
    val photo: String = "",
    val workPhone: String = "",
    val homePhone: String = "",
    val email: String = "",
    val speciality: String = "",
    val organization: String = "",
    val town: String = "",
    val country: String = "",
    val additionalContactInfo: String = "",
    val professionalInfo: String = "",
    val privateInfo: String = "",
    val reference: String = "",
    val cardTexture: String = "",
    val cardTextColor: String = "#0097A7",
    val isCardCorner: Boolean = false,
    val cardFormPhoto: String = "oval",
    var isChecked: Boolean = false
    ): Parcelable