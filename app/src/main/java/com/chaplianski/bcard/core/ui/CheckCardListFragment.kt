package com.chaplianski.bcard.core.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaplianski.bcard.core.adapters.CardListShareFragmentAdapter
import com.chaplianski.bcard.core.factories.CheckCardListFragmentViewModelFactory
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.core.viewmodels.CheckCardListFragmentViewModel
import com.chaplianski.bcard.databinding.FragmentCheckCardListBinding
import com.chaplianski.bcard.di.DaggerApp
import com.chaplianski.bcard.domain.model.Card
import javax.inject.Inject


class CheckCardListFragment : Fragment() {

    var _binding: FragmentCheckCardListBinding? = null
    val binding get() = _binding!!

    @Inject
    lateinit var checkCardListFragmentViewModelFactory: CheckCardListFragmentViewModelFactory
    val checkCardListFragmentViewModel: CheckCardListFragmentViewModel by viewModels { checkCardListFragmentViewModelFactory }

    override fun onAttach(context: Context) {
        (context.applicationContext as DaggerApp)
            .getAppComponent()
            .checkCardListFragmentInject(this)
        super.onAttach(context)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding = FragmentCheckCardListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardRV = binding.rvCheckCardFragment
        val cardListAdapter = CardListShareFragmentAdapter()
        val okButton = binding.btCheckCardFragmentSave
        val quantityCheckedCards = binding.tvCheckCardListFragmentQuantityCards
        val allCheckBox = binding.checkBoxCheckCardListFragmentCheckAll
        val currentCardId = arguments?.getLong(CURRENT_CARD_ID)
        var checkedCardCount = 0
        var checkAllFlag = false

        quantityCheckedCards.text = "$checkedCardCount cards"

        checkCardListFragmentViewModel.getCardList()

        checkCardListFragmentViewModel.cardList.observe(this.viewLifecycleOwner){ cardList ->

            var positionCurrentCheckedCard = 0
            if (currentCardId != null){
                cardList.forEachIndexed { index, cardItem ->
                    if (cardItem.id == currentCardId) {
                        cardItem.isChecked = true
                        positionCurrentCheckedCard = index
                    }
                }
                checkedCardCount = 1
                quantityCheckedCards.text = "$checkedCardCount cards"
            }

            cardRV.layoutManager = LinearLayoutManager(context)
            cardRV.adapter = cardListAdapter
            cardListAdapter.updateList(cardList)
            cardRV.scrollToPosition(positionCurrentCheckedCard)

            val listCard = mutableListOf<Card>()

            cardListAdapter.checkBoxListener = object : CardListShareFragmentAdapter.CheckBoxListener{
                override fun onCheck(card: Card) {
                    cardList.forEach { cardItem ->
                        if (cardItem.id == card.id) {
                            cardItem.isChecked = !cardItem.isChecked
                            if (cardItem.isChecked) checkedCardCount++ else checkedCardCount--
                        }
                        quantityCheckedCards.text = "$checkedCardCount cards"
                    }
                }
            }

            okButton.setOnClickListener {
                cardList.forEach { cardItem ->
                    if (cardItem.isChecked) listCard.add(cardItem)
                }
//                Log.d("MyLog", "list checked = ${listCard.size}")
            }

            allCheckBox.setOnClickListener {
               if (checkAllFlag){
                   cardList.forEach { cardItem ->
                       cardItem.isChecked = false

                   }
                checkedCardCount = 0
               } else {
                   cardList.forEach { cardItem ->
                       cardItem.isChecked = true

                   }
                   checkedCardCount = cardList.size
               }
                checkAllFlag = !checkAllFlag
                cardListAdapter.updateList(cardList.map { it.copy() })
                quantityCheckedCards.text = "$checkedCardCount cards"
            }

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}