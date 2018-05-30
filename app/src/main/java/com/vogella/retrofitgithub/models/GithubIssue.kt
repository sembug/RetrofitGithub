package com.vogella.retrofitgithub.models

import com.google.gson.annotations.SerializedName

class GithubIssue {

    internal var id: String? = null
    internal var title: String? = null
    internal var comments_url: String? = null

    @SerializedName("body")
    internal var comment: String? = null

    override fun toString(): String {
        return "$id - $title"
    }
}