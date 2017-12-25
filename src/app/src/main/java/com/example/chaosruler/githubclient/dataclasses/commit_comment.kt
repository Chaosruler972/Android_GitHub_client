package com.example.chaosruler.githubclient.dataclasses

import java.text.SimpleDateFormat
import java.util.*

class commit_comment(val username:String, @Suppress("unused") val comment:String, val time:Long)
{
    @Suppress("unused")
    fun get_time():String = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault()).format(time)
}