package project.app.artistproject.model.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SongModel(
    @PrimaryKey var id: String? = null,
    var title: String? = null,
    var album: String? = null,
    var artist: String? = null,
    var genre: String? = null,
    var source: String? = null,
    var image: String? = null,
    var trackNumber: Int = 0,
    var totalTrackCount: Int = 0,
    var duration: Int = 0,
    var site: String? = null,
    var playCount: Int = 0,
    var compare: Boolean = false
) : RealmObject() {
    override fun equals(other: Any?): Boolean {
        if (this.id.equals((other as SongModel).id)) return true else return false
    }
}
