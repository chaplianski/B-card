package com.chaplianski.bcard.core.model

import android.os.Parcelable
import com.chaplianski.bcard.core.utils.DEFAULT_CARD_FORM_PHOTO
import com.chaplianski.bcard.core.utils.DEFAULT_CARD_TEXTURE
import com.chaplianski.bcard.core.utils.DEFAULT_CARD_TEXT_COLOR
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardSettings(
    val cardId: Long = 0L,
    val cardTexture: String = DEFAULT_CARD_TEXTURE,
    val cardCorner: Boolean = false,
    val cardTextColor: String = DEFAULT_CARD_TEXT_COLOR,
    val cardAvatarForm: String = DEFAULT_CARD_FORM_PHOTO
): Parcelable
