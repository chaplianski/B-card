package com.chaplianski.bcard.di

import android.content.Context
import com.chaplianski.bcard.core.dialogs.AdditionalInformationDialog
import com.chaplianski.bcard.core.dialogs.CardSettingsDialog
import com.chaplianski.bcard.core.dialogs.DeleteCardDialog
import com.chaplianski.bcard.core.dialogs.PersonInformationDialog
import com.chaplianski.bcard.core.ui.*
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun loginFragmentInject(loginFragment: LoginFragment)
    fun registrationFragmentInject(registrationFragment: RegistrationFragment)
    fun cardsFragmentInject(cardsFragment: CardsFragment)
    fun editCardFragmentInject(editCardFragment: EditCardFragment)
    fun deleteCardFragmentInject(deleteCardDialog: DeleteCardDialog)
    fun shareFragmentInject(shareFragment: ShareFragment)
    fun checkCardListFragmentInject(checkCardListSaveFragment: CheckCardListSaveFragment)
    fun personInfoDialogInject(personInformationDialog: PersonInformationDialog)
    fun additionalInfoDialogInject(additionalInformationDialog: AdditionalInformationDialog)
    fun cardSettingsDialogInject(cardSettingsDialog: CardSettingsDialog)

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): AppComponent
    }


}