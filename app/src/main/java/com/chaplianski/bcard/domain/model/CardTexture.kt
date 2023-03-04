package com.chaplianski.bcard.domain.model

/**
 * Этот класс используется только в view layer ему в домейн делать нечего)
 */
data class CardTexture(
    val cardTextureName: String,
    val cardTextureResource: Int,
    var isChecked: Boolean,
)
