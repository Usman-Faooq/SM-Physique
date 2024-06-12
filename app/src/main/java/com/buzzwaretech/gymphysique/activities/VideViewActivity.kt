package com.buzzwaretech.gymphysique.activities

import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.buzzwaretech.gymphysique.databinding.ActivityVideViewBinding

class VideViewActivity : AppCompatActivity() {

    lateinit var binding: ActivityVideViewBinding
    var videoUrl = ""
    private lateinit var player: ExoPlayer
    private var isFullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoUrl = intent.getStringExtra("URL").toString()

        player = ExoPlayer.Builder(this).build()
        val uri: Uri = Uri.parse(videoUrl)

        binding.exoVideoView.player = player

        val mediaItem = MediaItem.fromUri(uri)
        player.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    binding.loaderLayout.visibility = View.GONE
                } else {
                    binding.loaderLayout.visibility = View.VISIBLE
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                // Handle playback state changes if needed
                when(state){
                    Player.STATE_ENDED ->{
                        finish()
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Toast.makeText(this@VideViewActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })

        setView()
        setListener()

    }

    private fun setView() {



    }

    private fun setListener() {

        binding.backIV.setOnClickListener {
            finish()
        }

        binding.rotationIV.setOnClickListener {
            toggleFullScreen()
        }

    }


    private fun toggleFullScreen() {
        if (isFullScreen) {
            // Change to portrait orientation
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            isFullScreen = false
        } else {
            // Change to landscape orientation
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            isFullScreen = true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        player.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
    }

}