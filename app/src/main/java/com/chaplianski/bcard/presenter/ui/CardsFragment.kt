package com.chaplianski.bcard.presenter.ui

import android.animation.Animator
import android.animation.ObjectAnimator
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
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.chaplianski.bcard.databinding.FragmentCardsBinding
import com.chaplianski.bcard.di.DaggerAppComponent
import com.chaplianski.bcard.presenter.adapters.CardsFragmentCardAdapter
import com.chaplianski.bcard.presenter.factories.CardsFragmentViewModelFactory
import com.chaplianski.bcard.presenter.helpers.CardsPickerLayoutManager
import com.chaplianski.bcard.presenter.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.presenter.utils.CustomFab
import com.chaplianski.bcard.presenter.utils.init
import com.chaplianski.bcard.presenter.viewmodels.CardsFragmentViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.Behavior.DragCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class CardsFragment : Fragment(com.chaplianski.bcard.R.layout.fragment_cards) {

    @Inject
    lateinit var cardsFragmentViewModelFactory: CardsFragmentViewModelFactory
    val cardsFragmentViewModel: CardsFragmentViewModel by viewModels { cardsFragmentViewModelFactory }

    var _binding: FragmentCardsBinding? = null
    val binding: FragmentCardsBinding get() = _binding!!
    var currentCardId = 1L

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

        val additionalInfoText: ConstraintLayout = binding.layoutUserInformation.clUserInfo
        val closeButton: Button = binding.layoutUserInformation.btUserInfoClose

        val fabSettings: CustomFab = binding.fabCardsFragmentSettings
        val fabEdit: FloatingActionButton = binding.fabCardsFragmentEdit
        val fabDelete: FloatingActionButton = binding.fabCardsFragmentDelete
        val fabShare: FloatingActionButton = binding.fabCardsFragmentShare
        val fabExit: FloatingActionButton = binding.fabCardsFragmentExit

        val appbar: AppBarLayout = binding.appbarCardsFragment
        val nameplate: FrameLayout = binding.flCardsFragmentTopInfo
        val instruction: TextView = binding.tvCardsFragmentInstruction

        val profileInfo: TextView = binding.layoutUserInformation.userInformationProfileInfo
        val profSkills: TextView = binding.layoutUserInformation.userInformationProfSkills
        val education: TextView = binding.layoutUserInformation.userInformationEducation
        val workExperience: TextView = binding.layoutUserInformation.userInformationWorkExperience
        val reference: TextView = binding.layoutUserInformation.userInformationReference

        // **** Additional User Info
//        infoButton.setOnClickListener {
//            additionalInfoText.visibility = View.VISIBLE
//            collapseAppbar(appbar, nameplate)
//        }

        setupFABs(fabSettings, fabEdit, fabDelete, fabShare, fabExit)


        closeButton.setOnClickListener {
            additionalInfoText.visibility = View.GONE
            expandAppbar(appbar, nameplate)
        }

        fabEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(CURRENT_CARD_ID, currentCardId)
            findNavController().navigate(com.chaplianski.bcard.R.id.action_cardsFragment_to_editCardFragment, bundle)
        }

        // **** Share card
        fabShare.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(CURRENT_CARD_ID, currentCardId)
            findNavController().navigate(com.chaplianski.bcard.R.id.action_cardsFragment_to_shareFragment, bundle)
        }

        fabDelete.setOnClickListener {
            showDialog(currentCardId)
        }

        fabExit.setOnClickListener {
            activity?.finishAffinity()
        }

        appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset - 154) == (appbar.height)) {
                fabSettings.hide()
                additionalInfoText.visibility = View.VISIBLE
            }
            if (Math.abs(verticalOffset) == 0) {
                fabSettings.show()
                additionalInfoText.visibility = View.GONE
            }
        }

        // ******  Cards wheel  ********

        val cardsRV: RecyclerView = view.findViewById(com.chaplianski.bcard.R.id.rv_cards_fragment_cards)
        val cardsPickerLayoutManager =
            CardsPickerLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val cardFragmentCardAdapter = CardsFragmentCardAdapter(cardsRV) //(listCards, cardsRV)
        val cardsSnapHelper: SnapHelper = LinearSnapHelper()
        cardsRV.layoutManager = cardsPickerLayoutManager
        cardsRV.adapter = cardFragmentCardAdapter
        cardsRV.onFlingListener = null
        cardsSnapHelper.attachToRecyclerView(cardsRV)

        lifecycleScope.launchWhenCreated {
            delay(500)
            cardsRV.scrollToPosition(0)
        }


        cardsFragmentViewModel.getCards()

        cardsFragmentViewModel.cards.observe(this.viewLifecycleOwner) { listCards ->
            cardFragmentCardAdapter.updateData(listCards)

            // Condition not empty list cards
            if (listCards.size > 0){
                val currentPosition = cardsSnapHelper.getSnapPosition(cardsRV)
                listCards[currentPosition].id
                Log.d("MyLog", "position1 = $currentPosition, listSize1 = ${listCards.size} " )
                cardsFragmentViewModel.getCard(listCards[currentPosition].id)


            }



            setupDialog(cardFragmentCardAdapter)
//            Log.updateData(listCards)

            cardsPickerLayoutManager.setOnScrollStopListener(object :
                CardsPickerLayoutManager.CardScrollStopListener {
                override fun selectedView(view: View?) {
                    val cardId = view?.findViewById<TextView>(com.chaplianski.bcard.R.id.tv_card_fragment_id)
                    val userName = view?.findViewById<TextView>(com.chaplianski.bcard.R.id.tv_card_fragment_item_name)
                    val userAvatar =
                        view?.findViewById<TextView>(com.chaplianski.bcard.R.id.tv_card_fragment_uri)

                    val currentPos = cardsSnapHelper.getSnapPosition(cardsRV)
                    if (currentPos == listCards.size){
//                        appbar.isLiftOnScroll = false
                        fabSettings.visibility = View.INVISIBLE
                        instruction.visibility = View.VISIBLE
                    setAppBarDragging(false, appbar)


                    } else {
                        fabSettings.visibility = View.VISIBLE
                        instruction.visibility = View.INVISIBLE
                        setAppBarDragging(true, appbar)
                    }

//                    nestedScrollView.isNestedScrollingEnabled = currentPos != listCards.size
                    Log.d("MyLog", "position = $currentPos, listSize = ${listCards.size} " )

                    if (userName?.text?.equals(null) != true && userAvatar?.text?.equals(null) != true && cardId?.text?.equals(
                            null
                        ) != true
                    ) {
                        if (cardId != null) {
                            cardsFragmentViewModel.getCard(cardId.text.toString().toLong()
                            )
                        }
                    }
                }

            })

            cardsFragmentViewModel.currentCard.observe(this.viewLifecycleOwner) { card ->
                val userName: TextView = view.findViewById(com.chaplianski.bcard.R.id.tv_cards_fragment_name)
                val userAvatar: ImageView = view.findViewById(com.chaplianski.bcard.R.id.iv_cards_fragment_avatar)
                currentCardId = card.id
                userName.text = card.name
                context?.let {
                    Glide.with(it).load(card.photo)
                        .override(150, 150)
                        .centerCrop()
                        .into(userAvatar)
                }

                profileInfo.text = card.profilInfo
                profSkills.text = card.professionalSkills
                education.text = card.education
                workExperience.text = card.workExperience
                reference.text = card.reference
            }

            cardFragmentCardAdapter.shortOnClickListener =
                object : CardsFragmentCardAdapter.ShortOnClickListener {

                    override fun shortClick() {
                        findNavController().navigate(com.chaplianski.bcard.R.id.action_cardsFragment_to_editCardFragment)

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

    private fun setAppBarDragging(newValue: Boolean, appBarLayout: AppBarLayout) {
        val params = appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = AppBarLayout.Behavior()
        behavior.setDragCallback(object : DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return newValue
            }
        })
        params.behavior = behavior
    }



    private fun setupFABs(
        fabSettings: FloatingActionButton,
        fabEdit: FloatingActionButton,
        fabDelete: FloatingActionButton,
        fabShare: FloatingActionButton,
        fabExit: FloatingActionButton
    ) {

        fabEdit.init(-340f, -120f)
        fabShare.init(-120f, -250f)
        fabDelete.init(120f, -250f)
        fabExit.init(340f, -120f)

        fabSettings.setOnClickListener {
            if (fabEdit.isOrWillBeShown) {
                fabEdit.hide()
                fabDelete.hide()
                fabShare.hide()
                fabExit.hide()
            } else {
                fabEdit.show()
                fabDelete.show()
                fabShare.show()
                fabExit.show()
            }
        }
    }

    fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
        val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        return layoutManager.getPosition(snapView)
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

    fun showDialog(cardId: Long) {
        DeleteCardFragment.show(parentFragmentManager, cardId)
    }

    fun setupDialog(cardFragmentCardAdapter: CardsFragmentCardAdapter) {
        DeleteCardFragment.setupListener(parentFragmentManager, this) {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    delay(600)
                    cardsFragmentViewModel.getCards()
                }
            }
        }
    }
}

private fun animationMove(button: View, from: Float, to: Float): Animator {
    val logoBegin = ObjectAnimator.ofFloat(button, View.TRANSLATION_Y, from, to)
    logoBegin.interpolator = AccelerateInterpolator()
    logoBegin.duration = 700
    return logoBegin
}
