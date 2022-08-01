package com.chaplianski.bcard.presenter.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
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


class CardsFragment : Fragment() {

    @Inject
    lateinit var cardsFragmentViewModelFactory: CardsFragmentViewModelFactory
    val cardsFragmentViewModel: CardsFragmentViewModel by viewModels { cardsFragmentViewModelFactory }

    lateinit var photoPicker: PhotoPicker
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

        photoPicker =
            PhotoPicker(context!!, requireActivity().activityResultRegistry) { uri ->
                savePhotoToAnswer(uri)
            }

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

        // **** Edit card

        val editInfoText: ConstraintLayout = binding.layoutUserEditInformation.clCardImplementation
        val personInfo: ConstraintLayout = binding.layoutUserEditInformation.clCardImplPersonInfo
        val cardSettings: ConstraintLayout = binding.layoutUserEditInformation.clCardImplCardSettings
        val personInfoButton: TextView = binding.layoutUserEditInformation.tvCardImplPersonInfo
        val cardSettingsButton: TextView = binding.layoutUserEditInformation.tvCardImplCardSettings
        val saveButtonEdit: Button = binding.layoutUserEditInformation.btCardImplSave
        val cardColors: RecyclerView = binding.layoutUserEditInformation.rvCardImplCardColor
        val strokeColors: RecyclerView = binding.layoutUserEditInformation.rvCardImplStrokeColor
        val avatar: ImageView = binding.layoutUserEditInformation.ivCardImplAvatar

        editButton.setOnClickListener {
            editInfoText.visibility = View.VISIBLE
//            appbar.scrollTo(0, 1050)
            collapseAppbar(appbar, nameplate)
//            appbar.visibility = View.GONE
        }

        personInfoButton.setOnClickListener {
            personInfo.visibility =
            if (personInfo.isVisible) View.GONE
            else View.VISIBLE
            if (cardSettings.isVisible) cardSettings.visibility = View.GONE
        }

        cardSettingsButton.setOnClickListener {
            cardSettings.visibility =
            if (cardSettings.isVisible) View.GONE
            else View.VISIBLE
            if (personInfo.isVisible) personInfo.visibility = View.GONE
        }

        saveButtonEdit.setOnClickListener {
            editInfoText.visibility = View.GONE
            expandAppbar(appbar, nameplate)
        }

        avatar.setOnClickListener {
            addAvatarDialog()
        }

        val cardColorsAdapter = CardColorAdapter()
        cardColors.layoutManager = GridLayoutManager(context,5)
        cardColors.adapter = cardColorsAdapter

        val strokeColorsAdapter = StrokeColorAdapter()
        strokeColors.layoutManager = GridLayoutManager(context, 6)
        strokeColors.adapter = strokeColorsAdapter

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
        }
    }

    private fun savePhotoToAnswer(imageUri: Uri?) {
//        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
//        val questionId = sharedPref?.getString(Constants.CURRENT_QUESTION_ID, "")

        val contentResolver = context?.getContentResolver()

        if (imageUri != null) {
//            if (questionId != null) {
                if (contentResolver != null) {
                    cardsFragmentViewModel.insertPhoto(imageUri)
//                    answerViewModel.insertPhoto(imageUri, questionId, contentResolver)
                }
//            }
        }
    }

    private fun addAvatarDialog() {
        val photoDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Choose variant of adding photo")
        builder.setItems(photoDialogItems) { _, position ->
            when (position) {
                0 -> photoPicker.pickPhoto()
                1 -> photoPicker.takePhoto()
            }
        }
        builder.show()
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
