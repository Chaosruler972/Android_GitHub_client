package com.example.chaosruler.githubclient.fragments.fragments



import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar

import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.activities.RepoView_Activity
import com.example.chaosruler.githubclient.dataclasses.repo
import com.example.chaosruler.githubclient.services.GitHub_remote_service
import kotlinx.android.synthetic.main.fragment_repo_data.*


/**
 * a repo data fragment, shows the data of a repo (satistics and etcetra)
 */
class repo_data : Fragment() {


    /**
     * inflates the view
     * @param container the container of this fragment (activity view holder)
     * @param inflater the inflater in chrage of infalting this view
     * @param savedInstanceState the last state of this fragment
     * @return a view of this fragment
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater?.inflate(R.layout.fragment_repo_data, container, false)

    /**
     * is responisble of calling in a threadded way the function that loads and queries the data
     * required to show on this fragment, call is threadded and non-blocked
     * @param savedInstanceState the last state of this fragment
     * @exception IllegalStateException if not called from getInstance() and manually constructed instead and arguements weren't set
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val repo_name = arguments.getString(getString(R.string.repo_name_key))
        val user_name = arguments.getString(getString(R.string.user_name_key))
        val repo_view_progressbar = RepoView_Activity.act!!.findViewById(R.id.repo_view_progressBar) as ProgressBar
        repo_view_progressbar.visibility = ProgressBar.VISIBLE
        Thread{
            /*
            gets a repo representation
             */
            val repo:repo?
            try {
                repo=GitHub_remote_service.get_repo_by_id_and_name(repo_name,user_name)
            }
            catch (e:Exception)
            {
                RepoView_Activity.act!!.runOnUiThread { repo_view_progressbar.visibility = ProgressBar.INVISIBLE
                }
                return@Thread
            }

            RepoView_Activity.act!!.runOnUiThread {
                try
                {
                    /*
                        Builds datastrings using the values from repo representation
                     */
                    repo_data_name.text = repo_data_name.text.toString().replace("name", repo.name)
                    repo_data_desc.text = repo_data_desc.text.toString().replace("desc", repo.description)
                    repo_data_issues.text = repo_data_issues.text.toString().replace("issues", repo.issues.toString())
                    repo_data_create.text = repo_data_create.text.toString().replace("date", repo.get_created_at())
                    repo_data_last_update.text = repo_data_last_update.text.toString().replace("update", repo.get_last_update())
                    if (repo.is_forked)
                        repo_data_isforked.text = repo_data_isforked.text.toString().replace("NOT", "")
                    repo_data_forks.text = repo_data_forks.text.toString().replace("forks", repo.amount_of_forks.toString())
                    repo_data_owner.text = repo_data_owner.text.toString().replace("owner", user_name)
                    repo_data_language.text = repo_data_language.text.toString().replace("language", repo.language)
                }
                catch (e:Exception)
                {
                    Log.d("Repo data","Something went wrong")
                }
                repo_view_progressbar.visibility = ProgressBar.INVISIBLE
            }
        }.start()
    }


    companion object
    {

        /**
         *  generator in a singleton-style of way, only this can be multi-instanced
         *  @param context the context that is required to generate keys from strings.xml
         *  @param repo the repo name that we want to scan
         *  @param user the user that has that repo
         *  @return a instance of this fragment with that data sent
         */
        @Suppress("unused")
        fun newInstance(context: Context,user:String,repo:String): repo_data {
            val bundle = Bundle()
            bundle.putString(context.getString(R.string.user_name_key),user)
            bundle.putString(context.getString(R.string.repo_name_key),repo)
            val fragment =  repo_data()
            fragment.arguments = bundle
            return fragment
        }
    }

}// Required empty public constructor
