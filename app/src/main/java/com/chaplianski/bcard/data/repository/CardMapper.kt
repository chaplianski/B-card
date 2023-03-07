package com.chaplianski.bcard.data.repository

import com.chaplianski.bcard.data.storage.modeldto.CardDTO
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.model.User
import com.chaplianski.bcard.data.storage.modeldto.UserDTO

fun Card.cardMapDomainToData(): CardDTO {
    return CardDTO(
        id = id,
        userId = userId,
        name = name,
        surname = surname,
        photo = photo,
        workPhone = workPhone,
        homePhone = homePhone,
        email = email,
        speciality = speciality,
        organization = organization,
        town = town,
        country = country,
        additionalContactInfo = additionalContactInfo,
        professionalInfo = professionalInfo,
        privateInfo = privateInfo,
        reference = reference,
        cardTexture = cardTexture,
        cardTextColor = cardTextColor,
        isCardCorner = isCardCorner,
        cardFormPhoto = cardFormPhoto
    )
}

fun CardDTO.cardMapDataToDomain(): Card {
    return Card(
        id = id,
        userId = userId,
        name = name,
        surname = surname,
        photo = photo,
        workPhone = workPhone,
        homePhone = homePhone,
        email = email,
        speciality = speciality,
        organization = organization,
        town = town,
        country = country,
        additionalContactInfo = additionalContactInfo,
        professionalInfo = professionalInfo,
        privateInfo = privateInfo,
        reference = reference,
        cardTexture = cardTexture,
        cardTextColor = cardTextColor,
        isCardCorner = isCardCorner,
        cardFormPhoto = cardFormPhoto
    )
}

fun User.userMapDomainToData(): UserDTO {
    return UserDTO(
        id = id,
        login = login,
        password = password,
        secretQuestion = secretQuestion,
        secretAnswer = secretAnswer
    )
}

fun UserDTO.userMapDataToDomain(): User {
    return User(
        id = id,
        login = login,
        password = password,
        secretQuestion = secretQuestion,
        secretAnswer = secretAnswer
    )
}