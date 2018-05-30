package com.vogella.retrofitgithub.controllers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import com.vogella.retrofitgithub.models.GithubRepo

class GithubRepoDeserializer : JsonDeserializer<GithubRepo>  {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): GithubRepo {
        val githubRepo = GithubRepo()

        val repoJsonObject = json!!.getAsJsonObject()
        githubRepo.name = repoJsonObject.get("name").asString
        githubRepo.url = repoJsonObject.get("url").asString

        val ownerJsonElement = repoJsonObject.get("owner")
        val ownerJsonObject = ownerJsonElement.asJsonObject
        githubRepo.owner = ownerJsonObject.get("login").asString

        return githubRepo
    }
}