package com.example.boatcontrol

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

class VideoPlayerFragment : Fragment() {
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    var rtspUrl: String = "rtsp://192.168.1.113:554/live/main_stream"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.exoplayer_view, container, false).also {
            playerView = it.findViewById(R.id.player_view)
        }

    override fun onStart() {
        super.onStart()
        player = ExoPlayer.Builder(requireContext()).build()
        playerView.player = player
        val mediaItem = MediaItem.fromUri(Uri.parse(rtspUrl))
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        playerView.player = null
        player?.release()
        player = null
    }
}
