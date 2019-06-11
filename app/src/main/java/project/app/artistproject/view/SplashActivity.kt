package project.app.artistproject.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_splash.*
import project.app.artistproject.R
import project.app.artistproject.databinding.ActivitySplashBinding
import project.app.artistproject.viewmodel.SplashViewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding

    private val splashViewModel: SplashViewModel by lazy {
        ViewModelProviders.of(this).get(SplashViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.viewModel = splashViewModel
        binding.lifecycleOwner = this

        splashViewModel.progress.observe(this, Observer {
            if(it)
            {
                progress_view.stopAnimation()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else
                progress_view.startAnimation()
        })

        splashViewModel.finish.observe(this, Observer {
            if(it)
                finish()
        })
    }

    override fun onStart() {
        super.onStart()

        splashViewModel.addRealmListener()
        splashViewModel.getJsonData(this)
    }
}
