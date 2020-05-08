package jp.techacademy.takanari.taskapp

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

//Code -> Generate を選択
class TaskAdapter(context: Context): BaseAdapter() {
    private val mLayoutInflater: LayoutInflater
    var taskList = listOf<Task>()

    init {
        this.mLayoutInflater = LayoutInflater.from(context)
    }

    //mTaskListのサイズ
    override fun getCount(): Int {
        return taskList.size
    }

    //mTaskListの要素
    override fun getItem(position: Int): Any {
        return taskList[position]
    }

    //getItemIdメソッドではidを返す
    override fun getItemId(position: Int): Long {
        return taskList[position].id.toLong()
    }



    //convertViewがnullのときはLayoutInflaterを使ってsimple_list_item_2からViewを取得
    //simple_list_item_2はタイトルとサブタイトルがあるセルです。まずはString型で保持しているtaskListから文字列を取得しタイトルを設定
    //getViewメソッドではTaskのタイトルと時間をTextViewに設定
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //convertViewは現在表示しようとしている行がnullかどうか判定を行っているのは、BaseAdapterにViewを再利用して描画する仕組みがあるため
        val view: View = convertView ?: mLayoutInflater.inflate(R.layout.simple_list_item_2, null)

        val textView1 = view.findViewById<TextView>(R.id.text1)
        val textView2 = view.findViewById<TextView>(R.id.text2)


        textView1.text = taskList[position].title

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.JAPANESE)
        val date = taskList[position].date
        textView2.text = simpleDateFormat.format(date)



        return view
    }
}