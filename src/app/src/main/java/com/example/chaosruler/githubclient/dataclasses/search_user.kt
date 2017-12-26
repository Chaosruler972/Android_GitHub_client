package com.example.chaosruler.githubclient.dataclasses

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.example.chaosruler.githubclient.services.load_Image_from_URL


class search_user(val username:String,var phone:String?,avatar:String, val url:String, context: Context)
{
    var bitmap: Bitmap? = null
    init
    {
        Thread {
            val imgview = ImageView(context)
            val status = load_Image_from_URL(avatar, imgview).execute()
            bitmap = status.get()
        }.start()
    }
}