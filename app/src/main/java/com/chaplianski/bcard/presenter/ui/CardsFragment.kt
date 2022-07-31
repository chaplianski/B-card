package com.chaplianski.bcard.presenter.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.chaplianski.bcard.R
import com.chaplianski.bcard.databinding.FragmentCardsBinding
import com.chaplianski.bcard.di.DaggerAppComponent
import com.chaplianski.bcard.presenter.adapters.CardsFragmentCardAdapter
import com.chaplianski.bcard.presenter.factories.CardsFragmentViewModelFactory
import com.chaplianski.bcard.presenter.helpers.CardsPickerLayoutManager
import com.chaplianski.bcard.presenter.viewmodels.CardsFragmentViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.coroutines.delay
import javax.inject.Inject


class CardsFragment : Fragment() {

    @Inject
    lateinit var cardsFragmentViewModelFactory: CardsFragmentViewModelFactory
    val cardsFragmentViewModel: CardsFragmentViewModel by viewModels { cardsFragmentViewModelFactory }

    var _binding: FragmentCardsBinding? = null
    val binding: FragmentCardsBinding get() = _binding!!

    override fun onAttach(context: Context) {
        DaggerAppComponent.builder()
            .context(context)
            .build()
            .cardsFragmentInject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val infoButton: Button = binding.btCardsFragmentAddInfo
        val editButton: Button = binding.btCardsFragmentEdit
        val shareButton: Button = binding.btCardsFragmentShare
        val deleteButton: Button = binding.btCardsFragmentDelete
        val editInfoText: ConstraintLayout = binding.layoutUserInformation.clUserInfoEdit
        val saveButtonEdit: Button = binding.layoutUserInformation.btUserInfoEdit
        val appbar: AppBarLayout = binding.appbarCardsFragment

        infoButton.setOnClickListener {

        }

        editButton.setOnClickListener {
            editInfoText.visibility = View.VISIBLE
//            appbar.scrollTo(0, 1050)
            appbar.setExpanded(false)
//            appbar.visibility = View.GONE
        }

        saveButtonEdit.setOnClickListener {
            editInfoText.visibility = View.GONE
            appbar.setExpanded(true)
        }

        shareButton.setOnClickListener {

        }

        deleteButton.setOnClickListener {

        }

        // ******  Cards wheel  ********

        val cardsRV: RecyclerView = view.findViewById(R.id.rv_cards_fragment_cards)
        val cardsPickerLayoutManager =
            CardsPickerLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        cardsFragmentViewModel.getCards()
        cardsFragmentViewModel.cards.observe(this.viewLifecycleOwner) { listCards ->

            val cardFragmentCardAdapter = CardsFragmentCardAdapter(listCards, cardsRV)
            val tasksSnapHelper: SnapHelper = LinearSnapHelper()
            cardsRV.layoutManager = cardsPickerLayoutManager
            cardsRV.adapter = cardFragmentCardAdapter
            tasksSnapHelper.attachToRecyclerView(cardsRV)

            lifecycleScope.launchWhenCreated {
                delay(500)
                cardsRV.scrollToPosition(0)
            }

            cardsPickerLayoutManager.setOnScrollStopListener(object :
                CardsPickerLayoutManager.CardScrollStopListener {
                override fun selectedView(view: View?) {
                    val userName = view?.findViewById<TextView>(R.id.tv_card_fragment_item_name)
                    val userAvatar =
                        view?.findViewById<ImageView>(R.id.iv_card_fragment_item_avatar)
                    cardsFragmentViewModel.transferData(
                        userName?.text.toString(),
                        userAvatar.toString()
                    )
                }
            })

            cardsFragmentViewModel.currentUser.observe(this.viewLifecycleOwner) { cardData ->
                val userName: TextView = view.findViewById(R.id.tv_cards_fragment_name)
                userName.text = cardData[0]
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
