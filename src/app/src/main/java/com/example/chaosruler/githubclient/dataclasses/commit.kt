@file:Suppress("unused")

package com.example.chaosruler.githubclient.dataclasses

import java.text.SimpleDateFormat
import java.util.*

/**
 * commit dataclass
 */
class commit(
        /**
         * the username that sent the commit
         */
        val username:String,
        /**
         * the message of the commit
         */
        val message:String,
        /**
         * the time the commit was sent in UNIX set
         */
        val time:Long,
        /**
         * the comments on the commit
         */
        val comments:Vector<commit_comment>)
{
    /**
     * convert time from unix to date format
     * @return a date format of commit's time
     */
    fun get_time():String = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault()).format(time)
}
