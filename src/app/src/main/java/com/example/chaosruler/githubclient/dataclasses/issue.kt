package com.example.chaosruler.githubclient.dataclasses


/**
 * issue dataclass
 */
@Suppress("unused")
class issue(
        /**
         * issue title
         */
        val title:String,
        /**
         * issue url (to scan the rest if required)
         */
        val url:String)
{
    /**
     * returns the name of the issue (not unique)
     * @return the name of the issue (not unique)
     */
    override fun toString(): String {
        return title
    }
}