package com.raywenderlich.funtime.device.player


import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.util.Util
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import android.support.v4.media.session.MediaSessionCompat
import android.content.Context
import android.net.Uri
import android.support.v4.media.session.PlaybackStateCompat
import project.app.artistproject.player.MediaPlayer

class MediaPlayerImpl : MediaPlayer {

    private lateinit var context: Context
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private var state = MutableLiveData<Int>().apply { value = 0 }

    private fun initPlayer() {
        val trackSelector = DefaultTrackSelector()
        val loadControl = DefaultLoadControl()
        val renderersFactory = DefaultRenderersFactory(context)

        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, renderersFactory, trackSelector, loadControl)
    }

    private fun initMediaSession() {
        stateBuilder = PlaybackStateCompat.Builder()
        stateBuilder.apply {
            this.setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_PLAY_PAUSE or
                        PlaybackStateCompat.ACTION_FAST_FORWARD or
                        PlaybackStateCompat.ACTION_REWIND
            )
        }
        mediaSession = MediaSessionCompat(context, "ExoPlayer")
        mediaSession.apply {
            this.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            this.setMediaButtonReceiver(null)
            this.setPlaybackState(stateBuilder.build())
            this.setCallback(SessionCallback())
            this.isActive = true
        }
    }

    override fun play(url: String) {
        val agent = Util.getUserAgent(context, "ExoPlayer")

        val mediaSource = ExtractorMediaSource.Factory(DefaultDataSourceFactory(context, agent))
            .setExtractorsFactory(DefaultExtractorsFactory())
            .createMediaSource(Uri.parse(url))

        exoPlayer.apply {
            this.prepare(mediaSource)
            this.addListener(object : Player.EventListener {
                override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
                }

                override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
                }

                override fun onLoadingChanged(isLoading: Boolean) {
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {
                        STATE_ENDED -> {
                            state.value = STATE_ENDED
                        }
                    }
                }

                override fun onPlayerError(error: ExoPlaybackException?) {
                    exoPlayer.stop()
                    exoPlayer.prepare(mediaSource)
                    exoPlayer.playWhenReady = true
                }
            })
            this.playWhenReady = true
        }
    }

    override fun getPlayerState(): MutableLiveData<Int> {
        return state
    }

    override fun getPlayerImpl(context: Context): ExoPlayer {
        this.context = context
        initPlayer()
        initMediaSession()
        return exoPlayer
    }

    override fun releasePlayer() {
        exoPlayer.stop()
        exoPlayer.release()
    }

    override fun pausePlayer() {
        SessionCallback().onPause()
    }

    override fun restartPlayer() {
        SessionCallback().onPlay()
    }

    private inner class SessionCallback : MediaSessionCompat.Callback() {

        private val SEEK_WINDOW_MS = 10000

        override fun onPlay() {
            exoPlayer.playWhenReady = true
        }

        override fun onPause() {
            exoPlayer.playWhenReady = false
        }

        override fun onRewind() {
            exoPlayer.seekTo(exoPlayer.currentPosition - SEEK_WINDOW_MS)
        }

        override fun onFastForward() {
            exoPlayer.seekTo(exoPlayer.currentPosition + SEEK_WINDOW_MS)
        }
    }
}