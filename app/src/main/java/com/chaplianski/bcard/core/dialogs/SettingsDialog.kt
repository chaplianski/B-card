package com.chaplianski.bcard.core.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import com.chaplianski.bcard.databinding.DialogSettingsBinding

class SettingsDialog : DialogFragment() {

    private var _binding: DialogSettingsBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val window: Window? = dialog!!.window
        window?.setGravity(Gravity.BOTTOM or Gravity.NO_GRAVITY)
        val params: WindowManager.LayoutParams? = window?.getAttributes()
        params?.y = 30
        window?.setAttributes(params)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backgroundButton: TextView = binding.tvSettingsDialogBackground
        val languageButton = binding.tvSettingsDialogLanguage
        val aboutButton = binding.tvSettingsDialogAbout
        val cancelButton = binding.tvSettingsDialogCancel

        backgroundButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(CHECKED_STATUS to BACKGROUND_STATUS))
                dismiss()
            }
        languageButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(CHECKED_STATUS to LANGUAGE_STATUS))
            dismiss()
        }
        aboutButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(CHECKED_STATUS to ABOUT_STATUS))
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        val TAG = SettingsDialog::class.java.simpleName
        val REQUEST_KEY = "request settings key"
        val CHECKED_STATUS = "checked settings status"
        val BACKGROUND_STATUS = "background status"
        val LANGUAGE_STATUS = "language status"
        val ABOUT_STATUS = "about status"

        fun show(manager: FragmentManager) {
            val dialogFragment = SettingsDialog()
            dialogFragment.show(manager, TAG)}

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            listener: (String) -> Unit
        ) {
            manager.setFragmentResultListener(
                REQUEST_KEY,
                lifecycleOwner,
                FragmentResultListener { key, result ->
                    val status = result.getString(CHECKED_STATUS)
                    if ( status != null) {
                        listener.invoke(status)
                    }
                })
        }
    }
}