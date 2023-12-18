package com.sookmyung.hanmundan.ui.calender

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sookmyung.hanmundan.model.DailyRecord

class CalenderViewModel : ViewModel() {
    private val _dailyRecord =
        MutableLiveData<DailyRecord>()
    val dailyRecord: LiveData<DailyRecord> = _dailyRecord

    fun updateDailyRecord(dr: DailyRecord){
        _dailyRecord.value = dr
    }
}