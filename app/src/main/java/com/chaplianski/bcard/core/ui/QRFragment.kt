package com.chaplianski.bcard.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidmads.library.qrgenearator.QRGSaver
import androidx.fragment.app.Fragment
import com.chaplianski.bcard.R
import com.google.zxing.WriterException


class QRFragment : Fragment() {

    var qrImage: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_q_r, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        qrImage = view.findViewById(R.id.iv_qr_fragment_code)
        val qrButton: Button = view.findViewById(R.id.bt_qr_fragment_button)

        qrButton.setOnClickListener {
            generateQRCode("Insert text")
        }


    }

    fun generateQRCode(text: String) {
        val qrgEncoder = QRGEncoder(text, null, QRGContents.Type.TEXT, 600)
        val qrSaver = QRGSaver()
//
        try {
            val bitmap = qrgEncoder.bitmap
            qrImage?.setImageBitmap(bitmap)
        } catch (e: WriterException) {

        }
    }
}

