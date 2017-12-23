package com.example.chaosruler.githubclient.dataclasses


import java.text.SimpleDateFormat
import java.util.*


@Suppress( "unused")
class repo(
        val name: String,
        val description: String,
        val URL: String,
        val language: String,
        val is_forked: Boolean,
        val amount_of_forks: Int,
        val issues: Int,
        private val created_at: Long,
        private val last_update: Long,
        val is_private: Boolean,
        val id: Long,
        val owner: String,
        val has_wiki: Boolean

)
{
    /*
        no context therefore no resources, have to manually type that, I could "hax" my way through it
        and send context to ctor, but its pointless
     */
    fun get_last_update():String = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault()).format(last_update)
    fun get_created_at():String = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault()).format(created_at)
}