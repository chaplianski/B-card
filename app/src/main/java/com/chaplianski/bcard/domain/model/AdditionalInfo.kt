package com.chaplianski.bcard.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AdditionalInfo(
    val cardId: Long = 0L,
    val address: String = "",
    val workInfo: String = "",
    val privateInfo: String = "",
    val reference: String = ""
): Parcelable
