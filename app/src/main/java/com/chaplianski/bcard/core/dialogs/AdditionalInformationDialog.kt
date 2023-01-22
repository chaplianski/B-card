package com.chaplianski.bcard.core.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.lifecycle.LifecycleOwner
import com.chaplianski.bcard.core.factories.AdditionalInfoDialogViewModelFactory
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.core.viewmodels.AdditionalInfoDialogViewModel
import com.chaplianski.bcard.databinding.DialogAdditionalInformationBinding
import com.chaplianski.bcard.di.DaggerApp
import com.chaplianski.bcard.domain.model.AdditionalInfo
import javax.inject.Inject


class AdditionalInformationDialog : DialogFragment() {

    private var _binding: DialogAdditionalInformationBinding? = null
    val binding get() = _binding!!

    @Inject
    lateinit var additionalInfoDialogViewModelFactory: AdditionalInfoDialogViewModelFactory
    val additionalInfoDialogViewModel: AdditionalInfoDialogViewModel by viewModels { additionalInfoDialogViewModelFactory }

    override fun onAttach(context: Context) {
        (context.applicationContext as DaggerApp)
            .getAppComponent()
            .additionalInfoDialogInject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val window: Window? = dialog!!.window
        window?.setGravity(Gravity.BOTTOM or Gravity.NO_GRAVITY)
        val params: WindowManager.LayoutParams? = window?.attributes
        params?.y = 230
        window?.attributes = params
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        _binding = DialogAdditionalInformationBinding.inflate(layoutInflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.dialog_person_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addressText = binding.etUserAdditionalInfoAddress
        val workInformationText = binding.etUserAdditionalInfoProfSkills
        val privateInfoText = binding.etUserAdditionalInfoPrivateInfo
        val referenceText = binding.editUserAdditionalInfoReference
        val saveButton = binding.btAdditionalInfoSave
        val cancelButton = binding.btAdditionalInfoCancel
        val currentCardId = arguments?.getLong(CURRENT_CARD_ID)


        if (currentCardId != null) {
            additionalInfoDialogViewModel.getCardData(currentCardId)
        }

        additionalInfoDialogViewModel.currentCard.observe(this.viewLifecycleOwner){ card ->
            addressText.setText(card.country)
            workInformationText.setText(card.professionalInfo)
            privateInfoText.setText(card.privateInfo)
            referenceText.setText(card.reference)
        }

        saveButton.setOnClickListener {
            val additionalInfo = AdditionalInfo(
                cardId = currentCardId ?: 0L,
                address = addressText.text.toString(),
                workInfo = workInformationText.text.toString(),
                privateInfo = privateInfoText.text.toString(),
                reference = referenceText.text.toString()
            )
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(ADDITIONAL_INFO to additionalInfo)
            )
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        dialog?.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
        super.onStart()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        val CHECKED_OPTION = "checked option"
        val ADDITIONAL_INFO = "additional information"

        val TAG = AdditionalInformationDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        //
        fun show(manager: FragmentManager, currentCardId: Long) {
            val dialogFragment = AdditionalInformationDialog()
            dialogFragment.arguments = bundleOf(
                CURRENT_CARD_ID to currentCardId
            )
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            listener: (Long, Parcelable) -> Unit
        ) {
            manager.setFragmentResultListener(
                REQUEST_KEY,
                lifecycleOwner,
                FragmentResultListener { key, result ->
                    val cardId = result.getLong(CURRENT_CARD_ID)
                    val additionalInfo = result.getParcelable<AdditionalInfo>(ADDITIONAL_INFO)
                    if (additionalInfo != null) {
                        listener.invoke(cardId, additionalInfo)
                    }
                })
        }
    }

}