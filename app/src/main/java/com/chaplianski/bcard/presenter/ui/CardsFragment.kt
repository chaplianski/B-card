package com.chaplianski.bcard.presenter.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.chaplianski.bcard.R
import com.chaplianski.bcard.databinding.FragmentCardsBinding
import com.chaplianski.bcard.di.DaggerAppComponent
import com.chaplianski.bcard.presenter.adapters.CardColorAdapter
import com.chaplianski.bcard.presenter.adapters.CardsFragmentCardAdapter
import com.chaplianski.bcard.presenter.adapters.StrokeColorAdapter
import com.chaplianski.bcard.presenter.factories.CardsFragmentViewModelFactory
import com.chaplianski.bcard.presenter.helpers.CardsPickerLayoutManager
import com.chaplianski.bcard.presenter.helpers.PhotoPicker
import com.chaplianski.bcard.presenter.viewmodels.CardsFragmentViewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.delay
import javax.inject.Inject


class CardsFragment : Fragment(R.layout.fragment_cards) {

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
        val additionalInfoText: ConstraintLayout = binding.layoutUserInformation.clUserInfo
        val closeButton: Button = binding.layoutUserInformation.btUserInfoClose


        val appbar: AppBarLayout = binding.appbarCardsFragment
        val nameplate: FrameLayout = binding.flCardsFragmentTopInfo

        // **** Additional User Info
        infoButton.setOnClickListener {
            additionalInfoText.visibility = View.VISIBLE
            collapseAppbar(appbar, nameplate)
        }

        closeButton.setOnClickListener {
            additionalInfoText.visibility = View.GONE
            expandAppbar(appbar, nameplate)
        }

        editButton.setOnClickListener {
            findNavController().navigate(R.id.action_cardsFragment_to_editCardFragment)
        }

        // **** Share card
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
            cardsRV.onFlingListener = null
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

            cardFragmentCardAdapter.shortOnClickListener = object: CardsFragmentCardAdapter.ShortOnClickListener{

                override fun shortClick() {
//                    editButton.text = "Input user information"
//                    editInfoText.visibility = View.VISIBLE
//                    appbar.setExpanded(false)
//                    personInfo.visibility = View.VISIBLE
                }

                override fun shortPhoneClick(phone: String) {
                    val i = Intent(Intent.ACTION_DIAL)
                    i.data = Uri.parse("tel:$phone")
                    activity?.startActivity(i)
                }

                override fun shortEmailClick(email: String) {
                    val i = Intent(Intent.ACTION_SEND)
//                    i.data = Uri.parse("mailto:$email")
                    i.type = "text/plain"
                    i.putExtra(Intent.EXTRA_EMAIL, email)
                    activity?.startActivity(Intent.createChooser(i, "Send email"))
                }

                override fun shortLinkedinClick(linkedin: String) {
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse("https:$linkedin"))
                    activity?.startActivity(i)
                }
            }
        }
    }



    private fun expandAppbar(
        appbar: AppBarLayout,
        nameplate: FrameLayout
    ) {
        appbar.setExpanded(true)
        moveNameplateVertical(nameplate, 220f, 0f).start()
    }

    private fun collapseAppbar(
        appbar: AppBarLayout,
        nameplate: FrameLayout
    ) {
        appbar.setExpanded(false)
        moveNameplateVertical(nameplate, 0f, 220f).start()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun moveNameplateVertical(nameplate: View, from: Float, to: Float): Animator {
        val move = ObjectAnimator.ofFloat(nameplate, View.TRANSLATION_Y, from, to)
        move.interpolator = AccelerateInterpolator()
        move.duration = 700
        return move
    }
}
