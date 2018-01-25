package com.example.chaosruler.githubclient.fragments.fragments.Issues




import android.annotation.SuppressLint
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
import com.example.chaosruler.githubclient.services.GitHub_remote_service
import kotlinx.android.synthetic.main.fragment_issues.*


/**
 * a fragment that displays issue of a specified repo
 */
class Issues_fragment : Fragment()
{

    /**
     * inflates the view
     * @param container the container of this fragment (activity view holder)
     * @param inflater the inflater in chrage of infalting this view
     * @param savedInstanceState the last state of this fragment
     * @return a view of this fragment
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater?.inflate(R.layout.fragment_issues, container, false)

    /**
     * the reponame that we want to scan it's content
     */
    private lateinit var repo_name:String
    /**
     * the username that has the repo that we want to scan its content
     */
    private lateinit var user_name:String
    /**
     * builds the adapter that scans and loads a repo's issues
     * if no reponame\username was sent, we exit
     * @param savedInstanceState the last state of the fragment
     */
    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /*
            attempt to load repo name and username of the wiki I want to enter into
         */
        try {
            repo_name = arguments.getString(getString(R.string.repo_name_key))
            user_name = arguments.getString(getString(R.string.user_name_key))
        } catch (e: Exception) {
            /*
            case we failed to load arguements
             */
            RepoView_Activity.act!!.finish()
        }
        val repo_view_progressbar = RepoView_Activity.act!!.findViewById(R.id.repo_view_progressBar) as ProgressBar
        repo_view_progressbar.visibility = ProgressBar.VISIBLE
        Thread{
            val vector = GitHub_remote_service.get_issues(repo_name,user_name,context)
            RepoView_Activity.act!!.runOnUiThread {
                repo_view_progressbar.visibility = ProgressBar.INVISIBLE
                try {
                    issues_listview.adapter = array_adapter(context, vector)
                }
                catch (e:Exception)
                {
                    Log.d("Issues fragment","More weird issues from kotlin")
                }
            }
        }.start()
        repo_view_progressbar.visibility = ProgressBar.INVISIBLE
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
        fun newInstance(context: Context, user:String, repo:String): Issues_fragment {
            val bundle = Bundle()
            bundle.putString(context.getString(R.string.user_name_key),user)
            bundle.putString(context.getString(R.string.repo_name_key),repo)
            val fragment = Issues_fragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}// Required empty public constructor
