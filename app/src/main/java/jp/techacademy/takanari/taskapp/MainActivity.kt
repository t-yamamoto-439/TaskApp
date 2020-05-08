package jp.techacademy.takanari.taskapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import io.realm.RealmChangeListener
import io.realm.Sort
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.app.AlarmManager
import android.app.PendingIntent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import io.realm.RealmResults
import kotlinx.android.synthetic.main.content_input.*

const val EXTRA_TASK = "jp.techacademy.takanari.taskapp"
//const val EXTRA_TASK = "jp.techacademy.taro.kirameki.taskapp.Category"


class MainActivity : AppCompatActivity() {
    private lateinit var mRealm: Realm
    //mRealmListenerはRealmのデータベースに追加や削除など変化があった場合に呼ばれるリスナー
    private val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(element: Realm) {
            reloadListView()
        }
    }

    private lateinit var mTaskAdapter: TaskAdapter

    lateinit var mCategoryAdapter : CategoryAdapter

    lateinit var categoryRealmResults: RealmResults<Category>

    var selectCategory : Category?=null

    private var mTask: Task? = null

    //保存用
    var listsave = listOf<Task>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        fab.setOnClickListener { view ->
            val intent = Intent(this@MainActivity, InputActivity::class.java)
            startActivity(intent)
        }

        // Realmの設定
        mRealm = Realm.getDefaultInstance()
        mRealm.addChangeListener(mRealmListener)

        // ListViewの設定
        mTaskAdapter = TaskAdapter(this@MainActivity)

        mCategoryAdapter = CategoryAdapter(this@MainActivity)


        // ListViewをタップしたときの処理
        listView1.setOnItemClickListener { parent, _, position, _ ->
            // 入力・編集する画面に遷移させる
            val task = parent.adapter.getItem(position) as Task
            val intent = Intent(this@MainActivity, InputActivity::class.java)
            intent.putExtra(EXTRA_TASK, task.id)
            startActivity(intent)
        }

        // spinner に adapter をセット
        // Kotlin Android Extensions
        spinner1.adapter = mCategoryAdapter

        mTask?.category?.let {spinner.setSelection(it.id,false) }


// リスナーを登録
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?,
                                        view: View?, position: Int, id: Long) {
                val newList =
                if (mCategoryAdapter.categoryList[position].category == ""){
                    listsave
                }
                else {
                        listsave.filter { it.category?.category == mCategoryAdapter.categoryList[position].category }
                }

                // 上記の結果を、TaskList としてセットする
                mTaskAdapter.taskList = newList
                // TaskのListView用のアダプタに渡す
                listView1.adapter = mTaskAdapter
                // 表示を更新するために、アダプターにデータが変更されたことを知らせる
                mTaskAdapter.notifyDataSetChanged()
            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        // ListViewを長押ししたときの処理
        listView1.setOnItemLongClickListener { parent, _, position, _ ->
            // タスクを削除する
            val task = parent.adapter.getItem(position) as Task

            // ダイアログを表示する
            val builder = AlertDialog.Builder(this@MainActivity)

            builder.setTitle("削除")
            builder.setMessage(task.title + "を削除しますか")

            builder.setPositiveButton("OK") { _, _ ->
                val results = mRealm.where(Task::class.java).equalTo("id", task.id).findAll()

                mRealm.beginTransaction()
                results.deleteAllFromRealm()
                mRealm.commitTransaction()

                val resultIntent = Intent(applicationContext, TaskAlarmReceiver::class.java)
                val resultPendingIntent = PendingIntent.getBroadcast(
                    this@MainActivity,
                    task.id,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(resultPendingIntent)

                reloadListView()
            }

            builder.setNegativeButton("CANCEL", null)

            val dialog = builder.create()
            dialog.show()

            true
        }
        reloadListView()
    }


    private fun reloadListView() {
        // Realmデータベースから、「全てのデータを取得して新しい日時順に並べた結果」を取得
        val taskRealmResults = mRealm.where(Task::class.java).findAll().sort("date", Sort.DESCENDING)

        // 上記の結果を、TaskList としてセットする
        mTaskAdapter.taskList = mRealm.copyFromRealm(taskRealmResults)

        // TaskのListView用のアダプタに渡す
        listView1.adapter = mTaskAdapter

        //弄る用
        mTaskAdapter.taskList = mRealm.copyFromRealm(taskRealmResults)

        //保存用
        listsave = mRealm.copyFromRealm(taskRealmResults)

        // 表示を更新するために、アダプターにデータが変更されたことを知らせる
        mTaskAdapter.notifyDataSetChanged()

        //spinner1用
//        categoryRealmResults = mRealm.where(Category::class.java).findAll()
//        mCategoryAdapter.categoryList.add(Category())

        categoryRealmResults = mRealm.where(Category::class.java).findAll()
        mCategoryAdapter.categoryList.clear()
        mCategoryAdapter.categoryList.add(Category())
        // 上記の結果を、TaskList としてセットする
        mCategoryAdapter.categoryList.addAll(mRealm.copyFromRealm(categoryRealmResults))

        spinner1.adapter = mCategoryAdapter

        mCategoryAdapter.notifyDataSetChanged()

    }

    override fun onDestroy() {
        super.onDestroy()

        mRealm.close()
    }

}