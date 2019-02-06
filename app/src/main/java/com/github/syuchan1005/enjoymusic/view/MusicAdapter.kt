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

    data class MusicData(val name: String, val url: String, var open: Boolean = false)

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
        moreButton.setOnClickListener {
            if (expand == position) expand = -1
            else {
                notifyItemChanged(expand)
                expand = position
            }
            notifyItemChanged(position)
        }

        val playButton = holder.view.findViewById<MaterialButton>(R.id.play_button)
        val seekBar = holder.view.findViewById<SeekBar>(R.id.seek_bar)
        var player: MediaPlayer? = null
        val seekBarUpdateHandler = Handler()
        val updateSeekBar = object : Runnable {
            override fun run() {
                seekBar.setProgress(player!!.currentPosition)
                seekBarUpdateHandler.postDelayed(this, 50)
            }
        }
        playButton.setOnClickListener {
            if (player == null) {
                queue.add(StringRequest("https://youtube.com/get_video_info?video_id=${musicData.url}&el=detailpage", {
                    player = MediaPlayer()
                    player!!.setDataSource(extractURL(it))
                    player!!.prepare()

                    player!!.start()
                    seekBar.max = player!!.duration
                    seekBarUpdateHandler.postDelayed(updateSeekBar, 0)
                    seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onStartTrackingTouch(p0: SeekBar?) {
                            player!!.pause()
                            seekBarUpdateHandler.removeCallbacks(updateSeekBar)
                        }
                        override fun onStopTrackingTouch(p0: SeekBar?) {}

                        override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                            if (fromUser) {
                                player!!.seekTo(progress)
                                player!!.start()
                                seekBarUpdateHandler.postDelayed(updateSeekBar, 0)
                            }
                        }
                    })

                    playButton.icon = activity.getDrawable(R.drawable.ic_pause_black_24dp)
                }, null))
            } else if (player!!.isPlaying) {
                player!!.pause()
                seekBarUpdateHandler.removeCallbacks(updateSeekBar)
                playButton.icon = activity.getDrawable(R.drawable.ic_play_arrow_black_24dp)
            } else {
                player!!.start()
                seekBarUpdateHandler.postDelayed(updateSeekBar, 0)
                playButton.icon = activity.getDrawable(R.drawable.ic_pause_black_24dp)
            }
        }

        val layoutParams = holder.view.layoutParams
        layoutParams.height = dp2px(if (expand == position) 160F else 80F, activity)
        holder.view.layoutParams = layoutParams
        moreButton.icon =
                activity.getDrawable(if (expand == position) R.drawable.ic_keyboard_arrow_up_black_24dp else R.drawable.ic_keyboard_arrow_down_black_24dp)
    }

    fun dp2px(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics())
            .toInt()
    }

    fun extractURL(str: String): String {
        val fmtsIndex = str.indexOf("adaptive_fmts=")
        val fmts = URLDecoder.decode(str.substring(fmtsIndex, str.indexOf('&', fmtsIndex)), "utf8")
        val urlIndex = fmts.indexOf("url=", fmts.indexOf("type=audio%2Fmp4")) + 4
        return URLDecoder.decode(fmts.substring(urlIndex, fmts.indexOf('&', urlIndex)), "UTF8")
    }

    override fun getItemCount() = musics.size

    fun deleteItem(position: Int) {
        actionCreator.removeMusic(position)
        notifyItemRemoved(position)
    }

    class SwipeToDeleteCallback constructor(
        dragDirs: Int = 0,
        swipeDirs: Int = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
        private val adapter: MusicAdapter
    ) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

        private val background = ColorDrawable(Color.RED)

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            adapter.deleteItem(viewHolder.adapterPosition)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            val itemView = viewHolder.itemView
            val backgroundCornerOffset = 20
            if (dX > 0) { // Swiping to the right
                background.setBounds(
                    itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + dX.toInt() + backgroundCornerOffset,
                    itemView.getBottom()
                )

            } else if (dX < 0) { // Swiping to the left
                background.setBounds(
                    itemView.getRight() + dX.toInt() - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom()
                )
            } else { // view is unSwiped
                background.setBounds(0, 0, 0, 0)
            }
            background.draw(c)
        }
    }
}
