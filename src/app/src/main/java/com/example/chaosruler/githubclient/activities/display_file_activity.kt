package com.example.chaosruler.githubclient.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.services.themer
import kotlinx.android.synthetic.main.activity_display_file.*

class display_file_activity : Activity() {

    private lateinit var data:String
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
            data = intent.getStringExtra(getString(R.string.file_key))
        }
        catch (e:Exception)
        {
            /*
                case data was not sent: therefore activity is empty, we should close with errr message
             */
            data = ""
            Toast.makeText(baseContext,baseContext.getString(R.string.cant_display_data), Toast.LENGTH_SHORT).show()
            finish()
        }
        /*
            case data exists, we should display it
         */
        display_file_data.text = data
    }
}
