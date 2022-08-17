package com.chaplianski.bcard.data.storage.database

import com.chaplianski.bcard.data.storage.dao.CardDao
import com.chaplianski.bcard.data.storage.modeldto.UserDTO
import com.chaplianski.bcard.data.storage.storage.UserStorage
import javax.inject.Inject

class UserStorageImpl @Inject constructor(): UserStorage {

    @Inject
    lateinit var cardDao: CardDao

    override fun addUser(userDTO: UserDTO): Long {
        return cardDao.insertUser(userDTO)
    }

    override fun checkUser(userDTO: UserDTO): Long {
       return cardDao.checkCurrentUser(userDTO)
    }
}