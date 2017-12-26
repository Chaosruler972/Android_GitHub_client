package com.example.chaosruler.githubclient.fragments.fragments.search_users_by_location

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.activities.MainActivity
import com.example.chaosruler.githubclient.dataclasses.search_user
import com.example.chaosruler.githubclient.services.load_Image_from_URL
import com.example.chaosruler.githubclient.services.themer
import java.util.*
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.util.Log


class array_adapter(context: Context, arr: Vector<search_user>): ArrayAdapter<search_user>(context, R.layout.item_search_user,arr.toTypedArray()) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        @Suppress("NAME_SHADOWING")
        val convertView: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_search_user, parent, false)

        val avy = themer.get_view_by_id(convertView,R.id.search_avatar) as ImageView
        val phone = themer.get_view_by_id(convertView,R.id.search_phone) as TextView
        val name = themer.get_view_by_id(convertView, R.id.search_username) as TextView
        val go = themer.get_view_by_id(convertView, R.id.search_goto) as ImageButton

        val user = getItem(position)

        name.text = user.username
        if(user.phone == null)
        {
            phone.text = context.getString(R.string.no_phone_number)
            phone.isEnabled = false
        }
        else
        {
            phone.text = user.phone
        }
        if(user.bitmap!=null)
            avy.setImageBitmap(user.bitmap)
        else
        {
            Thread{
                while (user.bitmap==null)
                {
                    try
                    {
                        Thread.sleep(2000)
                    }
                    catch (e:InterruptedException)
                    {
                        Log.d("Search users adapter","Woke up, try to get image")
                    }
                }
                MainActivity.act.runOnUiThread {
                    try {
                        avy.setImageBitmap(user.bitmap)
                    }
                    catch (e:Exception)
                    {

                    }
                }
            }.start()
        }


        go.setOnClickListener {
            val new_intent = Intent(Intent.ACTION_VIEW)
            new_intent.data = Uri.parse(user.url)
            MainActivity.act.startActivity(new_intent)
        }
        return convertView
    }
}