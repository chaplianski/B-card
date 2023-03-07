package com.chaplianski.bcard.core.dialogs

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaplianski.bcard.core.adapters.LanguageSettingsAdapter
import com.chaplianski.bcard.databinding.DialogLanguageSettingsBinding
import com.chaplianski.bcard.core.model.LanguageItem

class LanguageSettingsDialog :
    BasisDialogFragment<DialogLanguageSettingsBinding>(DialogLanguageSettingsBinding::inflate){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveButton = binding.btLanguageSettingsDialogAdd
        val cancelButton = binding.btLanguageSettingsDialogCancel
        val languageRV = binding.rvLanguageSettingsDialog
        var currentLanguage = "en"
        val languageItemList = listOf(
            LanguageItem("en", "English"),
            LanguageItem("de", "Deutsch"),
            LanguageItem("fr", "Français"),
            LanguageItem("pl", "Polski"),
            LanguageItem("ru", "Русский"),
        )
        val languageAdapter = LanguageSettingsAdapter(languageItemList)
        languageRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = languageAdapter
        }

        languageAdapter.languageCheckListener =
            object : LanguageSettingsAdapter.LanguageCheckListener {
                override fun onClickLanguage(language: LanguageItem) {
                    currentLanguage = language.shortName
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

    companion object {

        const val CHECKED_OPTION = "checked option"
        const val SETUP_LANGUAGE_STATUS = "setup language status"
        const val CHECKED_LANGUAGE = "checked language"
        val TAG = LanguageSettingsDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        fun show(
            manager: FragmentManager
        ) {
            val dialogFragment = LanguageSettingsDialog()
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
                    if (status != null && checkedLanguage != null) {
                        listener.invoke(
                            status, checkedLanguage
                        )
                    }
                })
        }
    }
}