package project.app.artistproject.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import project.app.artistproject.R
import project.app.artistproject.viewmodel.PlayerListViewModel
import project.app.artistproject.databinding.ActivityMainBinding
import project.app.artistproject.model.adapter.PlayListAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var playListAdapter: PlayListAdapter? = null

    private val playerlistViewModel: PlayerListViewModel by lazy {
        ViewModelProviders.of(this).get(PlayerListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = playerlistViewModel
        binding.lifecycleOwner = this

        // RecyclerView
        playlist.layoutManager = LinearLayoutManager(this)
        playListAdapter = PlayListAdapter(this)
        playlist.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        playlist.apply {
            adapter = playListAdapter
            hasFixedSize()
        }
        // RecyclerView OnClick Event
        playListAdapter?.selectItem?.observe(this, Observer {
            playerlistViewModel.playerPlay(it.source)
            playerlistViewModel.playingSongModel.value = it
            playerlistViewModel.loading.value = true
            playerlistViewModel.playing.value = true
        })

        playerlistViewModel.getSongList().observe(this, Observer {
            playListAdapter?.setList(it)
            playListAdapter?.notifyDataSetChanged()
        })

        // ExoPlayer
        musicPlayer.setPlayer(playerlistViewModel.getPlayer(this))
        musicPlayer.controllerHideOnTouch = false
        musicPlayer.controllerAutoShow = false
        musicPlayer.hideController()
        playerlistViewModel.getPlayerState(this, playListAdapter)
    }


    override fun onStart() {
        super.onStart()

        playerlistViewModel.addRealmListener()
        playerlistViewModel.setSongData()
    }

    override fun onPause() {
        super.onPause()

        playerlistViewModel.musicPause()
    }

    override fun onBackPressed() {
        playerlistViewModel.loading.value?.let {
            if(it)
                playerlistViewModel.musicStop()
            else
                super.onBackPressed()
        }
    }
}
