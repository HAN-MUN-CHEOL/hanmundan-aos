package com.sookmyung.hanmundan.ui.calender

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sookmyung.hanmundan.model.DailyRecord

class CalenderViewModel : ViewModel() {
    private val _dailyRecord =
        MutableLiveData<DailyRecord>(DailyRecord("실험", "이 데이터는 순전히 실험용입니다.", "2023.10.12", false))
    val dailyRecord: LiveData<DailyRecord> = _dailyRecord

    private fun getDailyRecord() {
        //TODO(날짜에 맞는 DailyRecord 가져올 것)
    }

    fun updateBookmark(){
        val dr = _dailyRecord.value?.copy(bookmark = !(_dailyRecord.value!!.bookmark))
        _dailyRecord.value = dr!!
    }
}