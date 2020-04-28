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


class CategoryAdapter (context: Context): BaseAdapter() {
    private val mLayoutInflater: LayoutInflater
    var categoryList = mutableListOf<Category>()

    init {
        this.mLayoutInflater = LayoutInflater.from(context)
    }

    //mTaskListのサイズ
    override fun getCount(): Int {
        return categoryList.size
    }

    //mTaskListの要素
    override fun getItem(position: Int): Any {
        return categoryList[position]
    }

    //getItemIdメソッドではidを返す
    override fun getItemId(position: Int): Long {
        return categoryList[position].id.toLong()
    }



    //convertViewがnullのときはLayoutInflaterを使ってsimple_list_item_2からViewを取得
    //simple_list_item_2はタイトルとサブタイトルがあるセルです。まずはString型で保持しているtaskListから文字列を取得しタイトルを設定
    //getViewメソッドではTaskのタイトルと時間をTextViewに設定
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //convertViewは現在表示しようとしている行がnullかどうか判定を行っているのは、BaseAdapterにViewを再利用して描画する仕組みがあるため
        val view: View = convertView ?: mLayoutInflater.inflate(R.layout.simple_list_item_1, null)

        val textView1 = view.findViewById<TextView>(R.id.text1)


        textView1.text = categoryList[position].category




        return view
    }
}