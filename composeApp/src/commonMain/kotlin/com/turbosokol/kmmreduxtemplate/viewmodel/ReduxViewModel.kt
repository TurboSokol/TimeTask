package com.turbosokol.kmmreduxtemplate.viewmodel

import androidx.lifecycle.ViewModel
import com.turbosokol.kmmreduxtemplate.core.redux.Action
import com.turbosokol.kmmreduxtemplate.core.redux.Effect
import com.turbosokol.kmmreduxtemplate.core.redux.Store
import com.turbosokol.kmmreduxtemplate.core.redux.app.AppState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

class ReduxViewModel(
    val store: Store<AppState, Action, Effect>
): ViewModel() {
    fun execute(action: Action) {
        CoroutineScope(Dispatchers.Default + Job()).launch {
            store.dispatch(action)
        }
    }
}