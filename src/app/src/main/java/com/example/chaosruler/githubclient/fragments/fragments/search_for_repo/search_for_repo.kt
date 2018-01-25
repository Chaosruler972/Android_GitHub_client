package com.example.chaosruler.githubclient.fragments.fragments.search_for_repo


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.activities.MainActivity
import com.example.chaosruler.githubclient.fragments.fragments.user_fragment.array_adapter
import com.example.chaosruler.githubclient.services.GitHub_remote_service
import kotlinx.android.synthetic.main.fragment_search_for_repo.*
import android.widget.ProgressBar
import android.view.inputmethod.InputMethodManager

/**
 * repo query fragment
 */
class search_for_repo : Fragment() {


    /**
     * inflates the view
     * @param container the container of this fragment (activity view holder)
     * @param inflater the inflater in chrage of infalting this view
     * @param savedInstanceState the last state of this fragment
     * @return a view of this fragment
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater?.inflate(R.layout.fragment_search_for_repo, container, false)

    /**
     * initates a list with q logic for query Github
     * @param savedInstanceState the last state of the fragment
     */
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        search_repo_btn.setOnClickListener {
            if(search_repo_text.text.toString().isEmpty())
            {
                Toast.makeText(context,getString(R.string.empty_string_criteria),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            search_repo_btn.isEnabled = false
            search_repo_text.clearFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(search_repo_text.windowToken, 0)
            val main_progressbar = MainActivity.act.findViewById(R.id.main_progressbar) as ProgressBar
            main_progressbar.visibility = ProgressBar.VISIBLE
            Thread{
                val repos = GitHub_remote_service.search_for_repos(search_repo_text.text.toString(),1,context)
                MainActivity.act.runOnUiThread {
                    main_progressbar.visibility = ProgressBar.INVISIBLE
                    try {
                        search_repos_listview.adapter = array_adapter(context, repos)
                    }
                    catch (e:Exception)
                    {
                        Log.d("Search Repos","Oi! Kotlin!")
                    }
                    search_repo_btn.isEnabled = true
                }

            }.start()
        }
    }
    companion object
    {
        /**
         *  generator in a singleton-style of way, only this can be multi-instanced
         *  @return a instance of this fragment with that data sent
         */
        @Suppress("unused")
        fun newInstance(): search_for_repo {
           return search_for_repo()
        }
    }

}// Required empty public constructor
