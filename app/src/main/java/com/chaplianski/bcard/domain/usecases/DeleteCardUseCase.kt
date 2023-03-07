package com.chaplianski.bcard.domain.usecases

import com.chaplianski.bcard.domain.repository.CardRepository
import javax.inject.Inject

class DeleteCardUseCase @Inject constructor(private val cardRepository: CardRepository) {

    fun execute(cardID: Long) = kotlin.runCatching{
        cardRepository.deleteCard(cardID)
    }
}