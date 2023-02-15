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
//            CardTexture("paper_45", R.drawable.paper_45, false),
//            CardTexture("paper_7", R.drawable.paper_7, false),

        )
    }

    val cardTextures = mapOf(
        "paper_01" to R.drawable.paper_01,
        "paper_43" to R.drawable.paper_43,
        "paper_08" to R.drawable.paper_08,
        "paper_015" to R.drawable.paper_015,
        "paper_10" to R.drawable.paper_10,
        "paper_019" to R.drawable.paper_019,
        "paper_02" to R.drawable.paper_02,
        "paper_033" to R.drawable.paper_033,
        "paper_025" to R.drawable.paper_025,
        "paper_03" to R.drawable.paper_03,
        "paper_06" to R.drawable.paper_06,
        "paper_09" to R.drawable.paper_09
    )

    fun sdf() {
        cardTextures.keys.forEach {
            if (true) cardTextures[it]
        }

    }
}