package com.example.chaosruler.githubclient.activities

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.services.themer
import kotlinx.android.synthetic.main.activity_display_file.*
import uk.co.senab.photoview.PhotoViewAttacher



class display_file_activity : Activity() {

    private lateinit var data:ByteArray
    override fun onCreate(savedInstanceState: Bundle?)
    {
        setTheme(themer.style(baseContext))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_file)
        /*
            attempts to fetch data if sent
         */


        try
        {
            data = intent.getByteArrayExtra(getString(R.string.file_key))
        }
        catch (e:Exception)
        {
            /*
                case data was not sent: therefore activity is empty, we should close with errr message
             */
            data=ByteArray(1)
            Toast.makeText(baseContext,baseContext.getString(R.string.cant_display_data), Toast.LENGTH_SHORT).show()
            finish()
        }
        val is_picture = themer.is_image(data)
        /*
            case data exists, we should display it
         */
        if(is_picture)
        {
            display_file_play.visibility = ImageButton.INVISIBLE
            display_file_parent.removeView(display_file_data)
            val imageview = ImageView(baseContext)
            imageview.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
            imageview.setImageBitmap(BitmapFactory.decodeByteArray(data,0,data.size))
            display_file_parent.addView(imageview)
            val pAttacher =  PhotoViewAttacher(imageview)
            pAttacher.update()

        }
        else
        {
            display_file_data.text = String(data)
            display_file_play.setOnClickListener {
                MainActivity.speakOut(String(data))
            }
        }

    }
}
