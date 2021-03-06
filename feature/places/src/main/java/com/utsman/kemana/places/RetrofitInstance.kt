/*
 * Copyright 2019 Muhammad Utsman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.utsman.kemana.places

import io.reactivex.Flowable
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface RetrofitInstance {

    @GET("geocoding/v5/mapbox.places/{query}.json")
    fun getPlaces(
        @Path("query") query: String,
        @Query("bbox", encoded = false) bbox: String,
        @Query("access_token") token: String
    ): Flowable<Places>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/directions/v5/mapbox/driving-traffic")
    fun getRoutes(
        @Body body: RequestBody,
        @Query("exclude") exclude: String,
        @Query("access_token") token: String
    ): Flowable<Route>

    @GET("/geocoding/v5/mapbox.places/{lon},{lat}.json")
    fun getAddress(
        @Path("lon") lon: Double,
        @Path("lat") lat: Double,
        @Query("access_token") token: String
    ): Flowable<Places>

    companion object{
        fun create(): RetrofitInstance {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build()

            val retrofit =  Retrofit.Builder()
                .baseUrl("https://api.mapbox.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()

            return retrofit.create(RetrofitInstance::class.java)
        }
    }
}