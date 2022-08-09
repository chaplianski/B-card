package com.chaplianski.bcard.presenter.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.chaplianski.bcard.R
import com.chaplianski.bcard.di.DaggerAppComponent
import com.chaplianski.bcard.presenter.factories.DeleteCardFragmentViewModelFactory
import com.chaplianski.bcard.presenter.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.presenter.viewmodels.DeleteCardFragmentViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject


class DeleteCardFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var deleteCardFragmentViewModelFactory: DeleteCardFragmentViewModelFactory
    val deleteCardFragmentViewModel: DeleteCardFragmentViewModel by viewModels { deleteCardFragmentViewModelFactory }

    override fun onAttach(context: Context) {
        DaggerAppComponent.builder()
            .context(context)
            .build()
            .deleteCardFragmentInject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delete_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yesButton: TextView = view.findViewById(R.id.tv_delete_card_fragment_yes)
        val noButton: TextView = view.findViewById(R.id.tv_delete_card_fragment_no)

        val cardId = arguments?.getLong(CURRENT_CARD_ID)

        yesButton.setOnClickListener {
            if (cardId != null) {
                deleteCardFragmentViewModel.deleteCard(cardId)
            }
        }

        noButton.setOnClickListener {
            dismiss()
        }
    }

}