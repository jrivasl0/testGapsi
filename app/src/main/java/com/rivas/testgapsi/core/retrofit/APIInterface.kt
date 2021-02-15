package com.rivas.testgapsi.core.retrofit

import com.rivas.testgapsi.core.models.SearchResultModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


internal interface APIInterface {
    @Headers("X-IBM-Client-Id: adb8204d-d574-4394-8c1a-53226a40876e")
    @GET("/demo-gapsi/search")
    fun getSearch(@Query("query") query: String): Call<SearchResultModel>
}