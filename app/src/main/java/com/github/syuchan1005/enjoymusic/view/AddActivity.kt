package com.github.syuchan1005.enjoymusic.view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.syuchan1005.enjoymusic.R
import com.github.syuchan1005.enjoymusic.di.Injectable
import com.github.syuchan1005.enjoymusic.store.MainActionCreator
import javax.inject.Inject

class AddActivity : AppCompatActivity(), Injectable {
    @Inject
    lateinit var actionCreator: MainActionCreator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
    }

    override fun onResume() {
        super.onResume()

        val type = intent.getStringExtra("TYPE")
        supportActionBar!!.title = type
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val text = findViewById<EditText>(R.id.name_text)
        val url = findViewById<EditText>(R.id.url_text)
        val okButton = findViewById<Button>(R.id.ok_button)
        okButton.text = type
        okButton.setOnClickListener {
            val data = MusicAdapter.MusicData(text.text.toString(), url.text.toString())
            if (type == "ADD") {
                actionCreator.addMusic(data)
            } else if (type == "EDIT") {
                actionCreator.editMusic(intent.getIntExtra("POSITION", 0), data)
            }
            onBackPressed()
        }

        if (type == "EDIT") {
            text.setText(intent.getStringExtra("NAME"), TextView.BufferType.EDITABLE)
            url.setText(intent.getStringExtra("URL"), TextView.BufferType.EDITABLE)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
