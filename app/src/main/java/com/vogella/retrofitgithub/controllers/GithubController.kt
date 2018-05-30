package com.vogella.retrofitgithub.controllers

import com.vogella.retrofitgithub.services.GithubAPI
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.Credentials
import okhttp3.Interceptor
import java.io.IOException
import com.vogella.retrofitgithub.models.GithubRepo
import com.google.gson.GsonBuilder

class GithubController {

    internal var githubAPI: GithubAPI? = null
    internal var username: String = ""
    internal var password: String = ""

    fun createGithubAPI(): GithubAPI {

        val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(GithubRepo::class.java, GithubRepoDeserializer())
                .create()

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(object : Interceptor {
                    @Throws(IOException::class)
                    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                        val originalRequest = chain.request()

                        val builder = originalRequest.newBuilder().header("Authorization",
                                Credentials.basic(username, password))

                        val newRequest = builder.build()
                        return chain.proceed(newRequest)
                    }
                }).build()

        val retrofit = Retrofit.Builder()
                .baseUrl(GithubAPI.ENDPOINT)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        return retrofit.create(GithubAPI::class.java)
    }
}
