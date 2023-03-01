package com.chaplianski.bcard.core.helpers

import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.domain.model.ContactCsv


fun ContactCsv.mapContactCsvToCard(): Card {

    return Card(
        id = 0,
        userId = 0,
        name = givenName ?: "",
        surname = familyName ?: "",
        photo = "",
        workPhone = phone1Type ?: "",
        homePhone = phone2Value ?: "",
        email = email1Value ?: "",
        speciality = organization1JobDescription ?: "",
        organization = organization1Title ?: "",
        town = address1City ?: "",
        country = address1Country ?: "",
        additionalContactInfo = notes ?: "",
        professionalInfo = organization1Department ?: "",
        privateInfo = notes ?: "",
    )
}

fun Card.mapCardToContactCsv(): ContactCsv {
    return ContactCsv(
        name = name,
        givenName = surname,
        familyName = surname,
        nickname = privateInfo,
        birthday = professionalInfo,
        location = town,
        occupation = speciality,
        notes = additionalContactInfo,
        photo = photo,
        email1Value = email,
        email2Value = email,
        phone1Value = workPhone,
        phone2Value = homePhone,
        address1Formatted = null,
        address1City = town,
        address1Country = country,
        organization1Name = organization,
        organization1Title = organization,
        organization1Department = null,
        organization1JobDescription = speciality,
        website1Value = additionalContactInfo,
    )
}




