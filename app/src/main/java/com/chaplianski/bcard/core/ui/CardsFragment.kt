package com.chaplianski.bcard.core.ui

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.adapters.CardsFragmentCardAdapter
import com.chaplianski.bcard.core.dialogs.*
import com.chaplianski.bcard.core.helpers.CardsPickerLayoutManager
import com.chaplianski.bcard.core.utils.CURRENT_BACKGROUND
import com.chaplianski.bcard.core.utils.DEFAULT_BACKGROUND
import com.chaplianski.bcard.core.utils.LOAD_FROM_FILE
import com.chaplianski.bcard.core.utils.LOAD_FROM_GOOGLE_ACCOUNT
import com.chaplianski.bcard.core.viewmodels.CardsFragmentViewModel
import com.chaplianski.bcard.databinding.FragmentCardsBinding
import com.chaplianski.bcard.di.DaggerApp
import com.chaplianski.bcard.domain.model.AdditionalInfo
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.model.CardSettings
import com.chaplianski.bcard.domain.model.PersonInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    var currentPosition = 0

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

//        val additionalInfoText = binding.layoutUserInformation.clUserInfo
//        val closeButton = binding.layoutUserInformation.btUserInfoClose
        val backgroundLayout = binding.clCardsFragment

        val settingsButton = binding.btCardsFragmentSettings
        val editButton = binding.btCardsFragmentEdit
        val deleteButton = binding.fabCardsFragmentDelete
        val shareButton = binding.btCardsFragmentShare
        val exitButton = binding.fabCardsFragmentExit
        val addCardButton = binding.btCardsFragmentAddCard

        val motionLayout = binding.motionLayoutFragmentCards
        val sortButton = binding.btCardsFragmentSort
        val searchButton = binding.btCardsFragmentSearch
        val searchField = binding.tvCardsFragmentSearchField
        val leftPanelImage = binding.ivFragmentCardsLeftPanel
        val rightPanelImage = binding.ivFragmentCardsRightPanel
        val cardsRV: RecyclerView =
            view.findViewById(com.chaplianski.bcard.R.id.rv_cards_fragment_cards)

        val sortSurnameButton = binding.btCardsFragmentSortName
        val sortPhoneButton = binding.btCardsFragmentSortPhone
        val sortMailButton = binding.btCardsFragmentSortMail
        val sortOrganizationButton = binding.btCardsFragmentSortOrganisation
        val sortLocationButton = binding.btCardsFragmentSortLocation


        val avatarUserInformation = binding.layoutUserInformation.ivUserInformationProfileAvatar
        val nameUserInformation = binding.layoutUserInformation.tvUserInformationProfileName
        val specialityUserInfo = binding.layoutUserInformation.tvUserInformationProfileSpeciality
        val organizationUserInfo =
            binding.layoutUserInformation.tvUserInformationProfileOrganization
        val additionalProfileInfo = binding.layoutUserInformation.userInformationProfileInfo
        val professionalInfo = binding.layoutUserInformation.userInformationProfInfo
        val privateInfo = binding.layoutUserInformation.userInformationPrivate
        val reference = binding.layoutUserInformation.userInformationReference

        var listCards = emptyList<Card>()

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val currentBackground = sharedPref?.getString(CURRENT_BACKGROUND, DEFAULT_BACKGROUND)
        val backgroundResource = this.resources.getIdentifier(
            currentBackground,
            "drawable",
            activity?.packageName
        )
        backgroundLayout.background = resources.getDrawable(backgroundResource)


        motionLayout.setTransition(R.id.begin_position_start, R.id.begin_position_finish)
        motionLayout.transitionToEnd()

        addMotionPanelsAndButtons(
            leftPanelImage,
            motionLayout,
            rightPanelImage,
            sortButton,
            searchButton
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
        val cardsPickerLayoutManager =
            CardsPickerLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val cardFragmentCardAdapter = CardsFragmentCardAdapter(cardsRV) //(listCards, cardsRV)
        val cardsSnapHelper: SnapHelper = LinearSnapHelper()
        cardsRV.layoutManager = cardsPickerLayoutManager
        cardsRV.adapter = cardFragmentCardAdapter
        cardsRV.onFlingListener = null
        cardsSnapHelper.attachToRecyclerView(cardsRV)

        lifecycleScope.launchWhenCreated {
            delay(100)
            cardsRV.scrollToPosition(0)
        }

        cardsRV.addScrollListener { position: Int ->
            currentCard = listCards[position]
            currentCardId = currentCard.id
            cardsFragmentViewModel.getCard(currentCardId)
            Log.d("MyLog", "Current Position 3: $position, currentCardId = $currentCardId")
        }


        cardsFragmentViewModel.getCards()
        cardsFragmentViewModel.cards.observe(this.viewLifecycleOwner) {


            if (!it.isNullOrEmpty()) {
                listCards = it.sortedBy { it.surname }
                cardsFragmentViewModel.getCard(listCards.first().id)
            }
            cardFragmentCardAdapter.updateData(listCards)

            if (listCards.size > 0) {
                currentPosition = cardsSnapHelper.getSnapPosition(cardsRV)

                if (currentPosition == -1) currentPosition = 0
                currentCardId = listCards[currentPosition].id
                Log.d(
                    "MyLog", "Current Position 2: $currentPosition, currentCardId = $currentCardId"
                )
            }

            cardsPickerLayoutManager.setOnScrollStopListener(object :
                CardsPickerLayoutManager.CardScrollStopListener {
                override fun selectedView(view: View?) {
                    val cardId =
                        view?.findViewById<TextView>(com.chaplianski.bcard.R.id.tv_card_fragment_id)
                    val userName =
                        view?.findViewById<TextView>(com.chaplianski.bcard.R.id.tv_card_fragment_item_name)
                    val userAvatar =
                        view?.findViewById<TextView>(com.chaplianski.bcard.R.id.tv_card_fragment_uri)

                    if (userName?.text?.equals(null) != true && userAvatar?.text?.equals(null) != true && cardId?.text?.equals(
                            null
                        ) != true
                    ) {
                        if (cardId != null) {
                            cardsFragmentViewModel.getCard(
                                cardId.text.toString().toLong()
                            )
                        }
                    }
                    currentPosition = cardsSnapHelper.getSnapPosition(cardsRV)
                    Log.d("MyLog", "curd id wheel = ${cardId?.text}")
                }

            })

            searchField.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(editText: Editable?) {
                    val searchFilter = listCards.filter { card ->
                        card.name.uppercase()
                            .contains(editText.toString().uppercase()) || card.surname.uppercase()
                            .contains(
                                editText.toString().uppercase()
                            ) || card.organization.uppercase()
                            .contains(editText.toString().uppercase()) || card.town.uppercase()
                            .contains(editText.toString().uppercase()) || card.workPhone.uppercase()
                            .contains(editText.toString().uppercase())
                    } as MutableList<Card>
                    cardFragmentCardAdapter.updateData(searchFilter)
                    cardsPickerLayoutManager.scrollToPosition(0)
                }

            })
        }

        cardsFragmentViewModel.currentCard.observe(this.viewLifecycleOwner) { card ->

            currentCard = card
            currentCardId = card.id
            imageUri = card.photo

            //********** Fill current card additional information  **********************************

            Glide.with(this).load(card.photo).centerCrop().placeholder(R.drawable.ic_portrait)
                .into(avatarUserInformation)

            nameUserInformation.text = "${card.surname} ${card.name}"
//                nameUserInformation.setTextColor(Color.parseColor(card.cardTextColor))
            specialityUserInfo.text = card.speciality
            organizationUserInfo.text = card.organization
            additionalProfileInfo.text = card.additionalContactInfo
            professionalInfo.text = card.professionalInfo
//                education.text = card.education
            privateInfo.text = card.privateInfo
            reference.text = card.reference

            // ************* Fill dialog data ************************
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
            additionalInfoCheck =
                currentAdditionalInfo.address.isNotEmpty() || currentAdditionalInfo.workInfo.isNotEmpty() || currentAdditionalInfo.privateInfo.isNotEmpty() || currentAdditionalInfo.reference.isNotEmpty()
            personInfoCheck = true
            cardSettingsCheck = true
        }

        sortSurnameButton.setOnClickListener {
            val sortedList = listCards.sortedBy { it.surname }
            cardFragmentCardAdapter.updateData(sortedList)
        }

        sortPhoneButton.setOnClickListener {
            val sortedList = listCards.sortedBy { it.workPhone }
            cardFragmentCardAdapter.updateData(sortedList)
        }

        sortMailButton.setOnClickListener {
            val sortedList = listCards.sortedBy { it.email }
            cardFragmentCardAdapter.updateData(sortedList)
        }

        sortOrganizationButton.setOnClickListener {
            val sortedList = listCards.sortedBy { it.organization }
            cardFragmentCardAdapter.updateData(sortedList)
        }

        sortLocationButton.setOnClickListener {
            val sortedList = listCards.sortedBy { it.town }
            cardFragmentCardAdapter.updateData(sortedList)
        }

        cardFragmentCardAdapter.shortOnClickListener =
            object : CardsFragmentCardAdapter.ShortOnClickListener {

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

    private fun setupSettingsDialog() {
        SettingsDialog.setupListener(parentFragmentManager, this.viewLifecycleOwner){status ->
            Log.d("MyLog", "status settings = $status")
            when(status){
                SettingsDialog.BACKGROUND_STATUS -> {
                    Log.d("MyLog", "background")
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

    private fun setupBackgroundSettingsDialog(backgroundLayout: CoordinatorLayout){
        BackgroundSettingsDialog.setupListener(parentFragmentManager, this.viewLifecycleOwner) {status, background  ->
            when(status){
                BackgroundSettingsDialog.SETUP_STATUS -> {
                    val backgroundResource = this.resources.getIdentifier(
                        background,
                        "drawable",
                        activity?.packageName
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
//                            Log.d("MyLog", "add card")

                            cardsFragmentViewModel.addCard(newCard)
                            cardsFragmentViewModel.getCards()


                        }
                    } else lifecycleScope.launch(Dispatchers.IO) {
//                        Log.d("MyLog", "update card")
                        cardsFragmentViewModel.updateCard(newCard)
                        cardsFragmentViewModel.getCards()
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
                ShareContactsDialog.SAVE_OPTION -> {
                    showSaveCardsDialog(cardId)
                }
                ShareContactsDialog.LOAD_FROM_FILE_OPTION -> {
                    showLoadCardsDialog(LOAD_FROM_FILE)
                }
                ShareContactsDialog.LOAD_FROM_GOOGLE_ACCOUNT_OPTION -> {
                    showLoadCardsDialog(LOAD_FROM_GOOGLE_ACCOUNT)
                }
            }
            setupLoadCardsDialog()
        }
    }

    private fun setupLoadCardsDialog() {
        LoadCardDialog.setupListener(
            parentFragmentManager, this.viewLifecycleOwner
        ) { status, cardId ->
            when (status) {
                LoadCardDialog.ADD_STATUS -> {
                    lifecycleScope.launch {
                        delay(500)
                        cardsFragmentViewModel.getCards()
                    }
//                    Log.d("MyLog", "setup load dialog")
                }
                LoadCardDialog.CANCEL_STATUS -> {
//                    Log.d("MyLog", "setup load dialog 2")
                    showShareDialog(cardId)
                }
                LoadCardDialog.AFTER_CHECK_DOUBLE_STATUS -> {
                    lifecycleScope.launch {
                        delay(500)
                        cardsFragmentViewModel.getCards()
                    }
                }
            }
        }
    }

    private fun showLoadCardsDialog(destination: String) {
        LoadCardDialog.show(parentFragmentManager, currentCardId, destination)
    }

    private fun showSaveCardsDialog(cardId: Long) {
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
                deleteImage(currentCard.photo)
                Log.d("MyLog", "del currentCardId = $currentCardId, currentCard = $currentCard")
                cardsFragmentViewModel.deleteCard(currentCardId)
                delay(500)
                cardsFragmentViewModel.getCards()
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
                    Log.d("MyLog", "DONE")
                }
            }
        }
    }
}

fun RecyclerView?.getCurrentPosition(): Int {
    return (this?.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
}

