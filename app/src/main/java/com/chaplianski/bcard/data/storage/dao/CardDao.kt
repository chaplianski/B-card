package com.chaplianski.bcard.data.storage.dao

import androidx.room.*
import com.chaplianski.bcard.data.storage.modeldto.CardDTO
import com.chaplianski.bcard.data.storage.modeldto.UserDTO

@Dao
abstract class CardDao {

    @Query("SELECT * FROM cards ORDER BY surname ASC")
    abstract fun getAllCards(): List<CardDTO>

    @Query("SELECT * FROM cards WHERE userId = :userId ORDER BY surname ASC")
    abstract fun getSortedSurnameCardList(userId: Long): List<CardDTO>
    @Query("SELECT * FROM cards WHERE userId = :userId ORDER BY workPhone ASC")
    abstract fun getSortedPhoneCardList(userId: Long): List<CardDTO>

    @Query("SELECT * FROM cards WHERE userId = :userId ORDER BY email ASC")
    abstract fun getSortedEmailCardList(userId: Long): List<CardDTO>
    @Query("SELECT * FROM cards WHERE userId = :userId ORDER BY organization ASC")
    abstract fun getSortedOrganizationCardList(userId: Long): List<CardDTO>
    @Query("SELECT * FROM cards WHERE userId = :userId ORDER BY town ASC")
    abstract fun getSortedTownCardList(userId: Long): List<CardDTO>

//    @Query("SELECT * FROM cards WHERE name OR surname  LIKE '%' || :searchValue || '%'")
    @Query("SELECT * FROM cards WHERE name LIKE '%' || :searchValue || '%' OR surname  LIKE '%' || :searchValue || '%' AND userId = :userId ")
    abstract fun getAllCardsBySearchValue(searchValue: String, userId: Long): List<CardDTO>

    fun getSortedCardList(fieldForSorting: String, userId: Long): List<CardDTO>{
        return when(fieldForSorting){
            SURNAME -> getSortedSurnameCardList(userId)
            PHONE -> getSortedPhoneCardList(userId)
            EMAIL -> getSortedEmailCardList(userId)
            ORGANIZATION -> getSortedOrganizationCardList(userId)
            LOCATION -> getSortedTownCardList(userId)
            else -> getSortedSurnameCardList(userId)
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