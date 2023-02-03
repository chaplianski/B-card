package com.chaplianski.bcard.core.dialogs

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.utils.CURRENT_BACKGROUND
import com.chaplianski.bcard.core.utils.CURRENT_USER_ID
import com.chaplianski.bcard.core.utils.DEFAULT_BACKGROUND
import com.chaplianski.bcard.databinding.DialogBackgroundSettingsBinding


class BackgroundSettingsDialog : DialogFragment() {

    private var _binding: DialogBackgroundSettingsBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val window: Window? = dialog!!.window
        window?.setGravity(Gravity.BOTTOM or Gravity.NO_GRAVITY)
        val params: WindowManager.LayoutParams? = window?.attributes
        params?.y = 30
        window?.attributes = params
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        _binding = DialogBackgroundSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backgroundImage = binding.ivBackgroundSettingsDialog
        val setupButton = binding.btBackgroundSettingsDialogSetup
        val cancelButton = binding.btBackgroundSettingsDialogCancel
        val backgroundRadioGroup = binding.radiogroupBackgroundSettingsDialog
        val background1RadioButton = binding.rbBackgroundSettingsDialog1
        val background2RadioButton = binding.rbBackgroundSettingsDialog2
        val background3RadioButton = binding.rbBackgroundSettingsDialog3
        val background4RadioButton = binding.rbBackgroundSettingsDialog4
        val background5RadioButton = binding.rbBackgroundSettingsDialog5
        val background6RadioButton = binding.rbBackgroundSettingsDialog6

        var checkedBackgroundVariant = DEFAULT_BACKGROUND
        val backgroundResource = this.resources.getIdentifier(
            checkedBackgroundVariant,
            "drawable",
            activity?.packageName
        )
        backgroundImage.setImageResource(backgroundResource)

        if (checkedBackgroundVariant != "") {
            when (checkedBackgroundVariant) {
                "background_03" -> background1RadioButton.isChecked = true
                "background_12" -> background2RadioButton.isChecked = true
                "background_18" -> background3RadioButton.isChecked = true
                "background_05" -> background4RadioButton.isChecked = true
                "background_09" -> background5RadioButton.isChecked = true
                "background_32" -> background6RadioButton.isChecked = true
            }
        }

        backgroundRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            val currentBackground = when(i){
                R.id.rb_background_settings_dialog_1 -> R.drawable.background_03
                R.id.rb_background_settings_dialog_2 -> R.drawable.background_12
                R.id.rb_background_settings_dialog_3 -> R.drawable.background_18
                R.id.rb_background_settings_dialog_4 -> R.drawable.background_05
                R.id.rb_background_settings_dialog_5 -> R.drawable.background_09
                R.id.rb_background_settings_dialog_6 -> R.drawable.background_32
                else -> R.drawable.background_32
            }

            checkedBackgroundVariant =
                context?.resources?.getResourceEntryName(currentBackground).toString()
            Log.d("MyLog", "variant = $checkedBackgroundVariant")
            val textureResource = this.resources.getIdentifier(
                checkedBackgroundVariant,
                "drawable",
                activity?.packageName
            )
            Log.d("MyLog", "variant 2 = $textureResource")
            backgroundImage.setImageResource(textureResource)
        }

        setupButton.setOnClickListener {
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            sharedPref?.edit()?.putString(CURRENT_BACKGROUND, checkedBackgroundVariant)?.apply()
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(CHECKED_OPTION to SETUP_STATUS, BACKGROUND_VARIANT to checkedBackgroundVariant)
            )
            dismiss()
        }
        cancelButton.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(CHECKED_OPTION to CANCEL_STATUS, BACKGROUND_VARIANT to checkedBackgroundVariant)
            )
            dismiss()
        }
    }

    override fun onStart() {
        dialog?.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        super.onStart()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        val CHECKED_OPTION = "checked background option"
        val SETUP_STATUS = "setup status"
        val CANCEL_STATUS = "cancel status"
        val BACKGROUND_VARIANT = "background variant"


        val TAG = BackgroundSettingsDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"

        //
        fun show(manager: FragmentManager) {
            val dialogFragment = BackgroundSettingsDialog()
//            dialogFragment.arguments = bundleOf(
//                CURRENT_CARD_ID to currentCardId
//            )
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
                    val background = result.getString(BACKGROUND_VARIANT)
                    val status = result.getString(CHECKED_OPTION)
//                    val additionalInfo = result.getParcelable<AdditionalInfo>(ADDITIONAL_INFO)
                    if (status != null && background != null) {
                        listener.invoke(status, background)
                    }
                })
        }
    }
}