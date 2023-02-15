package com.chaplianski.bcard.data.storage.dao

import android.util.Log
import androidx.room.*
import com.chaplianski.bcard.data.storage.modeldto.CardDTO
import com.chaplianski.bcard.data.storage.modeldto.UserDTO
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CardDao {

    @Query("SELECT * FROM cards ORDER BY surname ASC")
    abstract fun getAllCards(): List<CardDTO>

    @Query("SELECT * FROM cards ORDER BY surname ASC")
    abstract fun getSortedSurnameCardList(): List<CardDTO>
    @Query("SELECT * FROM cards ORDER BY workPhone ASC")
    abstract fun getSortedPhoneCardList(): List<CardDTO>

    @Query("SELECT * FROM cards ORDER BY email ASC")
    abstract fun getSortedEmailCardList(): List<CardDTO>
    @Query("SELECT * FROM cards ORDER BY organization ASC")
    abstract fun getSortedOrganizationCardList(): List<CardDTO>
    @Query("SELECT * FROM cards ORDER BY town ASC")
    abstract fun getSortedTownCardList(): List<CardDTO>


    fun getSortedCardList(fieldForSorting: String): List<CardDTO>{
        return when(fieldForSorting){
            SURNAME -> getSortedSurnameCardList()
            PHONE -> getSortedPhoneCardList()
            EMAIL -> getSortedEmailCardList()
            ORGANIZATION -> getSortedOrganizationCardList()
            LOCATION -> getSortedTownCardList()
            else -> getSortedSurnameCardList()
        }
    }
    @Query("SELECT * FROM users")
    abstract fun getAllUsers(): List<UserDTO>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertCard(cardDTO: CardDTO): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertUser(userDTO: UserDTO): Long

    @Query("DELETE FROM cards WHERE cards.id = :cardId")
    abstract fun deleteCard(cardId: Long): Int

//    fun updateCard(cardDTO: CardDTO){
//        val cardId = cardDTO.id
//        deleteCard(cardId)
//        cardDTO.id = cardId
//        insertCard(cardDTO)
//    }

    @Query("SELECT * FROM cards WHERE cards.id=:cardId")
    abstract fun getCard(cardId: Long): CardDTO

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract fun updateCard(cardDTO: CardDTO): Int

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

    companion object{
        val SURNAME = "surname"
        val PHONE = "phone"
        val EMAIL = "email"
        val ORGANIZATION = "organization"
        val LOCATION = "location"
    }

}