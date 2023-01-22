package com.chaplianski.bcard.core.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaplianski.bcard.core.adapters.CardListShareFragmentAdapter
import com.chaplianski.bcard.core.factories.CheckCardListLoadFragmentViewModelFactory
import com.chaplianski.bcard.core.viewmodels.CheckCardListLoadFragmentViewModel
import com.chaplianski.bcard.databinding.FragmentCheckCardListLoadBinding
import com.chaplianski.bcard.domain.model.Card
import ezvcard.Ezvcard
import javax.inject.Inject


class CheckCardListLoadFragment : Fragment() {

    var _binding: FragmentCheckCardListLoadBinding? = null
    val binding get() = _binding!!

    @Inject
    lateinit var checkCardListLoadFragmentViewModelFactory: CheckCardListLoadFragmentViewModelFactory
    val checkCardListLoadFragmentViewModel: CheckCardListLoadFragmentViewModel by viewModels { checkCardListLoadFragmentViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckCardListLoadBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveButton = binding.btCheckCardListLoadFragmentSave
        val cancelButton = binding.btCheckCardListLoadFragmentCancel
        val allCheckBox = binding.checkBoxCheckCardListLoadFragmentCheckAll
        val cardRV = binding.rvCheckCardListLoadFragment
        val cardAdapter = CardListShareFragmentAdapter()
        var checkedCardCount = 0
        var checkAllFlag = false
        val listCard = mutableListOf<Card>()

        saveButton.text = "Save [$checkedCardCount]"

        checkCardListLoadFragmentViewModel.loadedCardList.observe(this.viewLifecycleOwner) { cardList ->


            val fullContactList = mutableListOf<ContactContent>()
            val letterList =
                cardList.sortedBy { it.surname }.map { it.surname.first().uppercaseChar() }.toSet()
            letterList.forEach { letter ->
                cardList.sortedBy { it.surname }.forEach { card ->
                    if (letter == card.surname.first().uppercaseChar()) {
                        if (!fullContactList.contains(ContactContent.Letter(letter))){
                            fullContactList.add(ContactContent.Letter(letter))
                        }
                            fullContactList.add(ContactContent.Contact(card))
                    }
                }
            }

            cardRV.layoutManager = LinearLayoutManager(context)
            cardRV.adapter = cardAdapter
            cardAdapter.updateList(fullContactList)

//            cardAdapter.checkBoxListener = object : CardListShareFragmentAdapter.CheckBoxListener {
//                override fun onCheck(card: ContactContent) {
//                    cardList.forEach { cardItem ->
//                        if (card.surname == cardItem.surname) {
//                            cardItem.isChecked = !cardItem.isChecked
//                            if (cardItem.isChecked) checkedCardCount++ else checkedCardCount--
//                        }
//                        saveButton.text = "Save [$checkedCardCount]"
//                    }
//                }
//            }

//            allCheckBox.setOnClickListener {
//                if (!checkAllFlag) {
//                    cardList.forEach { cardItem ->
//                        cardItem.isChecked = true
//                    }
//                    checkedCardCount = cardList.size
//                } else {
//                    cardList.forEach { cardItem ->
//                        cardItem.isChecked = false
//                    }
//                    checkedCardCount = 0
//                }
//                Log.d("MyLog", "${cardList.map { it.isChecked }}")
//                checkAllFlag = !checkAllFlag
//                cardAdapter.updateList(cardList.map { it.copy() })
//                saveButton.text = "Save [$checkedCardCount]"
//            }

            saveButton.setOnClickListener {
                listCard.clear()
                cardList.forEach { cardItem ->
                    if (cardItem.isChecked) {
                        listCard.add(cardItem)
                    }

                }

                Log.d("MyLog", "${listCard.size}")
                if (!listCard.isNullOrEmpty()) {
                    checkCardListLoadFragmentViewModel.saveCheckedCardToDB(listCard)
                }

                //                Log.d("MyLog", "list checked = ${listCard.size}")
            }

        }

    }

    override fun onStart() {
        super.onStart()

        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 222)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 222 && resultCode == Activity.RESULT_OK) {
            data?.data?.also {
                lifecycleScope.launchWhenResumed {
                    val inputStream = context?.contentResolver?.openInputStream(it)
//                val file = File(it.path.toString())
                    val readVcard = Ezvcard.parse(inputStream).all()
                    checkCardListLoadFragmentViewModel.convertVCardListToCardList(readVcard)
//                    readVcard.forEach {
//                        Log.d("MyLog", "readVcard = $it.")
//                    }

                }

            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}

sealed class ContactContent {
    data class Letter(val letter: Char) : ContactContent()
    data class Contact(val card: Card) : ContactContent()
}