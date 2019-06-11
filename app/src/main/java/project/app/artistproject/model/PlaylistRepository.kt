package project.app.artistproject.model

import io.reactivex.Observable
import io.realm.RealmChangeListener
import io.realm.RealmResults
import org.json.JSONArray
import project.app.artistproject.model.data.SongModel

interface PlaylistRepository {
    fun active(listener : RealmChangeListener<RealmResults<SongModel>>)

    fun deactive()

    fun toObservable(): Observable<List<SongModel>>

    fun add()

    fun getJsonData(value: JSONArray?)

    fun findSongModel(data: SongModel) : SongModel?

    fun plusPlayCount(data: SongModel?)
}