package com.chaplianski.bcard.core.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import com.chaplianski.bcard.R
import com.chaplianski.bcard.core.utils.CURRENT_CARD_ID


class DeleteCardDialog : DialogFragment() {

//    @Inject
//    lateinit var deleteCardFragmentViewModelFactory: DeleteCardFragmentViewModelFactory
//    val deleteCardFragmentViewModel: DeleteCardFragmentViewModel by viewModels { deleteCardFragmentViewModelFactory }
//
////    @Inject
////    lateinit var cardsFragmentViewModelFactory: CardsFragmentViewModelFactory
////    val cardsFragmentViewModel: CardsFragmentViewModel by viewModels { cardsFragmentViewModelFactory }
//
//    override fun onAttach(context: Context) {
//        DaggerAppComponent.builder()
//            .context(context)
//            .build()
//            .deleteCardFragmentInject(this)
//        super.onAttach(context)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val window: Window? = dialog!!.window
        window?.setGravity(Gravity.BOTTOM or Gravity.NO_GRAVITY)
        val params: WindowManager.LayoutParams? = window?.getAttributes()
        //      params?.x = 300
        params?.y = 30
        window?.setAttributes(params)
//        window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.fragment_delete_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val deleteButton: TextView = view.findViewById(R.id.tv_delete_card_dialog_delete)
        val cancelButton: TextView = view.findViewById(R.id.tv_delete_card_dialog_cancel)
        val currentCardId = arguments?.getLong(CURRENT_CARD_ID)

        val cardId = arguments?.getLong(CURRENT_CARD_ID)

        deleteButton.setOnClickListener {
            if (cardId != null) {
//                deleteCardFragmentViewModel.deleteCard(cardId)
//                cardsFragmentViewModel.deleteCard(cardId)
                parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(CURRENT_CARD_ID to currentCardId))
                dismiss()
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        val TAG = DeleteCardDialog::class.java.simpleName
        val KEY_RESPONSE = "key response"
        val REQUEST_KEY = "request key"

        fun show(manager: FragmentManager, cardId: Long) {
            val dialogFragment = DeleteCardDialog()
            dialogFragment.arguments = bundleOf(
                CURRENT_CARD_ID to cardId)
            dialogFragment.show(manager, TAG)}


        fun setupListener(manager: FragmentManager, lifecycleOwner: LifecycleOwner, listener: (Long) -> Unit) {
            manager.setFragmentResultListener(REQUEST_KEY, lifecycleOwner, FragmentResultListener { key, result ->
                result.getLong(CURRENT_CARD_ID).let { listener.invoke(it) }

            })
        }
    }
}