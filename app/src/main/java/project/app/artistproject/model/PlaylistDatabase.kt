package project.app.artistproject.model

import io.reactivex.Observable
import io.realm.*
import org.json.JSONArray
import org.json.JSONObject
import project.app.artistproject.model.data.SongModel

class PlaylistDatabase : PlaylistRepository {

    companion object {
        val realm: Realm by lazy {
            Realm.getDefaultInstance()
        }
    }

    override fun active(listener: RealmChangeListener<RealmResults<SongModel>>) {
        realm.addChangeListener { listener }
    }

    override fun deactive() {
        realm.removeAllChangeListeners()
    }

    override fun add() {
        realm.beginTransaction()
        realm.commitTransaction()
    }

    override fun toObservable(): Observable<List<SongModel>> {
        realm.beginTransaction()
        val realmResult = realm.where(SongModel::class.java).findAll()
        realm.commitTransaction()
        val list: List<SongModel> = realm.copyFromRealm(realmResult)
        return Observable.fromArray(list)
    }

    override fun getJsonData(value: JSONArray?) {
        value?.let {
            realm.beginTransaction()

            /*
             * 1) 기존 캐싱된 데이터를 RealmResult(List)화 한다
             * 2) 가져온 JSONArray와 기존 List를 비교해서, 가져온 JSONArray에 기존 List에 같은 SongModel id가 있다면
             * 3) 그 SongModel의 compare 값은 true가 된다
             * 4) 모두 비교 한 후 기존 캐싱된 데이터 List의 compare이 false인 것을 삭제한다. true가 된 것은 다시 false한다
             * 5) 업데이트된 RealmResult를 다시 넣는다
             * 6) 이후 그 데이터에 가져온 것 덧씌운다
             */

            // 1)
            val realmResult = realm.where(SongModel::class.java).findAll()

            // 2)
            for (i in 0 until it.length()) {
                val model: SongModel =
                    realm.createOrUpdateObjectFromJson(SongModel::class.java, it.get(i) as JSONObject)
                for (item in realmResult) {
                    if (item.equals(model)) {
                        item.compare = true // 3)
                        break
                    }
                }
            }

            // 4)
            for (item in realmResult) {
                if (item.compare == false) {
                    item.deleteFromRealm()
                } else {
                    item.compare = false
                }
            }

            realm.copyToRealmOrUpdate(realmResult) // 5)
            realm.createOrUpdateAllFromJson(SongModel::class.java, it) // 6)
            realm.commitTransaction()
        }
    }

    override fun findSongModel(data: SongModel): SongModel? {
        realm.beginTransaction()
        val realmResult = realm.where(SongModel::class.java).equalTo("id", data.id).findFirst()
        realm.commitTransaction()

        return realmResult
    }

    override fun plusPlayCount(data: SongModel?) {
        realm.beginTransaction()
        val realmResult = realm.where(SongModel::class.java).equalTo("id", data?.id).findFirst()
        realmResult?.let {
            it.playCount++
            realm.insertOrUpdate(it)
        }
        realm.commitTransaction()
    }
}