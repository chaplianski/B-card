package com.chaplianski.bcard.data.storage.modeldto

import androidx.room.Entity
import androidx.room.PrimaryKey


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
    val cardTextColor: String = "#0097A7",
    val isCardCorner: Boolean = false,
    val cardFormPhoto: String = "oval",
    var isChecked: Boolean = false


)
