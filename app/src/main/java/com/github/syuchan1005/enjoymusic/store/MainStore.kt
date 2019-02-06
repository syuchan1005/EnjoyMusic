package com.github.syuchan1005.enjoymusic.store

import com.github.syuchan1005.enjoymusic.view.MusicAdapter
import com.github.syuchan1005.enjoymusic.flux.Dispatcher
import com.github.syuchan1005.enjoymusic.flux.Store
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.BehaviorProcessor
import javax.inject.Inject

class MainStore @Inject constructor(private val dispatcher: Dispatcher) : Store() {
    private val _musics = BehaviorProcessor.create<List<MusicAdapter.MusicData>>()
    val musics: Flowable<List<MusicAdapter.MusicData>> = _musics

    init {
        dispatcher.on(MainAction.AddMusic.TYPE)
            .map { (it as MainAction.AddMusic).data }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (_musics.value != null) {
                    _musics.onNext(listOf<MusicAdapter.MusicData>(*_musics.value!!.toTypedArray(), it))
                } else {
                    _musics.onNext(listOf(it))
                }
            }

        dispatcher.on(MainAction.RemoveMusic.TYPE)
            .map { (it as MainAction.RemoveMusic).data }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { position ->
                if (_musics.value != null && position >= 0 && position < _musics.value!!.size) {
                    _musics.onNext(_musics.value!!.filterIndexed { index, _ -> index != position })
                }
            }

        dispatcher.on(MainAction.EditMusic.TYPE)
            .map { (it as MainAction.EditMusic).data }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { data ->
                val list = _musics.value!!.toMutableList()
                list.set(data.position, data.data)
                _musics.onNext(list)
            }
    }
}
