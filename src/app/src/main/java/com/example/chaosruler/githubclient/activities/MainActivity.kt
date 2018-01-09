package com.example.chaosruler.githubclient.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log

import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.fragments.fragments.gists_list.Gists_list
import com.example.chaosruler.githubclient.fragments.fragments.search_for_repo.search_for_repo
import com.example.chaosruler.githubclient.fragments.fragments.search_users_by_location.search_users_by_location
import com.example.chaosruler.githubclient.fragments.fragments.user_fragment.user_fragment
import com.example.chaosruler.githubclient.services.GitHub_remote_service
import com.example.chaosruler.githubclient.services.themer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity()
{

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var act:AppCompatActivity
        var tts: TextToSpeech? = null
        @Suppress("unused")
        fun speakOut(string:String)
        {
            try
            {
                if (tts != null)
                {
                    if (tts!!.isSpeaking)
                    {
                        tts!!.stop()
                    } else {
                        tts!!.speak(string, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                }
            }
            catch (e:Exception)
            {
                Log.d("TTS","Something went wrong")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        setTheme(themer.style(baseContext))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tts = TextToSpeech(this, object :TextToSpeech.OnInitListener
        {
            override fun onInit(status: Int)
            {
                if (status == TextToSpeech.SUCCESS)
                {
                    // set US English as language for tts
                    @Suppress("UNUSED_VARIABLE")
                    val result = tts!!.setLanguage(Locale.US)

                }
                else
                {
                    Log.e("TTS", "Initilization Failed!")
                }
            }

        })
        /*
            kotlin limitation yet
         */
        act = this
        
        /*
            gets support action bar to show tabs
         */
        setSupportActionBar(main_toolbar)
        /*
            hides title in action bar
         */
        supportActionBar!!.hide()
        /*
            tabs amount
         */
        val amount_of_pages = 4
        /*
            inits table adapter for tabs
         */
        main_container.adapter = PagerAdapter(supportFragmentManager,amount_of_pages)
        /*
            links listeners
         */
        main_container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(main_tabs))

        /*
            inits on table change listener, change the fragment on tab move
         */
        main_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener
        {
            override fun onTabReselected(tab: TabLayout.Tab?)
            {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?)
            {

            }

            override fun onTabSelected(tab: TabLayout.Tab?)
            {
                if(tab != null)
                    main_container.currentItem = tab.position
            }

        })
    }

    inner class PagerAdapter(fragment_manager:FragmentManager,tabs_count:Int):FragmentStatePagerAdapter(fragment_manager)
    {
        private val num_of_tabs:Int = tabs_count
        override fun getItem(position: Int): Fragment = when(position)
        {
            /*
                lists the different fragments possible on tab from left to right
             */
            0-> user_fragment.newInstance()
            1-> Gists_list.newInstance(GitHub_remote_service.get_login(),baseContext)
            2-> search_for_repo.newInstance()
            3-> search_users_by_location.newInstance()
            else-> search_for_repo.newInstance()
        }
        /*
            have to implement this
         */
        override fun getCount(): Int = num_of_tabs
    }

    public override fun onDestroy()
    {
        // Shutdown TTS
        if (tts != null)
        {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}
