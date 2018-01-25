package com.example.chaosruler.githubclient.dataclasses

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.example.chaosruler.githubclient.services.load_Image_from_URL

/**
 * dataclass to represent a user when searched
 */
class search_user(
        /**
         * the username
         */
        val username:String,
        /**
         * the user's phone number
         */
        var phone:String?,
        /**
         * the user's avatar URL
         */
        avatar:String,
        /**
         * the users email to query more data
         */
        val url:String,
        /**
         * conext to load the image url to get the data
         */
        context: Context)
{
    /**
     * bitmap to hold users image
     */
    var bitmap: Bitmap? = null

    /**
     * automaticily downloads user image and sets it into bitmap
     */
    init
    {
        Thread {
            val imgview = ImageView(context)
            val status = load_Image_from_URL(avatar, imgview).execute()
            bitmap = status.get()
        }.start()
    }
}