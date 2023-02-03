package com.chaplianski.bcard.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardSettings(
    val cardId: Long = 0L,
    val cardTexture: String = "paper_025",
    val cardCorner: Boolean = false,
    val cardTextColor: String = "#FF000000",
    val cardAvatarForm: String = "square"
): Parcelable
