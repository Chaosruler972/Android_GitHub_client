package com.example.chaosruler.githubclient.dataclasses

import java.util.*

/**
 * a dataclass to represent gists
 */
@Suppress("unused")
class gist
(
        /**
         * the gist name
         */
        val desc:String,
        /**
         * the gists text file themselves
         */
        val vector:Vector<gist_file>
)
{
    /**
     * prints the name of the gist
     */
    override fun toString(): String {
        return desc
    }
}
