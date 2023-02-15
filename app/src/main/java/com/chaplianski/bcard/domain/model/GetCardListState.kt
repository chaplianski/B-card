package com.chaplianski.bcard.domain.model


sealed class GetCardListState{
    object Loading: GetCardListState()
    class GetCardList(val cardList: List<Card>): GetCardListState()
    class UpdateCard(val result: Int): GetCardListState()
    class DeleteCard(val result: Int): GetCardListState()
    class AddCard(val cardId: Long): GetCardListState()
    class Failure(val exception: String): GetCardListState()

}