package jp.techacademy.takanari.taskapp

import java.io.Serializable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

 class Category {
    var category:String = ""    // カテゴリー


    @PrimaryKey
    var id: Int = 0
}