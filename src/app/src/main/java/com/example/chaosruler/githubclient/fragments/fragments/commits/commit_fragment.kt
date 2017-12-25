package com.example.chaosruler.githubclient.fragments.fragments.commits


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.activities.RepoView_Activity
import com.example.chaosruler.githubclient.services.GitHub_remote_service
import kotlinx.android.synthetic.main.fragment_commit_fragment.*


/**
 * A simple [Fragment] subclass.
 */
class commit_fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater?.inflate(R.layout.fragment_commit_fragment, container, false)

    private lateinit var repo_name:String
    private lateinit var user_name:String
    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        /*
            attempt to load repo name and username of the wiki I want to enter into
         */
        try
        {
            repo_name = arguments.getString(getString(R.string.repo_name_key))
            user_name = arguments.getString(getString(R.string.user_name_key))
        }
        catch (e: Exception)
        {
            /*
            case we failed to load arguements
             */
            RepoView_Activity.act!!.finish()
        }
        Thread{
            val commits = GitHub_remote_service.get_commits(repo_name,user_name,context)
            RepoView_Activity.act!!.runOnUiThread {
                try {
                    commit_listview.setAdapter(com.example.chaosruler.githubclient.fragments.fragments.commits.array_adapter(context,commits))
                }
                catch (e:Exception)
                {

                }
            }
        }.start()
    }

    companion object
    {

        @Suppress("unused")
        fun newInstance(context: Context, user:String, repo:String): commit_fragment {
            val bundle = Bundle()
            bundle.putString(context.getString(R.string.user_name_key),user)
            bundle.putString(context.getString(R.string.repo_name_key),repo)
            val fragment =  commit_fragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}// Required empty public constructor
