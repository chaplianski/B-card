package com.chaplianski.bcard.core.helpers

import android.util.Log
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatButton
import com.chaplianski.bcard.core.adapters.CardListShareFragmentAdapter
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.model.ContactContent

class ProcessCard {

     fun fillCardAdapter(
        listCard: List<Card>,
        addButton: AppCompatButton,
        cardListAdapter: CardListShareFragmentAdapter,
        checkboxAllCards: CheckBox,
//        loadingProgressBar: LottieAnimationView
    ) {
//        Log.d("MyLog", "fill card list = $listCard")

        var cardList = emptyList<Card>()
        var checkedCardCount = 0
        cardList = listCard
        val newContactList = mutableListOf<ContactContent>()
        cardList.forEach {

            if (it.surname.isEmpty())  {
                it.surname = it.name
                it.name = ""
            }
//            Log.d("MyLog", "name = ${it.name}, surname = ${it.surname}, letter = ${it.surname.first().uppercaseChar()}")
        }

        var newLetterList = cardList
            .sortedBy { it.surname }
            .map { it.surname.first().uppercaseChar()}
            .toSet()

        Log.d("MyLog", "letters = $newLetterList")
        newLetterList.forEach { letter ->

            cardList
                .sortedBy { it.surname }
                .forEachIndexed { index, card ->

                    if (letter == card.surname.first().uppercaseChar()) {
                        if (!newContactList.contains(ContactContent.Letter(letter))) {
                            newContactList.add(ContactContent.Letter(letter))
                        }
                        newContactList.add(ContactContent.Contact(card))
                    }
                }
            addButton.text = "Add [$checkedCardCount]"
        }

        cardListAdapter.updateList(newContactList)

        cardListAdapter.checkBoxListener =
            object : CardListShareFragmentAdapter.CheckBoxListener {
                override fun onCheck(card: ContactContent.Contact) {
                    cardList.forEach { cardItem ->
                        if (cardItem.name == card.card.name && cardItem.surname == card.card.surname) {
                            cardItem.isChecked = !cardItem.isChecked
                            if (cardItem.isChecked) checkedCardCount++ else checkedCardCount--
                        }
                        addButton.text = "Add [$checkedCardCount]"
                    }
                }
            }

        var allCardCheckFlag = false
        checkboxAllCards.setOnClickListener {
            if (!allCardCheckFlag) {
                cardList.forEach {
                    it.isChecked = true
                }
                checkedCardCount = cardList.size
            } else {
                cardList.forEach {
                    it.isChecked = false
                }
                checkedCardCount = 0
            }

            newContactList.clear()
            newLetterList =
                cardList.sortedBy { it.surname }.map { it.surname.first().uppercaseChar() }
                    .toSet()
            newLetterList.forEach { letter ->
                cardList.sortedBy { it.surname }.forEachIndexed { index, card ->
                    if (letter == card.surname.first().uppercaseChar()) {
                        if (!newContactList.contains(ContactContent.Letter(letter))) {
                            newContactList.add(ContactContent.Letter(letter))
                        }
                        newContactList.add(ContactContent.Contact(card))
                    }
                }
                addButton.text = "Save [$checkedCardCount]"
            }
            cardListAdapter.updateList(newContactList)
            allCardCheckFlag = !allCardCheckFlag
        }
    }
}