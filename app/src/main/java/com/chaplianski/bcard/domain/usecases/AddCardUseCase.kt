package com.chaplianski.bcard.domain.usecases

import com.chaplianski.bcard.R
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddCardUseCase @Inject constructor(private val cardRepository: CardRepository) {

    fun execute(card: Card) = kotlin.runCatching {
        cardRepository.addCard(card)
    }


}