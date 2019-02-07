package com.github.syuchan1005.enjoymusic.view

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.syuchan1005.enjoymusic.R
import com.github.syuchan1005.enjoymusic.di.Injectable
import com.github.syuchan1005.enjoymusic.flux.BaseActivity
import com.github.syuchan1005.enjoymusic.store.MainActionCreator
import com.github.syuchan1005.enjoymusic.store.MainStore
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

class MainActivity : BaseActivity(), Injectable {
    @Inject
    lateinit var actionCreator: MainActionCreator
    @Inject
    lateinit var store: MainStore

    private lateinit var viewAdapter: MusicAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<FloatingActionButton>(R.id.add_button).setOnClickListener {
            val intent = Intent(application, AddActivity::class.java)
            intent.putExtra("TYPE", "ADD")
            startActivity(intent)
        }

        viewManager = LinearLayoutManager(this)
        viewAdapter = MusicAdapter(actionCreator, this)

        findViewById<RecyclerView>(R.id.music_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            addItemDecoration(DividerItemDecoration(this.context, LinearLayoutManager.VERTICAL))
        }

        store.musics
            .subscribe {
                viewAdapter.run {
                    viewAdapter.musics.clear()
                    viewAdapter.musics.addAll(it)
                    notifyDataSetChanged()
                }
            }
            .disposeWhenDestroy()
        actionCreator.addMusic(MusicAdapter.MusicData("Victim", "RbYva7AE8Aw"))
        actionCreator.addMusic(MusicAdapter.MusicData("Within", "3UJ_mERvw3A"))
    }
}
