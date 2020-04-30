package jp.techacademy.takanari.taskapp

import java.io.Serializable
import java.util.Date
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

//Serializableはデータを丸ごとファイルに保存したり、TaskAppでいうと別のActivityに渡すことができる
open class Task : RealmObject(), Serializable {
    var title: String = ""      // タイトル
    var contents: String = ""   // 内容
    var category:Category?= null    // カテゴリー
    var date: Date = Date()     // 日時


    // id をプライマリーキーとして設定
    //データーベースの一つのテーブルの中でデータを唯一的に確かめるための値
    @PrimaryKey
    var id: Int = 0
}