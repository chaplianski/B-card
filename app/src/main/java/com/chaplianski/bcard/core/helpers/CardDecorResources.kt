package com.chaplianski.bcard.core.helpers

import com.chaplianski.bcard.R
import com.chaplianski.bcard.domain.model.CardTexture

class CardDecorResources {

    val viewColor = listOf(
        R.color.black,
        R.color.purple_700,
        R.color.violet,
        R.color.red,
        R.color.dark_yellow,
        R.color.light_yellow,
        R.color.white
    )
    fun getCardTextureResource(): List<CardTexture> {
        return listOf(
            CardTexture("paper_01", R.drawable.paper_01, false),
            CardTexture("paper_02", R.drawable.paper_02, false),
            CardTexture("paper_03", R.drawable.paper_03, false),
            CardTexture("paper_06", R.drawable.paper_06, false),
            CardTexture("paper_08", R.drawable.paper_08, false),
            CardTexture("paper_09", R.drawable.paper_09, false),
            CardTexture("paper_10", R.drawable.paper_10, false),
            CardTexture("paper_015", R.drawable.paper_015, false),
            CardTexture("paper_016", R.drawable.paper_016, false),
            CardTexture("paper_019", R.drawable.paper_019, false),
            CardTexture("paper_025", R.drawable.paper_025, false),
            CardTexture("paper_031", R.drawable.paper_031, false),
            CardTexture("paper_033", R.drawable.paper_033, false),
            CardTexture("paper_034", R.drawable.paper_034, false),
            CardTexture("paper_035", R.drawable.paper_035, false),
            CardTexture("paper_43", R.drawable.paper_43, false),
            CardTexture("paper_40", R.drawable.paper_40, false),
            CardTexture("paper_42", R.drawable.paper_42, false),
        )
    }

    val background = mapOf(
        "background_32" to R.drawable.background_32,
        "background_03" to R.drawable.background_03,
        "background_12" to R.drawable.background_12,
        "background_18" to R.drawable.background_18,
        "background_05" to R.drawable.background_05,
        "background_09" to R.drawable.background_09
    )
}