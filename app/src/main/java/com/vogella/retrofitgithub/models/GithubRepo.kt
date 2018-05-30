package com.vogella.retrofitgithub.models

class GithubRepo {
    internal var name: String? = null
    internal var owner: String? = null
    internal var url: String? = null

    override fun toString(): String {
        return "$name $url"
    }
}