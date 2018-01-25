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
        /**
         * this activity, for easier communication between fragments and activity (buggy a bit using .getActivity on kotlin)
         */
        lateinit var act:AppCompatActivity
        /**
         * tts engine to read content when needed
         */
        var tts: TextToSpeech? = null
        /**
         * call for tts engine to read a string
         * @param string the string to read
         */
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

    /**
     * on create, creates the logic of creating a tab view with multiple fragments set
     */
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
            /**
             * must be implented
             */
            override fun onTabReselected(tab: TabLayout.Tab?)
            {

            }
            /**
             * must be implented
             */
            override fun onTabUnselected(tab: TabLayout.Tab?)
            {

            }
            /**
             * must be implented, if tab is selected we should "move" to that tab
             */
            override fun onTabSelected(tab: TabLayout.Tab?)
            {
                if(tab != null)
                    main_container.currentItem = tab.position
            }

        })
    }

    /**
     * an adapter that implenets the scrollable tabbing logic
     */
    inner class PagerAdapter(fragment_manager:FragmentManager,tabs_count:Int):FragmentStatePagerAdapter(fragment_manager)
    {
        /**
         * the number of tabs we should open, each tab consists a fragment
         */
        private val num_of_tabs:Int = tabs_count

        /**
         * gets a fragment by id for tab by position
         * @param position the position of the fragment that we want to get
         */
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

        /**
         * have to implement this, gets the amount of tabs available
         */
        override fun getCount(): Int = num_of_tabs
    }

    /**
     * should close tts
     */
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
