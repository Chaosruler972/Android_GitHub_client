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


class Issues_fragment : Fragment()
{


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater?.inflate(R.layout.fragment_issues, container, false)

    private lateinit var repo_name:String
    private lateinit var user_name:String
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
