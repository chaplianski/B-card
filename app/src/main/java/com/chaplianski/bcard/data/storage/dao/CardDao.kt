package com.chaplianski.bcard.data.storage.dao

import android.util.Log
import androidx.room.*
import com.chaplianski.bcard.data.storage.modeldto.CardDTO
import com.chaplianski.bcard.data.storage.modeldto.UserDTO

@Dao
abstract class CardDao {

    @Query("SELECT * FROM cards")
    abstract fun getAllCards(): List<CardDTO>

    @Query("SELECT * FROM users")
    abstract fun getAllUsers(): List<UserDTO>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertCard(cardDTO: CardDTO)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertUser(userDTO: UserDTO): Long

    @Query("DELETE FROM cards WHERE cards.id = :cardId")
    abstract fun deleteCard(cardId: Long)

//    fun updateCard(cardDTO: CardDTO){
//        val cardId = cardDTO.id
//        deleteCard(cardId)
//        cardDTO.id = cardId
//        insertCard(cardDTO)
//    }

    @Query("SELECT * FROM cards WHERE cards.id=:cardId")
    abstract fun getCard(cardId: Long): CardDTO

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract fun updateCard(cardDTO: CardDTO)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract fun updateUser(userDTO: UserDTO)

    @Query("SELECT * FROM users WHERE users.login=:login")
    abstract fun getSecretQuestion(login: String): UserDTO?

    @Query("SELECT EXISTS (SELECT * FROM users WHERE users.login=:login AND users.password=:password)")
    abstract fun checkUser(login: String, password: String): Boolean

    @Query("SELECT * FROM users WHERE users.login=:login AND users.password=:password")
    abstract fun getUserId(login: String, password: String): UserDTO

    fun checkCurrentUser(userDTO: UserDTO): Long {
        val login = userDTO.login
        val password = userDTO.password
        Log.d("MyLog", "userDto = $userDTO")
        Log.d("MyLog", "users = ${getAllUsers()}")
        val isExist = checkUser(login, password)
        return if (isExist) getUserId(login, password).id
        else -1
    }

}