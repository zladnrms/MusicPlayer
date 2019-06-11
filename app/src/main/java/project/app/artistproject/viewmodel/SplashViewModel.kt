package project.app.artistproject.viewmodel

import android.accounts.NetworkErrorException
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afollestad.materialdialogs.MaterialDialog
import project.app.artistproject.model.PlaylistDatabase
import project.app.artistproject.model.PlaylistRepository
import com.google.gson.JsonObject
import com.zeniex.www.zeniexautomarketing.network.ApiInterface
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.RealmChangeListener
import io.realm.RealmResults
import org.json.JSONObject
import project.app.artistproject.R
import project.app.artistproject.model.data.SongModel

class SplashViewModel : ViewModel() {

    private val retrofitClient by lazy {
        ApiInterface.create()
    }
    private val repository: PlaylistRepository by lazy {
        PlaylistDatabase()
    }
    private val listener = RealmChangeListener<RealmResults<SongModel>> {
        Log.d("Line 32 : RealmChangeListener", "Changed : " + it)
    }

    var progress = MutableLiveData<Boolean>().apply { value = false }
    var finish = MutableLiveData<Boolean>().apply { value = false }

    fun getJsonData(context: Context) {
        retrofitClient.getJson()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<JsonObject> {

                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(repo: JsonObject) {
                    repo.let {
                        val resultObject = JSONObject(it.toString())
                        val resultArray = resultObject.getJSONArray("music")
                        repository.getJsonData(resultArray)
                    }

                    progress.value = true
                }

                override fun onError(e: Throwable) {
                    /*
                    when (e) {
                        is NetworkErrorException ->
                        is ConnectException ->
                        is TimeoutException ->
                        else ->
                    }*/
                    MaterialDialog(context).show {
                        title(R.string.dialog_title)
                        message(R.string.dialog_content)
                        positiveButton(R.string.dialog_retry) {
                            getJsonData(context)
                        }
                        negativeButton(R.string.dialog_close) {
                            finish.value = true
                        }
                    }
                }
            })
    }

    fun addRealmListener() {
        repository.active(listener)
    }

    override fun onCleared() {
        repository.deactive()
        super.onCleared()
    }
}
