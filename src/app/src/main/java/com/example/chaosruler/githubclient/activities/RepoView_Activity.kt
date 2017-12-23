package com.example.chaosruler.githubclient.activities

import android.annotation.SuppressLint
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.fragments.fragments.Issues.Issues_fragment
import com.example.chaosruler.githubclient.fragments.fragments.Wiki_fragment
import com.example.chaosruler.githubclient.fragments.fragments.repo_files.repo_files_fragment
import com.example.chaosruler.githubclient.fragments.fragments.user_fragment.user_fragment
import com.example.chaosruler.githubclient.services.themer
import com.github.kittinunf.fuel.httpGet

import kotlinx.android.synthetic.main.activity_repo_view.*


class RepoView_Activity : AppCompatActivity() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var act: AppCompatActivity? = null
    }
    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private lateinit var repo_name:String
    private lateinit var user_name:String
    @Suppress("UNUSED_VARIABLE")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        setTheme(themer.style(baseContext))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo_view)
        /*
            save this activiy as a variable, kotlin cut the activity access
            and I would want error handling using that activity instance
         */
        act = this
        /*
            attempts to load the arguements (reponame, username) of the repo I want to show
         */
        try
        {
            repo_name = intent.getStringExtra(getString(R.string.repo_name_key))
            user_name = intent.getStringExtra(getString(R.string.user_name_key))
        }
        catch (e:Exception)
        {
            /*
                must have those variables
             */
            finish()
        }
        /*
            set action bar, used for tableview
         */
        setSupportActionBar(repo_finder_toolbar)
        /*
            adds table view toolbar
         */
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        /*
            hides toolbar, leaving tableview only
         */
        supportActionBar!!.hide()
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*
            amount of fragments before knowing for sure there's a wiki page is 2, third fragment is wiki display
         */
        var amount = 4
        Thread{

            /*
                   connects tableview to sectinPagerAdapter, with a custom one we made to handle fragments
             */
            runOnUiThread {  /*
                  case we don't have a wiki, therefore only 2 fragments
                  we remove the last tab, since we won't be using it - there's no fragment to show there if there isn't a wiki
               */
                mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager, amount)
                /*
                    sets activity "container" to get data from sectionPagerAdapter (aka, inflate the fragment there)
                 */
                repo_container.adapter = mSectionsPagerAdapter

                // Set up the ViewPager with the sections adapter.
                repo_container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(repo_finder_table))
                repo_finder_table.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(repo_container))
            }

        }.start()




    }




    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager, private var amount: Int) : FragmentPagerAdapter(fm)
    {

        override fun getItem(position: Int): Fragment = when(position)
        {
            /*
                first fragment is repo-data showing satiitcs, second fragment is repo files, showing repo files and directries
                third fragment is a wiki fragment, opening a fragment with HTML view
             */
            0->com.example.chaosruler.githubclient.fragments.fragments.repo_data.newInstance(baseContext,user_name,repo_name)
            1->Issues_fragment.newInstance(baseContext,user_name,repo_name)
            2->repo_files_fragment.newInstance(baseContext,user_name,repo_name)
            3->Wiki_fragment.newInstance(baseContext,user_name,repo_name)
            else-> user_fragment.newInstance()
        }


        override fun getCount(): Int {
            return amount
        }
    }


}
