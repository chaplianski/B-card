package com.chaplianski.bcard.di

import android.content.Context
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
    fun deleteCardFragmentInject(deleteCardFragment: DeleteCardFragment)
    fun shareFragmentInject(shareFragment: ShareFragment)
    fun checkCardListFragmentInject(checkCardListSaveFragment: CheckCardListSaveFragment)

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): AppComponent
    }


}