package com.chaplianski.bcard.presenter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.chaplianski.bcard.R
import contacts.core.Contacts


class ShareFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loadContact: Button = view.findViewById(R.id.bt_share_fragment_load)
        val saveContact: Button = view.findViewById(R.id.bt_share_fragment_save)
        val loadQR: Button = view.findViewById(R.id.bt_share_fragment_qr_load)
        val saveQR: Button = view.findViewById(R.id.bt_share_fragment_qr_save)


        val contacts = activity?.let { Contacts(it.applicationContext).query().find() }

        // https://www.linkedin.com/in/andi-funni-a02a0181/



    }

}