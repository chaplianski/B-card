package com.chaplianski.bcard.core.ui

import android.R.id.button1
import android.R.id.button2
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
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
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
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.adapters.CardsFragmentCardAdapter
import com.chaplianski.bcard.core.factories.CardsFragmentViewModelFactory
import com.chaplianski.bcard.core.helpers.CardsPickerLayoutManager
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.core.utils.animateVisibility
import com.chaplianski.bcard.core.utils.init
import com.chaplianski.bcard.core.viewmodels.CardsFragmentViewModel
import com.chaplianski.bcard.databinding.FragmentCardsBinding
import com.chaplianski.bcard.di.DaggerApp
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
    var imageUri = ""

    override fun onAttach(context: Context) {
        (context.applicationContext as DaggerApp)
            .getAppComponent()
            .cardsFragmentInject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val additionalInfoText = binding.layoutUserInformation.clUserInfo
//        val closeButton = binding.layoutUserInformation.btUserInfoClose

        val fabSettings = binding.btCardsFragmentSettings
        val fabEdit = binding.btCardsFragmentEdit
        val fabDelete = binding.fabCardsFragmentDelete
        val fabShare = binding.fabCardsFragmentShare
        val fabExit = binding.fabCardsFragmentExit


        val motionLayout = binding.motionLayoutFragmentCards
        val sortButton = binding.btCardsFragmentSort
        val searchButton = binding.btCardsFragmentSearch
        val leftPanelImage = binding.ivFragmentCardsLeftPanel
        val rightPanelImage = binding.ivFragmentCardsRightPanel


//        view.setOnClickListener{
//            when(it){
//                sortButton -> {
////                    motionLayout.setTransition(R.id.sort_button_click_start, R.id.sort_button_click_finish)
//                    motionLayout.setTransition(R.id.sort_button_click)
//
//                }
//                searchButton -> {
//                    motionLayout.setTransition(R.id.search_button_click)
//
////                    motionLayout.setTransition(R.id.search_button_click_start, R.id.search_button_click_finish)
//                }
//            }
//
//            motionLayout.transitionToEnd()
//        }
        var sortButtonSwitch = true
        var searchButtonSwitch = true
        var leftPanelSwitch = true
        var rightPanelSwitch = true

        sortButton.setOnClickListener {
            if (sortButtonSwitch){
                motionLayout.setTransition(R.id.sort_button_click_forward)
                motionLayout.transitionToEnd()
                sortButtonSwitch = false
            } else{
//                motionLayout.setTransition(R.id.search_button_click_back)
//                motionLayout.transitionToEnd()
//                searchButtonSwitch = false

                motionLayout.setTransition(R.id.sort_button_click_back)
                motionLayout.transitionToEnd()
                sortButtonSwitch = true
            }
        }


        searchButton.setOnClickListener {
            if (searchButtonSwitch){
                motionLayout.setTransition(R.id.search_button_click_forward)
                motionLayout.transitionToEnd()
                searchButtonSwitch = false


            } else{
//                motionLayout.setTransition(R.id.sort_button_click_back)
//                motionLayout.transitionToEnd()
//                sortButtonSwitch = false

                motionLayout.setTransition(R.id.search_button_click_back)
                motionLayout.transitionToEnd()
                searchButtonSwitch = true
            }
        }

        leftPanelImage.setOnClickListener {
            if (leftPanelSwitch){
                motionLayout.setTransition(R.id.left_panel_click_forward)
                motionLayout.transitionToEnd()
                leftPanelSwitch = false

                Log.d("MyLog","click left panel")
//                motionLayout.setTransition(R.id.search_button_click)
//                motionLayout.transitionToEnd()
//                searchButtonSwitch = true
            } else{
                motionLayout.setTransition(R.id.left_panel_click_back)
                motionLayout.transitionToEnd()
                leftPanelSwitch = true
            }
        }

        rightPanelImage.setOnClickListener {
            if (rightPanelSwitch){
                motionLayout.setTransition(R.id.right_panel_click_forward)
                motionLayout.transitionToEnd()
                rightPanelSwitch = false
                Log.d("MyLog","click right panel")

//                motionLayout.setTransition(R.id.search_button_click)
//                motionLayout.transitionToEnd()
//                searchButtonSwitch = true
            } else{
                motionLayout.setTransition(R.id.right_panel_click_back)
                motionLayout.transitionToEnd()
                rightPanelSwitch = true
            }
        }


//        val fabSort = binding.btCardsFragmentSortButton
//        val fabSortName = binding.btCardsFragmentSortName
//        val fabSortPhone = binding.fabCardsFragmentSortPhone
//        val fabSortOrganization = binding.fabCardsFragmentSortOrganisation
//        val fabSortLocation = binding.fabCardsFragmentSortLocation

//        val fabSearch = binding.fabSearchButton
//        val searchView = binding.tvCardsFragmentSearchField
//        val voiceSearchButton = binding.fabCardsFragmentSearchVoice
//        val searchButton = binding.fabCardsFragmentSearchSearch
//
//        val appbar: AppBarLayout = binding.appbarCardsFragment
//        val nameplate: FrameLayout = binding.flCardsFragmentTopInfo
//        val instruction: TextView = binding.tvCardsFragmentInstruction

//        val profileInfo: TextView = binding.layoutUserInformation.userInformationProfileInfo
//        val profSkills: TextView = binding.layoutUserInformation.userInformationProfSkills
//        val education: TextView = binding.layoutUserInformation.userInformationEducation
//        val workExperience: TextView = binding.layoutUserInformation.userInformationWorkExperience
//        val reference: TextView = binding.layoutUserInformation.userInformationReference

//        val sortButton = binding.btCardsFragmentSort


//        sortButton.setOnClickListener {
//            if (sortGroup.isVisible) sortGroup.visibility = View.GONE
//            else sortGroup.visibility = View.VISIBLE
//        }
//        val sortSearchRV = binding.rvCardsFragmentSortSearch
//        sortSearchRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,  false)
//        val sortSearchAdapter = SortSearchAdapter()
//        sortSearchRV.adapter = sortSearchAdapter


//        sortButton.setOnClickListener {
//            Log.d("MyLog", " click sort button")
//        }



        val launcher: ActivityResultLauncher<IntentSenderRequest> =
            registerForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult()
            ) { result -> }

        // **** Additional User Info
//        infoButton.setOnClickListener {
//            additionalInfoText.visibility = View.VISIBLE
//            collapseAppbar(appbar, nameplate)
//        }

//        setupFABs(fabSettings, fabEdit, fabDelete, fabShare, fabExit)
//        sortFABs(fabSort, fabSortName, fabSortPhone, fabSortOrganization, fabSortLocation)
//        searchFABs(fabSearch, searchView, voiceSearchButton)

//        closeButton.setOnClickListener {
//            additionalInfoText.visibility = View.GONE
//            expandAppbar(appbar, nameplate)
//        }

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

//        appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
//            if (Math.abs(verticalOffset - 250) > (appbar.height)) {
//                fabSettings.hide()
//                additionalInfoText.visibility = View.VISIBLE
//            }
//            if (Math.abs(verticalOffset) == 0) {
//                fabSettings.show()
//                additionalInfoText.visibility = View.GONE
//            }
//        }

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

        cardsFragmentViewModel.cards.observe(this.viewLifecycleOwner) {

            val listCards = it.sortedBy { it.surname }

            cardFragmentCardAdapter.updateData(listCards)




            // Condition not empty list cards TODO Пересмотреть что это за проверка была. Сейчас отключена
//            if (listCards.size > 0){
//                val currentPosition = cardsSnapHelper.getSnapPosition(cardsRV)
//           //     listCards[currentPosition].id     // java.lang.ArrayIndexOutOfBoundsException: length=3; index=-1
//                Log.d("MyLog", "position1 = $currentPosition, listSize1 = ${listCards.size} " )
//                cardsFragmentViewModel.getCard(listCards[currentPosition].id)
//            }

            setupDialog(cardFragmentCardAdapter, launcher)
//            Log.updateData(listCards)

            cardsPickerLayoutManager.setOnScrollStopListener(object :
                CardsPickerLayoutManager.CardScrollStopListener {
                override fun selectedView(view: View?) {
                    val cardId = view?.findViewById<TextView>(com.chaplianski.bcard.R.id.tv_card_fragment_id)
                    val userName = view?.findViewById<TextView>(com.chaplianski.bcard.R.id.tv_card_fragment_item_name)
                    val userAvatar =
                        view?.findViewById<TextView>(com.chaplianski.bcard.R.id.tv_card_fragment_uri)

//                    val currentPos = cardsSnapHelper.getSnapPosition(cardsRV)
//                    if (currentPos == listCards.size){
////                        appbar.isLiftOnScroll = false
//                        fabSettings.visibility = View.INVISIBLE
////                        instruction.visibility = View.VISIBLE
//                    setAppBarDragging(false, appbar)
//
//
//                    } else {
//                        fabSettings.visibility = View.VISIBLE
////                        instruction.visibility = View.INVISIBLE
//                        setAppBarDragging(true, appbar)
//                    }

//                    nestedScrollView.isNestedScrollingEnabled = currentPos != listCards.size


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

//            searchView.addTextChangedListener (object : TextWatcher{
//                override fun beforeTextChanged(
//                    s: CharSequence?,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {
//
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//                }
//
//                override fun afterTextChanged(editText: Editable?) {
//                    val searchFilter = listCards.filter { card ->
//                        card.name.uppercase().contains(editText.toString().uppercase()) ||
//                        card.surname.uppercase().contains(editText.toString().uppercase()) ||
//                        card.organization.uppercase().contains(editText.toString().uppercase()) ||
//                        card.town.uppercase().contains(editText.toString().uppercase()) ||
//                        card.workPhone.uppercase().contains(editText.toString().uppercase())
//                    } as MutableList<Card>
//                    cardFragmentCardAdapter.updateData(searchFilter)
//                    cardsPickerLayoutManager.scrollToPosition(0)
//                }
//
//            })




            cardsFragmentViewModel.currentCard.observe(this.viewLifecycleOwner) { card ->
//                val userName: TextView = view.findViewById(com.chaplianski.bcard.R.id.tv_cards_fragment_name)
//                val userAvatar: ImageView = view.findViewById(com.chaplianski.bcard.R.id.iv_cards_fragment_avatar)
//                currentCardId = card.id
                imageUri = card.photo
//                userName.text = "${card.name} ${card.surname}"
//                context?.let {
//                    Glide.with(it).load(card.photo)
//                        .override(150, 150)
//                        .centerCrop()
//                        .into(userAvatar)
//                }

//                profileInfo.text = card.profileInfo
//                profSkills.text = card.professionalSkills
//                education.text = card.education
//                workExperience.text = card.workExperience
//                reference.text = card.reference
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
                        val i = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
                        activity?.startActivity(Intent.createChooser(i, "Send email"))
                    }

                    override fun shortHomePhoneClick(homePhone: String) {
                        val i = Intent(Intent.ACTION_DIAL)
                        i.data = Uri.parse("tel:$homePhone")
                        activity?.startActivity(i)

//                        val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/$homePhone"))
//                        activity?.startActivity(i)
                    }
                }
        }

//        fabSortName.setOnClickListener {
//            cardsFragmentViewModel.sortCards(SORT_NAME)
//        }
//
//        fabSortPhone.setOnClickListener {
//            cardsFragmentViewModel.sortCards(SORT_PHONE)
//        }
//
//        fabSortOrganization.setOnClickListener {
//            cardsFragmentViewModel.sortCards(SORT_ORGANIZATION)
//        }
//
//        fabSortLocation.setOnClickListener {
//            cardsFragmentViewModel.sortCards(SORT_TOWN)
//        }

    }

    private fun searchFABs(fabSearch: FloatingActionButton, searchText: EditText, fabVoice: FloatingActionButton) {
        fabVoice.init(20f, -430f)

        fabSearch.setOnClickListener {
            if (fabVoice.isOrWillBeShown) {
                fabVoice.hide()
                searchText.isVisible = false
            } else {
                fabVoice.show()
                searchText.animateVisibility(true)
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

        fabEdit.init(-280f, -130f)
        fabShare.init(-120f, -250f)
        fabDelete.init(120f, -250f)
        fabExit.init(280f, -130f)

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

    private fun sortFABs(
        fabSort: FloatingActionButton,
        fabSortName: FloatingActionButton,
        fabSortPhone: FloatingActionButton,
        fabSortOrganisation: FloatingActionButton,
        fabSortLocation: FloatingActionButton
    ) {

        fabSortName.init(80f, -430f)
        fabSortOrganisation.init(260f, -430f)
        fabSortPhone.init(440f, -430f)
        fabSortLocation.init(620f, -430f)

        fabSort.setOnClickListener {
            if (fabSortName.isOrWillBeShown) {
                fabSortName.hide()
                fabSortPhone.hide()
                fabSortOrganisation.hide()
                fabSortLocation.hide()
            } else {
                fabSortName.show()
                fabSortPhone.show()
                fabSortOrganisation.show()
                fabSortLocation.show()
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

    fun setupDialog(
        cardFragmentCardAdapter: CardsFragmentCardAdapter,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        DeleteCardFragment.setupListener(parentFragmentManager, this) {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                    val contentResolver = context?.contentResolver

                    cardsFragmentViewModel.deleteCard(currentCardId)
//                    if (contentResolver != null) {
//                        cardsFragmentViewModel.deleteImage(imageUri, contentResolver, launcher)
//                    }
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
