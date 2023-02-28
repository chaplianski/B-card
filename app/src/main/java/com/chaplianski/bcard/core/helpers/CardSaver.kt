package com.chaplianski.bcard.core.helpers

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.chaplianski.bcard.domain.model.Card
import java.net.URI

class CardSaver {

    val saveFileContract = object : ActivityResultContract<List<Card>, URI>(){
        override fun createIntent(context: Context, input: List<Card>): Intent {
            TODO("Not yet implemented")
        }

        override fun parseResult(resultCode: Int, intent: Intent?): URI {
            TODO("Not yet implemented")
        }

    }
}