package com.sookmyung.hanmundan.data

import com.sookmyung.hanmundan.model.DictionaryResponseDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    @GET("search")
    fun getDictionary(
        @Query("key") key: String,
        @Query("q") word: String,
        @Query("req_type") reqType: String,
    ): Call<DictionaryResponseDTO>
}