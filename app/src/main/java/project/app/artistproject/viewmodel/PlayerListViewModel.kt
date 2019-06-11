package project.app.artistproject.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.raywenderlich.funtime.device.player.MediaPlayerImpl
import io.realm.RealmChangeListener
import io.realm.RealmResults
import project.app.artistproject.model.PlaylistDatabase
import project.app.artistproject.model.PlaylistRepository
import project.app.artistproject.model.data.SongModel
import com.google.android.exoplayer2.Player.STATE_ENDED
import project.app.artistproject.model.adapter.PlayListAdapter

class PlayerListViewModel : ViewModel() {

    private val repository: PlaylistRepository by lazy {
        PlaylistDatabase()
    }
    private val exoPlayer: MediaPlayerImpl by lazy {
        MediaPlayerImpl()
    }
    private val listener = RealmChangeListener<RealmResults<SongModel>>{
        setSongData()
    }

    var loading = MutableLiveData<Boolean>().apply { value = false } // 가져온 노래가 있는 지
    var playing = MutableLiveData<Boolean>().apply { value = false } // 노래가 실행중인지
    var playingSongModel = MutableLiveData<SongModel>() // 가져온 노래 데이터
    var playlistMutableLiveData = MutableLiveData<ArrayList<SongModel>>() // 노래 목록

    fun getPlayer(context: Context) : ExoPlayer {
        return exoPlayer.getPlayerImpl(context)
    }

    fun getPlayerState(owner: LifecycleOwner, adapter: PlayListAdapter?) {
        exoPlayer.getPlayerState().observe(owner, Observer {
            if(it == STATE_ENDED)
            {
                loading.value = false
                repository.plusPlayCount(adapter?.selectItem?.value)
            }
        })
    }

    fun playerPlay(url : String?) {
        url?.let {
            exoPlayer.play(it)
        }
    }

    fun musicPrev() {
        playing.value = false
        playlistMutableLiveData.value?.let {
            val musicNumber : Int = it.indexOf(playingSongModel.value)
            if(musicNumber >= 1)
            {
                val targetSong : SongModel = it[musicNumber.minus(1)]
                playingSongModel.value = targetSong
                playerPlay(targetSong.source)
            }
            else
            {
                val targetSong : SongModel= it[it.size - 1]
                playingSongModel.value = targetSong
                playerPlay(targetSong.source)
            }
        }
        playing.value = true
    }

    fun musicPlay() {
        exoPlayer.restartPlayer()
        this.playing.value = true
    }

    fun musicPause() {
        exoPlayer.pausePlayer()
        this.playing.value = false
    }

    fun musicStop() {
        exoPlayer.pausePlayer()
        this.playing.value = false
        this.loading.value = false
        this.playingSongModel.value = null
    }

    fun musicNext() {
        playing.value = false
        playlistMutableLiveData.value?.let {
            val musicNumber : Int = it.indexOf(playingSongModel.value)
            if(musicNumber == it.size - 1)
            {
                val targetSong : SongModel= it[0]
                playingSongModel.value = targetSong
                playerPlay(targetSong.source)
            }
            else
            {
                val targetSong : SongModel = it[musicNumber.plus(1)]
                playingSongModel.value = targetSong
                playerPlay(targetSong.source)
            }
        }
        playing.value = true
    }

    fun setSongData() {
        repository.toObservable().subscribe{
            playlistMutableLiveData.value = it as ArrayList
        }
    }

    fun getSongList(): MutableLiveData<ArrayList<SongModel>> {
        return playlistMutableLiveData
    }

    fun addRealmListener() {
        repository.active(listener)
    }

    override fun onCleared() {
        repository.deactive()
        super.onCleared()
    }
}
