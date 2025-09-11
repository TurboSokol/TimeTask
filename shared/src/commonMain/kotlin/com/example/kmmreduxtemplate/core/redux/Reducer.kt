package com.example.kmmreduxtemplate.core.redux

/***
 *If this code runs it was created by Evgenii Sokol.
 *If it doesn’t work, I don’t know who was created it.
 ***/

interface Reducer<G : GeneralState> {
    fun reduce(oldState: G, action: Action): G
}