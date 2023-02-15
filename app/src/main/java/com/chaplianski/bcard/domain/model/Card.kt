package com.chaplianski.bcard.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card(
    val id: Long = 0,
    val userId: Long = 0,
    var name: String = "",
    var surname: String = "",
    var photo: String = "",
    var workPhone: String = "",
    var homePhone: String = "",
    var email: String = "",
    var speciality: String = "",
    var organization: String = "",
    var town: String = "",
    var country: String = "",
    var additionalContactInfo: String = "",
    var professionalInfo: String = "",
    var privateInfo: String = "",
    val reference: String = "",
    val cardTexture: String = "",
    val cardTextColor: String = "#0097A7",
    val isCardCorner: Boolean = false,
    val cardFormPhoto: String = "oval",
    var isChecked: Boolean = false
    ): Parcelable