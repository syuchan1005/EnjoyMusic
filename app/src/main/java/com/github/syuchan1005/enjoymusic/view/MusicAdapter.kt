package com.github.syuchan1005.enjoymusic.view

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Handler
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.os.postDelayed
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.syuchan1005.enjoymusic.R
import com.github.syuchan1005.enjoymusic.store.MainActionCreator
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import java.net.URLDecoder


class MusicAdapter constructor(
    private val actionCreator: MainActionCreator,
    private val activity: MainActivity
) :
    RecyclerView.Adapter<MusicAdapter.MyViewHolder>() {
    val queue = Volley.newRequestQueue(activity)

    val musics = mutableListOf<MusicData>()
    var expand = -1

    class MusicData(val name: String, val url: String) {
        var player: MediaPlayer? = null

        private var bindSeekBar: SeekBar? = null
        private val seekBarUpdateHandler = Handler()
        private val updateSeekBar = object : Runnable {
            override fun run() {
                bindSeekBar?.progress = player!!.currentPosition
                seekBarUpdateHandler.postDelayed(this, 50)
            }
        }

        fun prepare(adapter: MusicAdapter, callback: ((MediaPlayer) -> Unit)?) {
            if (player == null) {
                adapter.queue.add(StringRequest("https://youtube.com/get_video_info?video_id=${url}&el=detailpage", {
                    player = MediaPlayer()
                    player!!.setDataSource(extractURL(it))
                    player!!.prepare()

                    callback?.invoke(player!!)
                }, null))
            } else {
                callback?.invoke(player!!)
            }
        }

        fun bindSeekBar(seekBar: SeekBar) {
            if (bindSeekBar == seekBar) return
            bindSeekBar = seekBar
            val data = this

            bindSeekBar!!.max = player!!.duration
            bindSeekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStartTrackingTouch(p0: SeekBar?) {
                    data.pause()
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {}

                override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        player!!.seekTo(progress)
                        data.start()
                    }
                }
            })
        }

        fun start() {
            if (player != null) {
                player!!.start()
                seekBarUpdateHandler.postDelayed(updateSeekBar, 0)
            }
        }

        fun pause() {
            if (player != null) {
                player!!.pause()
                seekBarUpdateHandler.removeCallbacks(updateSeekBar)
            }
        }

        private fun extractURL(str: String): String {
            val fmtsIndex = str.indexOf("adaptive_fmts=")
            val fmts = URLDecoder.decode(str.substring(fmtsIndex, str.indexOf('&', fmtsIndex)), "utf8")
            val urlIndex = fmts.indexOf("url=", fmts.indexOf("type=audio%2Fmp4")) + 4
            return URLDecoder.decode(fmts.substring(urlIndex, fmts.indexOf('&', urlIndex)), "UTF8")
        }
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.song_tile, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val musicData = musics[position]

        Picasso.get()
            .load("https://i.ytimg.com/vi/${musicData.url}/mqdefault.jpg")
            .placeholder(R.drawable.ic_airplanemode_active_black_24dp)
            .error(R.drawable.ic_airplanemode_inactive_black_24dp)
            .into(holder.view.findViewById<ImageView>(R.id.imageView))

        holder.view.findViewById<TextView>(R.id.text_view).text = musicData.name

        holder.view.findViewById<MaterialButton>(R.id.edit_button).setOnClickListener {
            val intent = Intent(activity.application, AddActivity::class.java)
            intent.putExtra("TYPE", "EDIT")
            intent.putExtra("NAME", musicData.name)
            intent.putExtra("URL", musicData.url)
            intent.putExtra("POSITION", position)
            activity.startActivity(intent)
        }

        val moreButton = holder.view.findViewById<MaterialButton>(R.id.more_button)
        val playButton = holder.view.findViewById<MaterialButton>(R.id.play_button)
        val seekBar = holder.view.findViewById<SeekBar>(R.id.seek_bar)

        moreButton.setOnClickListener {
            if (expand != -1) musics[expand].pause()

            if (expand == position) {
                expand = -1
            } else {
                notifyItemChanged(expand)
                expand = position
            }
            playButton.icon = activity.getDrawable(R.drawable.ic_play_arrow_black_24dp)
            notifyItemChanged(position)
        }

        playButton.setOnClickListener {
            when {
                musicData.player == null -> {
                    playButton.isEnabled = false
                    musicData.prepare(this) {
                        musicData.bindSeekBar(seekBar)
                        musicData.start()
                        playButton.icon = activity.getDrawable(R.drawable.ic_pause_black_24dp)
                        playButton.isEnabled = true
                    }
                }
                musicData.player!!.isPlaying -> {
                    musicData.pause()
                    playButton.icon = activity.getDrawable(R.drawable.ic_play_arrow_black_24dp)
                }
                else -> {
                    musicData.start()
                    playButton.icon = activity.getDrawable(R.drawable.ic_pause_black_24dp)
                }
            }
        }

        val layoutParams = holder.view.layoutParams
        layoutParams.height = dp2px(if (expand == position) 160F else 80F, activity)
        holder.view.layoutParams = layoutParams
        moreButton.icon = activity.getDrawable(
            if (expand == position) R.drawable.ic_keyboard_arrow_up_black_24dp
            else R.drawable.ic_keyboard_arrow_down_black_24dp
        )
    }

    fun dp2px(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics())
            .toInt()
    }

    override fun getItemCount() = musics.size

}
