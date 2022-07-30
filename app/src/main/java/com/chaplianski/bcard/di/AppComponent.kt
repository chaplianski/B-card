package com.chaplianski.bcard.di

import android.content.Context
import com.chaplianski.bcard.presenter.ui.CardsFragment
import com.chaplianski.bcard.presenter.ui.LoginFragment
import com.chaplianski.bcard.presenter.ui.RegistrationFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun loginFragmentInject(loginFragment: LoginFragment)
    fun registrationFragmentInject(registrationFragment: RegistrationFragment)
    fun cardsFragmentInject(cardsFragment: CardsFragment)
//    fun categoryChooseFragmentInject(categoryChooseFragment: CategoryChooseFragment)
//    fun parentPurposeChooseFragmentInject(parentsPurposeChooseFragment: ParentsPurposeChooseFragment)
//    fun interestChooseFragmentInject(kidsInterestChooseFragment: KidsInterestChooseFragment)
//    fun kidGoalsFragmentInject(kidsGoalsFragment: KidsGoalsFragment)
//    fun tasksFragmentInject(tasksFragment: TasksFragment)
//    fun taskKidDetailFragmentInject(dayKidDetailTasksFragment: DayKidDetailTasksFragment)
//    fun newTaskFragmentInject(newTaskFragment: NewTaskFragment)
//    fun dayPersonalTasksDialogInject(dayPersonalTasksDialogFragment: DayPersonalTasksDialogFragment)
//    fun editTaskFragmentInject(editTaskFragment: EditTaskFragment)


    @Component.Builder
    interface Builder{
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): AppComponent
    }
}