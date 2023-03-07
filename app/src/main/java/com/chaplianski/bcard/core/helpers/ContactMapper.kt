package com.chaplianski.bcard.core.helpers

import com.chaplianski.bcard.domain.model.Card
import com.chaplianski.bcard.core.model.ContactCsv

fun ContactCsv.mapContactCsvToCard(): Card {
    return Card(
        id = 0,
        userId = 0,
        name = givenName ?: "",
        surname = familyName ?: "",
        photo = "",
        workPhone = phone1Value ?: "",
        homePhone = phone2Value ?: "",
        email = email1Value ?: "",
        speciality = organization1JobDescription ?: "",
        organization = organization1Title ?: "",
        town = address1City ?: "",
        country = address1Country ?: "",
        additionalContactInfo = notes ?: "",
        professionalInfo = organization1Department ?: "",
        privateInfo = nickname ?: "",
    )
}

fun Card.mapCardToContactCsv(): ContactCsv {
    var firstName = ""
    var secondName = ""
    val nameArray = name.split(" ")
    if (nameArray.size > 1) {
        firstName = nameArray[0]
        secondName = nameArray[1].trimStart()
    } else firstName = name

        return ContactCsv(
            name = "$name $surname",
            givenName = firstName,
            additionalName = secondName,
            familyName = surname,
            nickname = privateInfo,
            occupation = speciality,
            notes = additionalContactInfo,
            photo = photo,
            email1Value = email,
            email2Value = email,
            phone1Value = workPhone,
            phone2Value = homePhone,
            address1City = town,
            address1Country = country,
            organization1Name = organization,
            organization1Title = organization,
            organization1Department = professionalInfo,
            organization1JobDescription = speciality,
        )
}




