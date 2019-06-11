package project.app.artistproject.player

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.ExoPlayer

interface MediaPlayer {

  fun play(url: String)

  fun getPlayerImpl(context: Context): ExoPlayer

  fun getPlayerState() : MutableLiveData<Int>

  fun releasePlayer()

  fun pausePlayer()

  fun restartPlayer()
}