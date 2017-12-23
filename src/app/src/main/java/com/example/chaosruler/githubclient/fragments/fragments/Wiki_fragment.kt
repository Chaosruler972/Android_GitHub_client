package com.example.chaosruler.githubclient.fragments.fragments



import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.activities.RepoView_Activity
import kotlinx.android.synthetic.main.fragment_wiki_fragment.*
import android.webkit.WebView




class Wiki_fragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater?.inflate(R.layout.fragment_wiki_fragment, container, false)

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
        catch (e:Exception)
        {
            /*
            case we failed to load arguements
             */
            RepoView_Activity.act!!.finish()
        }
        val repo_view_progressbar = RepoView_Activity.act!!.findViewById(R.id.repo_view_progressBar) as ProgressBar
        repo_view_progressbar.visibility = ProgressBar.VISIBLE
        wiki_webview.webViewClient = object :WebViewClient()
        {
            override fun onPageFinished(view: WebView, url: String)
            {
                repo_view_progressbar.visibility = ProgressBar.INVISIBLE
            }
        }
        /*
            build URL
         */
        val url = context.getString(R.string.wiki_url).replace("REPO",repo_name).replace("OWNER",user_name)
        /*
            enable javascript (navigation in wiki page)
         */
        wiki_webview.settings.javaScriptEnabled = true
        /*
            load URL in webview
         */
        wiki_webview.loadUrl(url)


    }

    companion object
    {

        @Suppress("unused")
        fun newInstance(context: Context, user:String, repo:String): Wiki_fragment {
            val bundle = Bundle()
            bundle.putString(context.getString(R.string.user_name_key),user)
            bundle.putString(context.getString(R.string.repo_name_key),repo)
            val fragment =  Wiki_fragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}// Required empty public constructor
