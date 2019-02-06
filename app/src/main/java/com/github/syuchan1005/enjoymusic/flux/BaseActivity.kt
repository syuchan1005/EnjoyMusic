package com.github.syuchan1005.enjoymusic.flux

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


abstract class BaseActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    fun Disposable.disposeWhenDestroy() = disposable.add(this)

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }
}
