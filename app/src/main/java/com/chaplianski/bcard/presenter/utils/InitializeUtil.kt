package com.chaplianski.bcard.presenter.utils

import androidx.room.Database
import com.google.firebase.auth.FirebaseAuth

lateinit var AUTH: FirebaseAuth


fun initFirebase(){
    AUTH = FirebaseAuth.getInstance()
}