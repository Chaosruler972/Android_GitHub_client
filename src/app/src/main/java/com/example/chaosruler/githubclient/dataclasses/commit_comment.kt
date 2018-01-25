package com.example.chaosruler.githubclient.dataclasses

import java.text.SimpleDateFormat
import java.util.*

/**
 * a dataclass to represent commit's comments
 */
class commit_comment(
        /**
         * the username that pushed the message
         */
        val username:String,
        /**
         * the text of the message
         */
        @Suppress("unused") val comment:String,
        /**
         * when was the message sent
         */
        val time:Long)
{
    /**
     * convert time from unix to date format
     * @return a date format of commit's time
     */
    @Suppress("unused")
    fun get_time():String = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault()).format(time)
}