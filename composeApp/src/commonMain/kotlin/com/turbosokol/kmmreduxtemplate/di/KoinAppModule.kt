package com.turbosokol.kmmreduxtemplate.di

import com.turbosokol.kmmreduxtemplate.viewmodel.ReduxViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/***
*If this code runs it was created by Evgenii Sokol.
*If it doesn't work, I don't know who was created it.
***/

val appModule = module {
    
    viewModel {
        ReduxViewModel(store = get()) 
    }

}
