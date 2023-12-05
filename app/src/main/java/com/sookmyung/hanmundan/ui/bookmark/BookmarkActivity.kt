package com.sookmyung.hanmundan.ui.bookmark

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.sookmyung.hanmundan.R

class BookmarkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)
        val item = ArrayList<Data>()

        item.add(Data("하루", "하루하루 지나가면 바람결에 실려오는 나의 꿈들이 나를 채우고"))
        item.add(Data("하늘", "하늘을 날아가는 기분이야"))
        item.add(Data("하루살이", "이제 난 하루살이 하루하루 내일도 잃어버린 채"))

        val recyclerView: RecyclerView = findViewById(R.id.rv_bookmark_list)
        val adapter = RecyclerViewAdapter(item)
        recyclerView.adapter = adapter
    }
}