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
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.category_input.*

class categoryActivity : AppCompatActivity() {

    private var mCategory: Category? = null

    private val OnCategoryClickListener = View.OnClickListener{
        addCategory()
        finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)


        add_button.setOnClickListener(OnCategoryClickListener)

    }


    private fun addCategory() {
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()

        if (mCategory == null) {
            // 新規作成の場合
            mCategory = Category()

            val taskRealmResults = realm.where(Category::class.java).findAll()

            val identifier: Int =
                if (taskRealmResults.max("id") != null) {
                    taskRealmResults.max("id")!!.toInt() + 1
                } else {
                    0
                }
            mCategory!!.id = identifier
        }

        val category = category_edit_text.text.toString()
        mCategory!!.category = category

        realm.copyToRealmOrUpdate(mCategory!!)
        realm.commitTransaction()

        realm.close()
    }
}
