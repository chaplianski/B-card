package com.chaplianski.bcard.data.repository

import com.chaplianski.bcard.data.storage.modeldto.CardDTO
import com.chaplianski.bcard.domain.model.Card


fun Card.cardMapDomainToData(): CardDTO {
    return CardDTO(
        id = id,
        name = name,
        photo = photo,
        phone = phone,
        linkedin = linkedin,
        email = email,
        speciality = speciality,
        location = location,
        profilInfo = profilInfo,
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
        name = name,
        photo = photo,
        phone = phone,
        linkedin = linkedin,
        email = email,
        speciality = speciality,
        location = location,
        profilInfo = profilInfo,
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