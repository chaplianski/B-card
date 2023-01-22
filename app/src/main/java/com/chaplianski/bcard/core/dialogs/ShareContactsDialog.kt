package com.chaplianski.bcard.core.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID
import com.chaplianski.bcard.databinding.DialogShareContactsBinding
import com.google.android.material.textview.MaterialTextView


class ShareContactsDialog : DialogFragment() {

    private var _binding: DialogShareContactsBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        val window: Window? = dialog!!.window
        window?.setGravity(Gravity.BOTTOM or Gravity.NO_GRAVITY)
        val params: WindowManager.LayoutParams? = window?.getAttributes()
        //      params?.x = 300
        params?.y = 30
        window?.setAttributes(params)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = DialogShareContactsBinding.inflate(layoutInflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.dialog_share_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveButton = binding.tvShareDialogSave
        val loadButton = binding.tvShareDialogLoad
        val cancelButton = binding.tvShareDialogCancel
        val currentCardId = arguments?.getLong(CURRENT_CARD_ID)

        saveButton.setOnClickListener {

            parentFragmentManager.setFragmentResult(
                REQUEST_KEY, bundleOf(CURRENT_CARD_ID to currentCardId, CHECKED_OPTION to SAVE_STATUS)
            )
            dismiss()
        }
//
        loadButton.setOnClickListener {

            parentFragmentManager.setFragmentResult(
                REQUEST_KEY, bundleOf(CURRENT_CARD_ID to currentCardId, CHECKED_OPTION to LOAD_STATUS)
            )
            dialog?.dismiss()
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

//        val KEY_RESPONSE = "key response"
        val CHECKED_OPTION = "checked option"
        val SAVE_STATUS = "save status"
        val LOAD_STATUS = "load status"



        val TAG = ShareContactsDialog::class.java.simpleName
        val REQUEST_KEY = "$TAG: default request key"
        //
        fun show(manager: FragmentManager, currentCardId: Long) {
            val dialogFragment = ShareContactsDialog()
//            val kids = ArrayList(kidList)
            dialogFragment.arguments = bundleOf(
//                CURRENT_TASK to task.mapToEditTask(),
//                "kids" to kids,
//                CURRENT_CHORE_ID to task.choreId,
//                TASK_STATUS to taskStatus
                  CURRENT_CARD_ID to currentCardId
            )
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(manager: FragmentManager, lifecycleOwner: LifecycleOwner, listener: (Long, String) -> Unit) {
            manager.setFragmentResultListener(REQUEST_KEY, lifecycleOwner, FragmentResultListener { key, result ->
                val cardId = result.getLong(CURRENT_CARD_ID)
                val status = result.getString(CHECKED_OPTION)
                if (status != null) {
                    listener.invoke(cardId, status)
                }

//                    .let { listener.invoke(it)
//                result.getString(REJECTED_TASK_STATUS).let {
//                    if (it != null) {
//                        listener.invoke(it)
//                    }
//                }


//                }
            })
        }
    }
}