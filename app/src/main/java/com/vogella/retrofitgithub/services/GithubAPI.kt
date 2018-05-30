package com.vogella.retrofitgithub.services

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import com.vogella.retrofitgithub.models.GithubIssue
import com.vogella.retrofitgithub.models.GithubRepo

interface GithubAPI {

    @GET("/repositories?per_page=100")
    fun getRepos():
            Single<List<GithubRepo>>

    @GET("/repos/{owner}/{repo}/issues")
    fun getIssues(@Path("owner") owner: String, @Path("repo") repository: String):
            Single<List<GithubIssue>>

    companion object {
        val ENDPOINT = "https://api.github.com"
    }
}