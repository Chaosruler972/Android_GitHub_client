@file:Suppress("unused")

package com.example.chaosruler.githubclient.dataclasses

import java.text.SimpleDateFormat
import java.util.*


class commit(val username:String,val message:String,val time:Long,val comments:Vector<commit_comment>)
{
    fun get_time():String = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault()).format(time)
}
