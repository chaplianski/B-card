package com.chaplianski.bcard.presenter.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.chaplianski.bcard.R
import com.chaplianski.bcard.di.DaggerAppComponent
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.presenter.adapters.CardsFragmentCardAdapter
import com.chaplianski.bcard.presenter.factories.CardsFragmentViewModelFactory
import com.chaplianski.bcard.presenter.helpers.CardsPickerLayoutManager
import com.chaplianski.bcard.presenter.viewmodels.CardsFragmentViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject


class CardsFragment : Fragment() {

    @Inject
    lateinit var cardsFragmentViewModelFactory: CardsFragmentViewModelFactory
    val cardsFragmentViewModel: CardsFragmentViewModel by viewModels { cardsFragmentViewModelFactory }

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // ******  Cards wheel  ********

        val cardsRV: RecyclerView = view.findViewById(R.id.rv_cards_fragment_cards)
        val cardsPickerLayoutManager = CardsPickerLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        cardsFragmentViewModel.getCards()

        cardsFragmentViewModel.cards.observe(this.viewLifecycleOwner){

            val cardFragmentCardAdapter = CardsFragmentCardAdapter(it, cardsRV)
            val tasksSnapHelper: SnapHelper = LinearSnapHelper()


//            if (it.size > 1) {
                cardsRV.layoutManager = cardsPickerLayoutManager
//            }

            cardsRV.adapter = cardFragmentCardAdapter
            tasksSnapHelper.attachToRecyclerView(cardsRV)

            lifecycleScope.launchWhenCreated {
                delay(500)
                cardsRV.scrollToPosition(0)
            }

        }
    }


}
