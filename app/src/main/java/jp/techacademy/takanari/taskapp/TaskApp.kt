package jp.techacademy.takanari.taskapp

import android.app.Application
import io.realm.Realm

class TaskApp: Application() {
    override fun onCreate() {
        super.onCreate()
        //Realm.init(this)をしてRealmを初期化
        Realm.init(this)
    }
}