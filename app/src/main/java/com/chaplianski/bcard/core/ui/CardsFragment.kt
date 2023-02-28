package com.chaplianski.bcard.core.ui

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.adapters.CardListAdapter
import com.chaplianski.bcard.core.dialogs.*
import com.chaplianski.bcard.core.helpers.*
import com.chaplianski.bcard.core.utils.*
import com.chaplianski.bcard.core.viewmodels.CardsFragmentViewModel
import com.chaplianski.bcard.databinding.FragmentCardsBinding
import com.chaplianski.bcard.di.DaggerApp
import com.chaplianski.bcard.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.grantland.widget.AutofitTextView
import java.io.File
import javax.inject.Inject


class CardsFragment : Fragment() {

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory
    val cardsFragmentViewModel: CardsFragmentViewModel by viewModels { vmFactory }

    var _binding: FragmentCardsBinding? = null
    val binding: FragmentCardsBinding get() = _binding!!

    var currentCardId = 0L
    var currentCard = Card()
    var imageUri = ""
    var currentPersonInfo = PersonInfo()
    var currentAdditionalInfo = AdditionalInfo()
    var currentCardSettings = CardSettings()
    var personInfoCheck = false
    var additionalInfoCheck = false
    var cardSettingsCheck = false
    var currentPosition = -1
    var currentAddItemId = 0L
    var currentCardEditId = 0L
    var currentCardDeleteId = -2L
    var currentCardDeletePosition = 0
    var scrolledPosition = 0
    var listCards = mutableListOf<Card>()
    override fun onAttach(context: Context) {
        (context.applicationContext as DaggerApp).getAppComponent().cardsFragmentInject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backgroundLayout = binding.clCardsFragment
        val adContainerView = binding.layoutCardFragmentAd
        val settingsButton = binding.btCardsFragmentSettings
        val editButton = binding.btCardsFragmentEdit
        val deleteButton = binding.btCardsFragmentDelete
        val shareButton = binding.btCardsFragmentShare
        val exitButton = binding.fabCardsFragmentExit
        val addCardButton = binding.btCardsFragmentAddCard

        val motionLayout = binding.motionLayoutFragmentCards
        val sortButton = binding.btCardsFragmentSort
        val searchButton = binding.btCardsFragmentSearch
        val searchField = binding.tvCardsFragmentSearchField
        val resetSearchField = binding.btCardsFragmentSearchReset
        val leftPanelImage = binding.ivFragmentCardsLeftPanel
        val rightPanelImage = binding.ivFragmentCardsRightPanel
        val cardsRV = binding.rvCardsFragmentCards

        val sortSurnameButton = binding.btCardsFragmentSortName
        val sortPhoneButton = binding.btCardsFragmentSortPhone
        val sortMailButton = binding.btCardsFragmentSortMail
        val sortOrganizationButton = binding.btCardsFragmentSortOrganisation
        val sortLocationButton = binding.btCardsFragmentSortLocation

        val userInfoLayout = binding.layoutUserInformation.clUserInfo

        val avatarUserInformation = binding.layoutUserInformation.ivUserInformationProfileAvatar
        val nameUserInformation = binding.layoutUserInformation.tvUserInformationProfileName
        val specialityUserInfo = binding.layoutUserInformation.tvUserInformationProfileSpeciality
        val organizationUserInfo =
            binding.layoutUserInformation.tvUserInformationProfileOrganization
        val addProfileInfoTitle = binding.layoutUserInformation.userInformationProfileInfoTitle
        val profInfoTitle = binding.layoutUserInformation.userInformationProfInfoTitle
        val privateInfoTitle = binding.layoutUserInformation.userInformationPrivateTitle
        val referenceTitle = binding.layoutUserInformation.userInformationReferenceTitle
        val additionalProfileInfo = binding.layoutUserInformation.userInformationProfileInfo
        val professionalInfo = binding.layoutUserInformation.userInformationProfInfo
        val privateInfo = binding.layoutUserInformation.userInformationPrivate
        val reference = binding.layoutUserInformation.userInformationReference
        val absentInfoText = binding.layoutUserInformation.tvUserInformationAbsentInfo

        val adsPicker = AdsPicker()
        adsPicker.setAdNative(requireContext(), adContainerView)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val currentBackground = sharedPref?.getString(CURRENT_BACKGROUND, DEFAULT_BACKGROUND)
        val backgroundResource = this.resources.getIdentifier(
            currentBackground, RESOURCE_TYPE_DRAWABLE, activity?.packageName
        )
        backgroundLayout.background = context?.let {
            ContextCompat.getDrawable(
                it, backgroundResource
            )
        }

        motionLayout.setTransition(R.id.begin_position_start, R.id.begin_position_finish)
        motionLayout.transitionToEnd()

        addMotionPanelsAndButtons(
            leftPanelImage, motionLayout, rightPanelImage, sortButton, searchButton
        )

        addCardButton.setOnClickListener {
            currentPersonInfo = PersonInfo()
            currentAdditionalInfo = AdditionalInfo()
            currentCardSettings = CardSettings()
            personInfoCheck = false
            additionalInfoCheck = false
            cardSettingsCheck = false
            showEditDialog(0L)
        }

        settingsButton.setOnClickListener {
            showSettingsDialog()
        }
        setupSettingsDialog()
        setupBackgroundSettingsDialog(backgroundLayout)
        setupLanguageDialog()

        editButton.setOnClickListener {
            if (currentCardId != 0L) showEditDialog(currentCardId)
        }
        setupEditDialog()

        // **** Share card
        shareButton.setOnClickListener {
            showShareDialog(currentCardId)
        }
        setupShareDialog()

        deleteButton.setOnClickListener {
            if (currentCardId != -1L) showDeleteDialog(currentCardId)
        }

        setupDeleteDialog()

        exitButton.setOnClickListener {
            activity?.finishAffinity()
        }


        // ******  Cards wheel  ********
        val cardFragmentCardAdapter = CardListAdapter(requireContext())
        setupRecyclerView(cardsRV, cardFragmentCardAdapter, userInfoLayout, adContainerView)

//        cardsRV.addGlobalLayoutListener(){position ->
////            currentPosition = position
//            Log.d("MyLog", "listener1: ${position}, currentCardId = $currentCardId")
//        }
//        cardsRV.addScrollListener(){position ->
//            Log.d("MyLog", "listener2: ${position}, currectposition = ${position+(position/ AD_POSITION)}")
//        }

        cardsFragmentViewModel.getCardListState
            .flowWithLifecycle(lifecycle)
            .onEach {
                when (it) {
                    is GetCardListState.Loading -> {}
                    is GetCardListState.GetCardList -> {
                        listCards.clear()
                        listCards = if (!it.cardList.isEmpty()) {
                            it.cardList.map { it.copy() } as MutableList<Card>
                        } else it.cardList as MutableList<Card>
                        cardFragmentCardAdapter.differ.submitList(listCards)

                        if (it.cardList.isNotEmpty()) {
                            if (currentPosition == -1) {
                                cardsFragmentViewModel.getCard(listCards[0].id)
                                currentPosition = 0
                            }
                        }
                        if (currentAddItemId > 0) {
                            it.cardList.forEachIndexed { index, card ->
                                if (card.id == currentAddItemId) {
                                    scrolledPosition = (index + (index / AD_POSITION))
                                }
                            }
                            currentAddItemId = 0
                        }
                        if (currentCardEditId > 0) {
                            it.cardList.forEachIndexed { index, card ->
                                if (card.id == currentCardEditId) {
                                    scrolledPosition = (index + (index / AD_POSITION))
                                }
                            }
                            currentCardEditId = 0
                        }
                        if (currentCardDeleteId != -2L) {
                            Log.d("MyLog", "Current del it = $currentCardDeleteId")
                            if (currentCardDeleteId == -1L) cardsRV.scrollToPosition(0)
                            val cardListSize = listCards.size.toLong()
                            if (currentCardDeleteId == cardListSize) {
                                scrolledPosition =
                                    ((cardListSize + cardListSize % AD_POSITION) - 1).toInt()
                            }
                            if (currentCardDeleteId == 0L && listCards.size > 0) scrolledPosition =
                                0
                            if (currentPosition > 0) {
                                currentPosition--
                                scrolledPosition = currentPosition
                            }
                            currentCardDeleteId = -2
                        }
                        cardsRV.scrollToPosition(scrolledPosition)
                        addSearchFieldListener(searchField, resetSearchField)
                    }
                    is GetCardListState.AddCard -> {
                        currentAddItemId = it.cardId
                        lifecycleScope.launch(Dispatchers.IO) {
                            cardsFragmentViewModel.getCards(SURNAME)
                        }
                    }
                    is GetCardListState.UpdateCard -> {
                        lifecycleScope.launch(Dispatchers.IO) {
                            if (it.result == 1) {

                                cardsFragmentViewModel.getCards(SURNAME)
                            }
                        }
                    }
                    is GetCardListState.DeleteCard -> {
                        lifecycleScope.launch(Dispatchers.IO) {
                            if (it.result == 1) cardsFragmentViewModel.getCards(SURNAME)
                        }
                    }
                    is GetCardListState.Failure -> {}
                }
            }
            .launchIn(lifecycleScope)

        lifecycleScope.launch(Dispatchers.IO) {
            cardsFragmentViewModel.getCards(SURNAME)
        }

        cardsFragmentViewModel.currentCard.observe(this.viewLifecycleOwner) { card ->

//            Log.d("MyLog", "card cardId = ${card.id}")
            currentCard = card
            currentCardId = card.id
            imageUri = card.photo

            fillCurrentCardProfileInformation(
                card,
                avatarUserInformation,
                nameUserInformation,
                absentInfoText,
                specialityUserInfo,
                organizationUserInfo,
                addProfileInfoTitle,
                additionalProfileInfo,
                profInfoTitle,
                professionalInfo,
                privateInfoTitle,
                privateInfo,
                referenceTitle,
                reference
            )

            saveToCurrentCardEditDialogInformation(card)

            additionalInfoCheck =
                currentAdditionalInfo.address.isNotEmpty() || currentAdditionalInfo.workInfo.isNotEmpty() || currentAdditionalInfo.privateInfo.isNotEmpty() || currentAdditionalInfo.reference.isNotEmpty()
            personInfoCheck = true
            cardSettingsCheck = true
        }

        sortSurnameButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                cardsFragmentViewModel.getCards(SURNAME)
            }
        }

        sortPhoneButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                cardsFragmentViewModel.getCards(PHONE)
                currentPosition = -1
            }
        }

        sortMailButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                cardsFragmentViewModel.getCards(EMAIL)
                currentPosition = -1
            }
        }

        sortOrganizationButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                cardsFragmentViewModel.getCards(ORGANIZATION)
                currentPosition = -1
            }
        }

        sortLocationButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                cardsFragmentViewModel.getCards(LOCATION)
                currentPosition = -1
            }
        }

        cardFragmentCardAdapter.shortOnClickListener =
            object : CardListAdapter.ShortOnClickListener {

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
                }
            }
    }

//    private fun loadAD(context: Context, adContainerView: FrameLayout) {
//        val binding = LayoutAdToCardBinding.inflate(LayoutInflater.from(context))
//        val adLoader = AdLoader.Builder(context, AD_UNIT_ID)
//            .forNativeAd { nativeAd ->
//                Log.d("MyLog", "is success")
//                val adView = populateNativeAdView(nativeAd, binding)
//                adContainerView.addView(adView)
//            }
//            .withAdListener(object : AdListener() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    Log.d("MyLog", "is failure")
//                }
//            })
//            .withNativeAdOptions(
//                NativeAdOptions.Builder()
//
//                .build())
//
//            .build()
//        adLoader.loadAds(AdRequest.Builder().build(), 5)
//    }

    private fun addSearchFieldListener(
        searchField: EditText,
        resetSearchButton: ImageButton
    ) {

        searchField.doAfterTextChanged {
            lifecycleScope.launch(Dispatchers.IO) {
                cardsFragmentViewModel.getCardListBySearchValue(
                    searchField.text.toString().uppercase()
                )
                currentPosition = 0
            }
        }
        resetSearchButton.setOnClickListener {
            searchField.text.clear()
            lifecycleScope.launch(Dispatchers.IO) {
                cardsFragmentViewModel.getCards(SURNAME)
            }
        }
    }

    private fun saveToCurrentCardEditDialogInformation(card: Card) {
        currentPersonInfo = PersonInfo(
            id = card.id,
            userId = card.userId,
            name = card.name,
            surname = card.surname,
            photo = card.photo,
            workPhone = card.workPhone,
            homePhone = card.homePhone,
            email = card.email,
            speciality = card.speciality,
            organization = card.organization,
            town = card.town,
            country = card.country
        )
        currentAdditionalInfo = AdditionalInfo(
            cardId = card.id,
            address = card.additionalContactInfo,
            workInfo = card.professionalInfo,
            privateInfo = card.privateInfo,
            reference = card.reference
        )
        currentCardSettings = CardSettings(
            cardId = card.id,
            cardTexture = card.cardTexture,
            cardCorner = card.isCardCorner,
            cardTextColor = card.cardTextColor,
            cardAvatarForm = card.cardFormPhoto
        )
    }

    private fun fillCurrentCardProfileInformation(
        card: Card,
        avatarUserInformation: ImageView,
        nameUserInformation: AutofitTextView,
        absentInfoText: TextView,
        specialityUserInfo: AutofitTextView,
        organizationUserInfo: AutofitTextView,
        addProfileInfoTitle: TextView,
        additionalProfileInfo: TextView,
        profInfoTitle: TextView,
        professionalInfo: TextView,
        privateInfoTitle: TextView,
        privateInfo: TextView,
        referenceTitle: TextView,
        reference: TextView
    ) {
        Glide.with(this).load(card.photo).centerCrop().placeholder(R.drawable.ic_portrait)
            .into(avatarUserInformation)
        nameUserInformation.text =
            UiText.StringResource(R.string.two_value_without_comma, card.surname, card.name)
                .asString(requireContext())// "${card.surname} ${card.name}"
        absentInfoText.isVisible = false
        specialityUserInfo.text = card.speciality
        organizationUserInfo.text = card.organization
        if (card.additionalContactInfo.isEmpty()) {
            addProfileInfoTitle.visibility = View.GONE
            additionalProfileInfo.visibility = View.GONE
        } else {
            addProfileInfoTitle.visibility = View.VISIBLE
            additionalProfileInfo.visibility = View.VISIBLE
            additionalProfileInfo.text = card.additionalContactInfo
        }
        if (card.professionalInfo.isEmpty()) {
            profInfoTitle.visibility = View.GONE
            professionalInfo.visibility = View.GONE
        } else {
            profInfoTitle.visibility = View.VISIBLE
            professionalInfo.visibility = View.VISIBLE
            professionalInfo.text = card.professionalInfo
        }
        if (card.privateInfo.isEmpty()) {
            privateInfoTitle.visibility = View.GONE
            privateInfo.visibility = View.GONE
        } else {
            privateInfoTitle.visibility = View.VISIBLE
            privateInfo.visibility = View.VISIBLE
            privateInfo.text = card.privateInfo
        }
        if (card.reference.isEmpty()) {
            referenceTitle.visibility = View.GONE
            referenceTitle.visibility = View.GONE
        } else {
            referenceTitle.visibility = View.VISIBLE
            referenceTitle.visibility = View.VISIBLE
            reference.text = card.reference
        }
        if (card.additionalContactInfo.isEmpty() && card.professionalInfo.isEmpty() && card.privateInfo.isEmpty() && card.reference.isEmpty()) {
            absentInfoText.isVisible = true
        }
    }

    private fun addMotionPanelsAndButtons(
        leftPanelImage: ImageView,
        motionLayout: MotionLayout,
        rightPanelImage: ImageView,
        sortButton: ImageButton,
        searchButton: ImageButton
    ) {
        var sortButtonSwitch = true
        var searchButtonSwitch = true
        var leftPanelSwitch = true
        var rightPanelSwitch = true

        leftPanelImage.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                when {
                    (!rightPanelSwitch && leftPanelSwitch && !sortButtonSwitch) -> {
                        motionLayout.setTransition(R.id.sort_button_click_back)
                        motionLayout.transitionToEnd()
                        sortButtonSwitch = true
                        delay(300)
                        motionLayout.setTransition(R.id.right_panel_click_back)
                        motionLayout.transitionToEnd()
                        rightPanelSwitch = true
                        delay(300)
                        motionLayout.setTransition(R.id.left_panel_click_forward)
                        motionLayout.transitionToEnd()
                        leftPanelSwitch = false
                    }
                    (!rightPanelSwitch && leftPanelSwitch && !searchButtonSwitch) -> {
                        motionLayout.setTransition(R.id.search_button_click_back)
                        motionLayout.transitionToEnd()
                        sortButtonSwitch = true
                        delay(300)
                        motionLayout.setTransition(R.id.right_panel_click_back)
                        motionLayout.transitionToEnd()
                        rightPanelSwitch = true
                        delay(300)
                        motionLayout.setTransition(R.id.left_panel_click_forward)
                        motionLayout.transitionToEnd()
                        leftPanelSwitch = false
                    }
                    (leftPanelSwitch && rightPanelSwitch) -> {
                        motionLayout.setTransition(R.id.left_panel_click_forward)
                        motionLayout.transitionToEnd()
                        leftPanelSwitch = false
                    }
                    (!leftPanelSwitch && rightPanelSwitch) -> {
                        motionLayout.setTransition(R.id.left_panel_click_back)
                        motionLayout.transitionToEnd()
                        leftPanelSwitch = true
                    }
                    (!rightPanelSwitch && leftPanelSwitch) -> {
                        motionLayout.setTransition(R.id.right_panel_click_back)
                        motionLayout.transitionToEnd()
                        rightPanelSwitch = true
                        delay(300)
                        motionLayout.setTransition(R.id.left_panel_click_forward)
                        motionLayout.transitionToEnd()
                        leftPanelSwitch = false
                    }

                }
            }
        }

        rightPanelImage.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                when {
                    (!rightPanelSwitch && !sortButtonSwitch && leftPanelSwitch) -> {
                        motionLayout.setTransition(R.id.sort_button_click_back)
                        motionLayout.transitionToEnd()
                        sortButtonSwitch = true
                        delay(300)
                        motionLayout.setTransition(R.id.right_panel_click_back)
                        motionLayout.transitionToEnd()
                        rightPanelSwitch = true
                    }
                    (!rightPanelSwitch && !searchButtonSwitch && leftPanelSwitch) -> {
                        motionLayout.setTransition(R.id.search_button_click_back)
                        motionLayout.transitionToEnd()
                        searchButtonSwitch = true
                        delay(300)
                        motionLayout.setTransition(R.id.right_panel_click_back)
                        motionLayout.transitionToEnd()
                        rightPanelSwitch = true
                    }
                    (rightPanelSwitch && leftPanelSwitch) -> {
                        motionLayout.setTransition(R.id.right_panel_click_forward)
                        motionLayout.transitionToEnd()
                        rightPanelSwitch = false
                    }
                    (!rightPanelSwitch && leftPanelSwitch) -> {
                        motionLayout.setTransition(R.id.right_panel_click_back)
                        motionLayout.transitionToEnd()
                        rightPanelSwitch = true
                    }
                    (!leftPanelSwitch && rightPanelSwitch) -> {
                        motionLayout.setTransition(R.id.left_panel_click_back)
                        motionLayout.transitionToEnd()
                        leftPanelSwitch = true
                        delay(300)
                        motionLayout.setTransition(R.id.right_panel_click_forward)
                        motionLayout.transitionToEnd()
                        rightPanelSwitch = false
                    }

                }
            }
        }

        sortButton.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                when {
                    (searchButtonSwitch && sortButtonSwitch) -> {
                        motionLayout.setTransition(R.id.sort_button_click_forward)
                        motionLayout.transitionToEnd()
                        sortButtonSwitch = false
                    }
                    (searchButtonSwitch && !sortButtonSwitch) -> {
                        motionLayout.setTransition(R.id.sort_button_click_back)
                        motionLayout.transitionToEnd()
                        sortButtonSwitch = true
                    }
                    (!searchButtonSwitch && sortButtonSwitch) -> {
                        motionLayout.setTransition(R.id.search_button_click_back)
                        motionLayout.transitionToEnd()
                        searchButtonSwitch = true
                        delay(300)
                        motionLayout.setTransition(R.id.sort_button_click_forward)
                        motionLayout.transitionToEnd()
                        sortButtonSwitch = false
                    }
                }
            }
        }

        searchButton.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                when {
                    (searchButtonSwitch && sortButtonSwitch) -> {
                        motionLayout.setTransition(R.id.search_button_click_forward)
                        motionLayout.transitionToEnd()
                        searchButtonSwitch = false
                    }
                    (!searchButtonSwitch && sortButtonSwitch) -> {
                        motionLayout.setTransition(R.id.search_button_click_back)
                        motionLayout.transitionToEnd()
                        searchButtonSwitch = true
                    }
                    (searchButtonSwitch && !sortButtonSwitch) -> {
                        motionLayout.setTransition(R.id.sort_button_click_back)
                        motionLayout.transitionToEnd()
                        sortButtonSwitch = true
                        delay(300)
                        motionLayout.setTransition(R.id.search_button_click_forward)
                        motionLayout.transitionToEnd()
                        searchButtonSwitch = false
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(
        cardsRV: RecyclerView,
        cardAdapter: CardListAdapter,
        userInfoLayout: ConstraintLayout,
        adContainerView: FrameLayout
    ) {

        cardAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                cardsRV.scrollToPosition(scrolledPosition)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                cardsRV.smoothScrollToPosition(itemCount)
            }
        })
        val cardsPickerLayoutManager =
            CardsPickerLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val cardsSnapHelper: SnapHelper = LinearSnapHelper()
        cardsRV.layoutManager = cardsPickerLayoutManager
        cardsRV.adapter = cardAdapter
        cardsRV.onFlingListener = null
        cardsSnapHelper.attachToRecyclerView(cardsRV)

        cardsPickerLayoutManager.setOnScrollStopListener(object :
            CardsPickerLayoutManager.CardScrollStopListener {
            override fun selectedView(view: View?) {
                val cardId =
                    view?.findViewById<TextView>(R.id.tv_card_fragment_id)
                val userName =
                    view?.findViewById<TextView>(R.id.tv_card_fragment_item_name)
                val userAvatar =
                    view?.findViewById<TextView>(R.id.tv_card_fragment_uri)
//                Log.d("MyLog", "curd id wheel = ${cardId?.text}")

                if (cardId != null) {
//                    Log.d("MyLog", "get card wheel ")
                    userInfoLayout.isVisible = true
                    adContainerView.isVisible = false
                    listCards.forEachIndexed { index, card ->
                        if (card.id == cardId.text.toString().toLong()) currentPosition =
                            index + (index / AD_POSITION)
                    }
//                    currentCardId = cardId.text.toString().toLong()
                    cardsFragmentViewModel.getCard(cardId.text.toString().toLong())
                } else {
                    userInfoLayout.isVisible = false
                    adContainerView.isVisible = true
                }
            }
        })
    }

    private fun setupSettingsDialog() {
        SettingsDialog.setupListener(parentFragmentManager, this.viewLifecycleOwner) { status ->
//            Log.d("MyLog", "status settings = $status")
            when (status) {
                SettingsDialog.BACKGROUND_STATUS -> {
//                    Log.d("MyLog", "background")
                    showBackgroundSettingsDialog()
                }
                SettingsDialog.LANGUAGE_STATUS -> {
                    showLanguageSettingsDialog()
                }
                SettingsDialog.ABOUT_STATUS -> {
                    showAboutSettingsDialog()
                }
            }
        }
    }


    private fun setupBackgroundSettingsDialog(backgroundLayout: CoordinatorLayout) {
        BackgroundSettingsDialog.setupListener(
            parentFragmentManager, this.viewLifecycleOwner
        ) { status, background ->
            when (status) {
                BackgroundSettingsDialog.SETUP_STATUS -> {
                    val backgroundResource = this.resources.getIdentifier(
                        background, RESOURCE_TYPE_DRAWABLE, activity?.packageName
                    )
                    backgroundLayout.background = resources.getDrawable(backgroundResource)
                }
                BackgroundSettingsDialog.CANCEL_STATUS -> {

                }
            }
        }
    }

    private fun showAboutSettingsDialog() {
        AboutSettingsDialog.show(parentFragmentManager)
    }

    private fun showLanguageSettingsDialog() {
        LanguageSettingsDialog.show(parentFragmentManager)
    }

    private fun showBackgroundSettingsDialog() {
        BackgroundSettingsDialog.show(parentFragmentManager)
    }

    private fun showSettingsDialog() {
        SettingsDialog.show(parentFragmentManager)
    }

    private fun setupLanguageDialog() {
        LanguageSettingsDialog.setupListener(
            parentFragmentManager,
            this.viewLifecycleOwner
        ) { status, language ->
            if (status == LanguageSettingsDialog.SETUP_LANGUAGE_STATUS) {
//                Log.d("MyLog", "setup dialog lang = $language")
                val defaultLocaleHelper = DefaultLocaleHelper
                defaultLocaleHelper.getInstance(requireContext()).setCurrentLocale(language)
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }
        }
    }

    fun RecyclerView.addScrollListener(onScroll: (position: Int) -> Unit) {
        var lastPosition = 0
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (layoutManager is LinearLayoutManager) {
                    val currentVisibleItemPosition =
                        (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if (lastPosition != currentVisibleItemPosition && currentVisibleItemPosition != RecyclerView.NO_POSITION) {
                        onScroll.invoke(currentVisibleItemPosition)
                        lastPosition = currentVisibleItemPosition
                    }
                }
            }
        })
    }

    fun RecyclerView.addGlobalLayoutListener(onScroll: (position: Int) -> Unit) {
        var lastPosition = 0
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (layoutManager is LinearLayoutManager) {
                    val currentVisibleItemPosition =
                        (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if (lastPosition != currentVisibleItemPosition && currentVisibleItemPosition != RecyclerView.NO_POSITION) {
                        onScroll.invoke(currentVisibleItemPosition)
                        lastPosition = currentVisibleItemPosition
                    }
                }
            }

        })
    }


    private fun setupEditDialog() {
        EditCardDialog.setupListener(parentFragmentManager, this) { cardId, status ->
            when (status) {
                EditCardDialog.PERSON_INFO_STATUS -> {
                    showPersonInfoDialog(cardId)
                }
                EditCardDialog.ADD_INFO_STATUS -> {
                    showAdditionalInfoDialog(cardId)
                }
                EditCardDialog.SETTINGS_CARD_STATUS -> {
                    showSettingsCardDialog(cardId)
                }
                EditCardDialog.SAVE_STATUS -> {
                    val newCard = Card(
                        id = cardId,
                        userId = currentPersonInfo.userId,
                        name = currentPersonInfo.name,
                        surname = currentPersonInfo.surname,
                        photo = currentPersonInfo.photo,
                        workPhone = currentPersonInfo.workPhone,
                        homePhone = currentPersonInfo.homePhone,
                        email = currentPersonInfo.email,
                        speciality = currentPersonInfo.speciality,
                        organization = currentPersonInfo.organization,
                        town = currentPersonInfo.town,
                        country = currentPersonInfo.country,
                        additionalContactInfo = currentAdditionalInfo.address,
                        professionalInfo = currentAdditionalInfo.workInfo,
                        privateInfo = currentAdditionalInfo.privateInfo,
                        reference = currentAdditionalInfo.reference,
                        cardTexture = currentCardSettings.cardTexture,
                        cardTextColor = currentCardSettings.cardTextColor,
                        isCardCorner = currentCardSettings.cardCorner,
                        cardFormPhoto = currentCardSettings.cardAvatarForm
                    )
                    if (cardId == 0L) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            cardsFragmentViewModel.addCard(newCard)
                        }
                    } else lifecycleScope.launch(Dispatchers.IO) {
                        cardsFragmentViewModel.updateCard(newCard)
                        currentCardEditId = cardId

                    }
                }
            }
            setupPersonInfoDialog()
            setupAdditionalDialog()
            setupCardSettingsDialog()
        }
    }

    private fun setupCardSettingsDialog() {
        CardSettingsDialog.setupListener(
            parentFragmentManager, this
        ) { cardId, status, cardSettingsInfo ->
//            Log.d("MyLog", "cardInfo = $cardSettingsInfo")
            if (status == CardSettingsDialog.SAVE_STATUS) {
                currentCardSettings = cardSettingsInfo as CardSettings
                cardSettingsCheck = true
                showEditDialog(cardId)
            } else showEditDialog(cardId)
        }
    }

    private fun setupAdditionalDialog() {
        AdditionalInformationDialog.setupListener(
            parentFragmentManager, this
        ) { cardId, status, additionalInfo ->
//            Log.d("MyLog", "addInfo = $additionalInfo")
            if (status == AdditionalInformationDialog.SAVE_STATUS) {
                currentAdditionalInfo = additionalInfo as AdditionalInfo
                additionalInfoCheck =
                    currentAdditionalInfo.address.isNotEmpty() || currentAdditionalInfo.workInfo.isNotEmpty() || currentAdditionalInfo.privateInfo.isNotEmpty() || currentAdditionalInfo.reference.isNotEmpty()
                showEditDialog(currentCardId)
            } else showEditDialog(currentCardId)
        }
    }

    private fun setupPersonInfoDialog() {
        PersonInformationDialog.setupListener(
            parentFragmentManager, this
        ) { cardId, status, personInfo ->
            if (status == PersonInformationDialog.SAVE_STATUS) {
                currentPersonInfo = personInfo as PersonInfo
                personInfoCheck = true
//                Log.d("MyLog", "setup person info, cardId = $cardId")
                showEditDialog(cardId)
            } else showEditDialog(cardId)
        }
    }

    private fun showSettingsCardDialog(cardId: Long) {
        CardSettingsDialog.show(parentFragmentManager, cardId)
    }

    private fun showAdditionalInfoDialog(cardId: Long) {
        AdditionalInformationDialog.show(parentFragmentManager, cardId)
    }

    private fun showPersonInfoDialog(cardId: Long) {
        PersonInformationDialog.show(parentFragmentManager, cardId)
    }

    private fun showEditDialog(currentCardId: Long) {
        EditCardDialog.show(
            parentFragmentManager,
            currentCardId,
            personInfoCheck,
            additionalInfoCheck,
            cardSettingsCheck
        )
    }

    private fun setupShareDialog() {
        ShareContactsDialog.setupListener(parentFragmentManager, this) { cardId, status ->
            when (status) {
                ShareContactsDialog.SAVE_VCF_OPTION -> {
                    showSaveVcfCardsDialog(cardId)
                }
                ShareContactsDialog.SAVE_CSV_OPTION -> {
                    showSaveCsvCardsDialog(cardId)
                }
                ShareContactsDialog.LOAD_FROM_VCF_FILE_OPTION -> {
                    val filePicker = ContactPicker(
                        requireContext(),
                        requireActivity().activityResultRegistry
                    ) { uri ->
                        showLoadVcfCardsDialog(uri)
                    }
                    filePicker.checkReadFilesPermission()
                }
                ShareContactsDialog.LOAD_FROM_CSV_FILE_OPTION -> {
                    val filePicker = ContactPicker(
                        requireContext(),
                        requireActivity().activityResultRegistry
                    ) { uri ->
                        showLoadCsvCardsDialog(uri)
                    }
                    filePicker.checkReadFilesPermission()
                }
                ShareContactsDialog.LOAD_FROM_GOOGLE_ACCOUNT_OPTION -> {
                    val accountContactPicker = AccountContactsPicker(
                        requireContext(),
                        requireActivity().activityResultRegistry
                    ) { permission ->
                        if (permission) showLoadContactsDialog()
                    }
                    accountContactPicker.checkPermission()
                }
            }
            setupLoadCardsDialog()
            setupLoadContactsDialog()
        }
    }

    private fun setupLoadContactsDialog() {
        LoadContactListDialog.setupListener(
            parentFragmentManager,
            this.viewLifecycleOwner
        ) { status ->
            when (status) {
                LoadContactListDialog.ADD_STATUS -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        cardsFragmentViewModel.getCards(SURNAME)
                    }
                }
                LoadContactListDialog.CANCEL_STATUS -> {
                }
            }
        }
    }

    private fun setupLoadCardsDialog() {
        LoadCardListFromVCFDialog.setupListener(
            parentFragmentManager, this.viewLifecycleOwner
        ) { status, cardId ->
            when (status) {
                LoadCardListFromVCFDialog.ADD_STATUS -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        cardsFragmentViewModel.getCards(SURNAME)
                    }
                }
                LoadCardListFromVCFDialog.CANCEL_STATUS -> {
                    showShareDialog(cardId)
                }
                LoadCardListFromVCFDialog.AFTER_CHECK_DOUBLE_STATUS -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        cardsFragmentViewModel.getCards(SURNAME)
                    }
                }
            }
        }
    }

    private fun showLoadContactsDialog() {
        LoadContactListDialog.show(parentFragmentManager)
    }

    private fun showLoadVcfCardsDialog(uri: Uri) {
        LoadCardListFromVCFDialog.show(parentFragmentManager, uri)
    }

    private fun showLoadCsvCardsDialog(uri: Uri) {

    }

    private fun showSaveCsvCardsDialog(cardId: Long) {

    }

    private fun showSaveVcfCardsDialog(cardId: Long) {
        SaveCardDialog.show(parentFragmentManager, cardId)
    }

    private fun showShareDialog(currentCardId: Long) {
        ShareContactsDialog.show(parentFragmentManager, currentCardId)
    }


    fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
        val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        return layoutManager.getPosition(snapView)
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    private fun showDeleteDialog(cardId: Long) {
        DeleteCardDialog.show(parentFragmentManager, cardId)
    }

    private fun setupDeleteDialog(
    ) {
        DeleteCardDialog.setupListener(parentFragmentManager, this.viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.IO) {
                if (currentCardId != null) {
                    deleteImage(currentCard.photo)
//                Log.d("MyLog", "del current position = ${currentPosition}, cardId = $currentCardId")
                    cardsFragmentViewModel.deleteCard(currentCardId)
                    currentCardDeleteId = currentCardId
                }


            }
        }
    }

    private fun deleteImage(path: String) {
        val fDelete = File(path)
        if (fDelete.exists()) {
            if (fDelete.delete()) {
                MediaScannerConnection.scanFile(
                    requireContext(),
                    arrayOf(Environment.getExternalStorageDirectory().toString()),
                    null
                ) { path, uri ->
//                    Log.d("MyLog", "DONE")
                }
            }
        }
    }

    companion object {
        val SURNAME = "surname"
        val PHONE = "phone"
        val EMAIL = "email"
        val ORGANIZATION = "organization"
        val LOCATION = "location"
    }
}


