package com.example.chaosruler.githubclient.dataclasses


@Suppress("unused")
class issue(val title:String, val url:String)
{
    override fun toString(): String {
        return title
    }
}