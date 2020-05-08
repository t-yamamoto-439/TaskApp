package jp.techacademy.takanari.taskapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import io.realm.Realm
import kotlinx.android.synthetic.main.content_input.*
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import io.realm.RealmChangeListener
import io.realm.RealmResults

class InputActivity : AppCompatActivity() {

    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0
    private var mTask: Task? = null
    //スピナーに入れる配列
    val spinnerItems = arrayListOf<Category>()
    //何番目か数える用
    var count = 0

    lateinit var mCategoryAdapter : CategoryAdapter

    private lateinit var mRealm: Realm
    //mRealmListenerはRealmのデータベースに追加や削除など変化があった場合に呼ばれるリスナー
    private val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(element: Realm) {
            reloadListView()
        }
    }

    var selectCategory : Category?=null

    lateinit var categoryRealmResults: RealmResults<Category>

    private val mOnDateClickListener = View.OnClickListener {
        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                mYear = year
                mMonth = month
                mDay = dayOfMonth
                val dateString = mYear.toString() + "/" + String.format("%02d", mMonth + 1) + "/" + String.format("%02d", mDay)
                date_button.text = dateString
            }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    //mOnDoneClickListenerでは、addTaskメソッドでRealmに保存/更新したあと、
    // finishメソッドを呼び出すことでInputActivityを閉じて前の画面（MainActivity）に戻る
    private val mOnTimeClickListener = View.OnClickListener {
        val timePickerDialog = TimePickerDialog(this,
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                mHour = hour
                mMinute = minute
                val timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute)
                times_button.text = timeString
            }, mHour, mMinute, false)
        timePickerDialog.show()
    }

    private val mOnDoneClickListener = View.OnClickListener {
        mRealm.removeChangeListener(mRealmListener)
        addTask()
        finish()
    }

    private val mOnCategryClickLitener = View.OnClickListener{
        val intent = Intent(this@InputActivity, categoryActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        // Realmの設定
        mRealm = Realm.getDefaultInstance()
        mRealm.addChangeListener(mRealmListener)

        mCategoryAdapter = CategoryAdapter(this@InputActivity)


        // ActionBarを設定する
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        //setSupportActionBarによってツールバーをActionBarとして使えるように設定
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            //setDisplayHomeAsUpEnabledメソッドで、ActionBarに戻るボタンを表示
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        // UI部品の設定
        date_button.setOnClickListener(mOnDateClickListener)
        times_button.setOnClickListener(mOnTimeClickListener)
        done_button.setOnClickListener(mOnDoneClickListener)
        category_button.setOnClickListener(mOnCategryClickLitener)

        // EXTRA_TASK から Task の id を取得して、 id から Task のインスタンスを取得する
        val intent = intent
        //EXTRA_TASK が設定されていないとtaskId には第二引数で指定している既定値 -1 が代入される
        val taskId = intent.getIntExtra(EXTRA_TASK, -1)
        val realm = Realm.getDefaultInstance()
        //Task の id が taskId のものが検索され、findFirst() によって最初に見つかったインスタンスが返され、 mTask へ代入
        mTask = realm.where(Task::class.java).equalTo("id", taskId).findFirst()
        realm.close()

        //EXTRA_TASK が設定されていない、すなわち taskId が -1の場合、nullが代入
        if (mTask == null) {
            // 新規作成の場合
            val calendar = Calendar.getInstance()
            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            mHour = calendar.get(Calendar.HOUR_OF_DAY)
            mMinute = calendar.get(Calendar.MINUTE)
        } else {
            // 更新の場合
            title_edit_text.setText(mTask!!.title)
            content_edit_text.setText(mTask!!.contents)
//            spinnerItems.setText(mTask!!.category)

            val calendar = Calendar.getInstance()
            calendar.time = mTask!!.date
            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            mHour = calendar.get(Calendar.HOUR_OF_DAY)
            mMinute = calendar.get(Calendar.MINUTE)

            val dateString = mYear.toString() + "/" + String.format("%02d", mMonth + 1) + "/" + String.format("%02d", mDay)
            val timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute)

            date_button.text = dateString
            times_button.text = timeString
            selectCategory = mTask!!.category
        }
        reloadListView()

        spinner.adapter = mCategoryAdapter

        selectCategory?.let {spinner.setSelection(it.id+1,false) }
//        spinner.setSelection(position,false)


        // リスナーを登録
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?,
                                        view: View?, position: Int, id: Long) {
                selectCategory = mCategoryAdapter.categoryList[position]
                // Kotlin Android Extensions
            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }

    private fun reloadListView() {
//        mCategoryAdapter.categoryList.clear()
        // Realmデータベースから、「全てのデータを取得して新しい日時順に並べた結果」を取得
        categoryRealmResults = mRealm.where(Category::class.java).findAll()
        mCategoryAdapter.categoryList.clear()
        mCategoryAdapter.categoryList.add(Category().apply { id = -1; category = "カテゴリーの選択" })
        // 上記の結果を、TaskList としてセットする
        mCategoryAdapter.categoryList.addAll(mRealm.copyFromRealm(categoryRealmResults))
        // TaskのListView用のアダプタに渡す
        spinner.adapter = mCategoryAdapter

        // 表示を更新するために、アダプターにデータが変更されたことを知らせる
        mCategoryAdapter.notifyDataSetChanged()
    }


    private fun addTask() {
            val realm = Realm.getDefaultInstance()

            realm.beginTransaction()

            if (mTask == null) {
                // 新規作成の場合
                mTask = Task()

                val taskRealmResults = realm.where(Task::class.java).findAll()

                val identifier: Int =
                    if (taskRealmResults.max("id") != null) {
                        taskRealmResults.max("id")!!.toInt() + 1
                    } else {
                        0
                    }
                mTask!!.id = identifier
            }

        val title = title_edit_text.text.toString()
        val content = content_edit_text.text.toString()
        selectCategory?.let { if(it.id != -1){ val result = mRealm.where(Category::class.java).equalTo("id", selectCategory!!.id).findFirst()
            mRealm.copyFromRealm(categoryRealmResults)
            mTask!!.category = result
        } }


        mTask!!.title = title
        mTask!!.contents = content
        val calendar = GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute)
        val date = calendar.time
        mTask!!.date = date

        realm.copyToRealmOrUpdate(mTask!!)
        realm.commitTransaction()

        realm.close()

        val resultIntent = Intent(applicationContext, TaskAlarmReceiver::class.java)
        resultIntent.putExtra(EXTRA_TASK, mTask!!.id)
        //PendingIntentは
        val resultPendingIntent = PendingIntent.getBroadcast(
            this,
            mTask!!.id,
            resultIntent,
            //PendingIntent.FLAG_UPDATE_CURRENTは既存のPendingIntentがあれば、タスクのデータだけ置き換える
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        //第一引数のRTC_WAKEUPはUTC時間を指定する。画面スリープ中でもアラームを発行する
        //第二引数でタスクの時間をUTC時間で指定しています。
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, resultPendingIntent)
    }

}