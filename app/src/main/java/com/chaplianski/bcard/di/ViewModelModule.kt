package com.chaplianski.bcard.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaplianski.bcard.core.viewmodels.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoadCardsDialogViewModel::class)
    abstract fun bindLoadCardsDialogViewModel(mainViewModel: LoadCardsDialogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CardsFragmentViewModel::class)
    abstract fun bindCardsFragmentViewModel(mainViewModel: CardsFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AdditionalInfoDialogViewModel::class)
    abstract fun bindAdditionalInfoDialogViewModel(mainViewModel: AdditionalInfoDialogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CardSettingsDialogViewModel::class)
    abstract fun bindCardSettingsDialogViewModel(mainViewModel: CardSettingsDialogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginFragmentViewModel::class)
    abstract fun bindLoginFragmentViewModel(mainViewModel: LoginFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PersonInfoDialogViewModel::class)
    abstract fun bindPersonInfoDialogViewModel(mainViewModel: PersonInfoDialogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoadContactListDialogViewModel::class)
    abstract fun bindLoadContactListDialogViewModel(mainViewModel: LoadContactListDialogViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: InjectingViewModelFactory): ViewModelProvider.Factory
}