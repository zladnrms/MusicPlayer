package project.app.artistproject

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        val config = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()

        Realm.setDefaultConfiguration(config)
    }
}