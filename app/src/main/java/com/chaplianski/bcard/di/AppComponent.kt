package com.chaplianski.bcard.di

import android.content.Context
import com.chaplianski.bcard.core.dialogs.*
import com.chaplianski.bcard.core.ui.*
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ViewModelModule::class])
interface AppComponent {

    fun loginFragmentInject(loginFragment: LoginFragment)
    fun cardsFragmentInject(cardsFragment: CardsFragment)
    fun deleteCardFragmentInject(deleteCardDialog: DeleteCardDialog)
    fun personInfoDialogInject(personInformationDialog: PersonInformationDialog)
    fun additionalInfoDialogInject(additionalInformationDialog: AdditionalInformationDialog)
    fun cardSettingsDialogInject(cardSettingsDialog: CardSettingsDialog)
    fun saveCardDialogInject(saveCardDialog: SaveCardDialog)
    fun loadCardsDialogInject(loadCardListFromFileDialog: LoadCardListFromFileDialog)
    fun checkDoubleCardListDialogInject(checkDoubleCardListDialog: CheckDoubleCardListDialog)
    fun loadContactListDialogInject(loadContactListDialog: LoadContactListDialog)

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): AppComponent
    }
}