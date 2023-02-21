package com.chaplianski.bcard.core.dialogs

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.dialogs.EditCardDialog.Companion.ADDITIONAL_INFO_ENABLE
import com.chaplianski.bcard.core.dialogs.EditCardDialog.Companion.CARD_SETTING_INFO_ENABLE
import com.chaplianski.bcard.core.dialogs.EditCardDialog.Companion.PERSON_INFO_ENABLE
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.databinding.DialogAboutSettingsBinding
import com.chaplianski.bcard.databinding.DialogEditCardBinding


class AboutSettingsDialog : DialogFragment() {

    private var _binding: DialogAboutSettingsBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val window: Window? = dialog!!.window
        window?.setGravity(Gravity.BOTTOM or Gravity.NO_GRAVITY)
        val params: WindowManager.LayoutParams? = window?.getAttributes()
        params?.y = 300
        window?.setAttributes(params)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        _binding = DialogAboutSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.dialog_edit_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val cancelButton = binding.btSaveCardDialogCancel
        val policyButton = binding.tvAboutDialogPolicy
        val termsButton = binding.tvAboutDialogTerms

//        cancelButton.setOnClickListener {
//            dismiss()
//        }

        policyButton.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://levelty.app/levelty_privacy_policy"))
            activity?.startActivity(i)
        }

        termsButton.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://levelty.app/levelty_website_and_mobile_app_terms"))
            activity?.startActivity(i)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        val CHECKED_OPTION = "checked option"



        val TAG = AboutSettingsDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        //
        fun show(
            manager: FragmentManager,
//            currentCardId: Long,
//            isPersonInfoEnable: Boolean,
//            isAdditionalInfoEnable: Boolean,
//            isCardSettingsEnable: Boolean
        ) {
            val dialogFragment = AboutSettingsDialog()

//            val bundle = Bundle()
//            bundle.putLong(CURRENT_CARD_ID, currentCardId)
//            bundle.putBoolean(PERSON_INFO_ENABLE, isPersonInfoEnable)
//            bundle.putBoolean(ADDITIONAL_INFO_ENABLE, isAdditionalInfoEnable)
//            bundle.putBoolean(CARD_SETTING_INFO_ENABLE, isCardSettingsEnable)
//            dialogFragment.arguments = bundle

//            dialogFragment.arguments = bundleOf(
//                PERSON_INFO_ENABLE to isPersonInfoEnable,
//                ADDITIONAL_INFO_ENABLE to isAdditionalInfoEnable,
//                CARD_SETTING_INFO_ENABLE to isCardSettingsEnable,
//                CURRENT_CARD_ID to currentCardId,
//            )
//            Log.d("MyLog", "bundle = ${bundle}")
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            listener: (Long, String) -> Unit
        ) {
            manager.setFragmentResultListener(
                REQUEST_KEY,
                lifecycleOwner,
                FragmentResultListener { key, result ->
                    val cardId = result.getLong(CURRENT_CARD_ID)
                    val status = result.getString(CHECKED_OPTION)
//                    val isPersonInfoEnable = result.getBoolean(PERSON_INFO_ENABLE, false)
//                    val isAdditionalInfoEnable = result.getBoolean(ADDITIONAL_INFO_ENABLE, false)
//                    val isCardSettingsEnable = result.getBoolean(CARD_SETTING_INFO_ENABLE, false)
                    if (status != null) {
                        listener.invoke(
                            cardId,
                            status
                        ) //, isPersonInfoEnable, isAdditionalInfoEnable, isCardSettingsEnable)
                    }
                })
        }
    }
}