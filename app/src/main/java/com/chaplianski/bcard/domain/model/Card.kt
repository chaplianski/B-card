package com.chaplianski.bcard.domain.model

import android.os.Parcelable
import com.chaplianski.bcard.core.utils.DEFAULT_CARD_CORNER
import com.chaplianski.bcard.core.utils.DEFAULT_CARD_FORM_PHOTO
import com.chaplianski.bcard.core.utils.DEFAULT_CARD_TEXT_COLOR
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card(
    var id: Long = 0,
    var userId: Long = 0,
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
    var cardTexture: String = "",
    val cardTextColor: String = DEFAULT_CARD_TEXT_COLOR,
    val isCardCorner: Boolean = DEFAULT_CARD_CORNER,
    val cardFormPhoto: String = DEFAULT_CARD_FORM_PHOTO,
    var isChecked: Boolean = false
) : Parcelable