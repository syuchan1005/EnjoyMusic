package com.github.syuchan1005.enjoymusic.store

import com.github.syuchan1005.enjoymusic.view.MusicAdapter
import com.github.syuchan1005.enjoymusic.flux.Action

sealed class MainAction<out T>(override val type: String) : Action<T> {
    class AddMusic(override val data: MusicAdapter.MusicData) : MainAction<MusicAdapter.MusicData>(TYPE) {
        companion object {
            const val TYPE = "MainAction.Music.Add";
        }
    }

    class RemoveMusic(override val data: Int) : MainAction<Int>(TYPE) {
        companion object {
            const val TYPE = "MainAction.Music.REMOVE"
        }
    }

    class EditMusic(override val data: EditMusicData) : MainAction<EditMusicData>(TYPE) {
        companion object {
            const val TYPE = "MainAction.Music.EDIT"
        }
    }
}

data class EditMusicData(val position: Int, val data: MusicAdapter.MusicData)
