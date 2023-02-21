package com.chaplianski.bcard.core.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.adapters.LanguageSettingsAdapter
import com.chaplianski.bcard.core.dialogs.EditCardDialog.Companion.ADDITIONAL_INFO_ENABLE
import com.chaplianski.bcard.core.dialogs.EditCardDialog.Companion.CARD_SETTING_INFO_ENABLE
import com.chaplianski.bcard.core.dialogs.EditCardDialog.Companion.PERSON_INFO_ENABLE
import com.chaplianski.bcard.core.dialogs.EditCardDialog.Companion.PERSON_INFO_STATUS
import com.chaplianski.bcard.core.helpers.DefaultLocaleHelper
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.databinding.DialogEditCardBinding
import com.chaplianski.bcard.databinding.DialogLanguageSettingsBinding


class LanguageSettingsDialog : DialogFragment() {
    private var _binding: DialogLanguageSettingsBinding? = null
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

        _binding = DialogLanguageSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.dialog_edit_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveButton = binding.btLanguageSettingsDialogAdd
        val cancelButton = binding.btLanguageSettingsDialogCancel
        val languageRV = binding.rvLanguageSettingsDialog
        val defaultLocaleHelper = DefaultLocaleHelper
        var currentLanguage = "en"
        val languageList = listOf<String>("en", "ru")
        val languageAdapter = LanguageSettingsAdapter(languageList)
        languageRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = languageAdapter
        }

        languageAdapter.languageCheckListener = object : LanguageSettingsAdapter.LanguageCheckListener{
            override fun onClickLanguage(language: String) {
                currentLanguage = language
            }

        }


       saveButton.setOnClickListener {
           parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(
                    CHECKED_OPTION to SETUP_LANGUAGE_STATUS, CHECKED_LANGUAGE to currentLanguage
                )
            )
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        val CHECKED_OPTION = "checked option"
        val SETUP_LANGUAGE_STATUS = "setup language status"
        val CHECKED_LANGUAGE = "checked language"

        val TAG = LanguageSettingsDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        //
        fun show(
            manager: FragmentManager
        ) {
            val dialogFragment = LanguageSettingsDialog()

//            val bundle = Bundle()
//            bundle.putLong(CURRENT_CARD_ID, currentCardId)
//            bundle.putBoolean(PERSON_INFO_ENABLE, isPersonInfoEnable)
//            bundle.putBoolean(ADDITIONAL_INFO_ENABLE, isAdditionalInfoEnable)
//            bundle.putBoolean(CARD_SETTING_INFO_ENABLE, isCardSettingsEnable)
//            dialogFragment.arguments = bundle

//            dialogFragment.arguments = bundleOf(
//                PERSON_INFO_ENABLE to isPersonInfoEnable,
//            )
//            Log.d("MyLog", "bundle = ${bundle}")
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            listener: (String, String) -> Unit
        ) {
            manager.setFragmentResultListener(
                REQUEST_KEY,
                lifecycleOwner,
                FragmentResultListener { key, result ->
                    val checkedLanguage = result.getString(CHECKED_LANGUAGE)
                    val status = result.getString(CHECKED_OPTION)
//                    val isPersonInfoEnable = result.getBoolean(PERSON_INFO_ENABLE, false)
//                    val isAdditionalInfoEnable = result.getBoolean(ADDITIONAL_INFO_ENABLE, false)
//                    val isCardSettingsEnable = result.getBoolean(CARD_SETTING_INFO_ENABLE, false)
                    if (status != null && checkedLanguage != null) {
                        listener.invoke(
                            status, checkedLanguage
                        ) //, isPersonInfoEnable, isAdditionalInfoEnable, isCardSettingsEnable)
                    }
                })
        }
    }
}