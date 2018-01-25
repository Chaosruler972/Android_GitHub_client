package com.example.chaosruler.githubclient.dataclasses

/**
 * Gist file itself representation (dataclass)
 */
class gist_file(
        /**
         * the filename of the gist
         */
        val filename:String,
        /**
         * the data inside the gist (the text itself)
         */
        val data:String,
        /**
         * the language it was written on (C/Java/English?!?)
         */
        @Suppress("unused") val language:String)