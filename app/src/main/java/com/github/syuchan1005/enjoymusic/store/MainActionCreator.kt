package com.github.syuchan1005.enjoymusic.store

import com.github.syuchan1005.enjoymusic.view.MusicAdapter
import com.github.syuchan1005.enjoymusic.flux.Dispatcher
import javax.inject.Inject

class MainActionCreator @Inject constructor(private val dispatcher: Dispatcher) {
    fun addMusic(data: MusicAdapter.MusicData) {
        dispatcher.dispatch(MainAction.AddMusic(data))
    }

    fun removeMusic(position: Int) {
        dispatcher.dispatch(MainAction.RemoveMusic(position))
    }

    fun editMusic(position: Int, data: MusicAdapter.MusicData) {
        dispatcher.dispatch(MainAction.EditMusic(EditMusicData(position, data)))
    }
}
