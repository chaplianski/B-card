package com.chaplianski.bcard.data.storage.modeldto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chaplianski.bcard.core.utils.DEFAULT_CARD_CORNER
import com.chaplianski.bcard.core.utils.DEFAULT_CARD_FORM_PHOTO
import com.chaplianski.bcard.core.utils.DEFAULT_CARD_TEXT_COLOR


@Entity(tableName = "cards")
data class CardDTO(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long = 0,
    val name: String = "",
    val surname: String = "",
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
    val cardTextColor: String = DEFAULT_CARD_TEXT_COLOR,
    val isCardCorner: Boolean = DEFAULT_CARD_CORNER,
    val cardFormPhoto: String = DEFAULT_CARD_FORM_PHOTO,
    var isChecked: Boolean = false


)
