package com.chaplianski.bcard.data.repository

import com.chaplianski.bcard.data.storage.modeldto.CardDTO
import com.chaplianski.bcard.data.storage.modeldto.UserDTO
import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.model.User


fun Card.cardMapDomainToData(): CardDTO {
    return CardDTO(
        id = id,
        userId = userId,
        name = name,
        surname = surname,
        photo = photo,
        phone = phone,
        linkedin = linkedin,
        email = email,
        speciality = speciality,
        organization = organization,
        town = town,
        country = country,
        profilInfo = profileInfo,
        education = education,
        professionalSkills = professionalSkills,
        workExperience = workExperience,
        reference = reference,
        cardColor = cardColor,
        strokeColor = strokeColor,
        cornerRound = cornerRound,
        formPhoto = formPhoto
    )
}

fun CardDTO.cardMapDataToDomain(): Card {
    return Card(
        id = id,
        userId = userId,
        name = name,
        surname = surname,
        photo = photo,
        phone = phone,
        linkedin = linkedin,
        email = email,
        speciality = speciality,
        organization = organization,
        town = town,
        country = country,
        profileInfo = profilInfo,
        education = education,
        professionalSkills = professionalSkills,
        workExperience = workExperience,
        reference = reference,
        cardColor = cardColor,
        strokeColor = strokeColor,
        cornerRound = cornerRound,
        formPhoto = formPhoto
    )
}

fun User.userMapDomainToData(): UserDTO {
    return UserDTO(
        id = id,
        email = email,
        password = password
    )
}

fun UserDTO.userMapDataToDomain(): User {
    return User(
        id = id,
        email = email,
        password = password
    )
}