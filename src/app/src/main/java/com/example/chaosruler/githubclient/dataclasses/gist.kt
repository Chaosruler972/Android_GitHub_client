package com.example.chaosruler.githubclient.dataclasses

import java.util.*

@Suppress("unused")
class gist
(
        val desc:String,
        val vector:Vector<gist_file>
)
{
    override fun toString(): String {
        return desc
    }
}
