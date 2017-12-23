package com.example.chaosruler.githubclient.fragments.fragments.repo_files


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.ProgressBar
import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.activities.RepoView_Activity
import com.example.chaosruler.githubclient.services.GitHub_remote_service
import kotlinx.android.synthetic.main.fragment_repo_files.*
import org.eclipse.egit.github.core.RepositoryContents
import org.eclipse.egit.github.core.service.ContentsService
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class repo_files_fragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater?.inflate(R.layout.fragment_repo_files, container, false)

    private val stack: Stack<ListAdapter> = Stack()
    lateinit var repo_name:String
    lateinit var user_name:String
    private lateinit var repo_url:String
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        /*
            attempts to load arguements
         */
        try
        {
            repo_name = arguments.getString(getString(R.string.repo_name_key))
            user_name = arguments.getString(getString(R.string.user_name_key))
        }
        catch (e:Exception)
        {
            /*
            upon failure
             */
            RepoView_Activity.act!!.finish()
        }
        val repo_view_progressbar = RepoView_Activity.act!!.findViewById(R.id.repo_view_progressBar) as ProgressBar
        repo_view_progressbar.visibility = ProgressBar.VISIBLE
        Thread{
            /*
            get root directory content
             */
            repo_url = GitHub_remote_service.get_repo_url(repo_name,user_name)!!
            val contentService = GitHub_remote_service.get_ContentService(repo_name,user_name)
            Log.d("Files_fragment","CS is " + contentService.toString())
            val contents:MutableList<RepositoryContents>?
            try
            {
                contents = GitHub_remote_service.get_content(repo_name,user_name)
            }
            catch (e:Exception)
            {
                Log.d("Repo Files","No contents")
                return@Thread
            }
            RepoView_Activity.act!!.runOnUiThread {
                try {
                    repo_files_list.adapter = repo_files_arrayadapter(context, contents!!.toTypedArray(), contentService, this)
                }
                catch (e:Exception)
                {
                    Log.d("Repo files","Weird kotlin error")
                }
                    /*
                    }
                        disable up button
                     */
                repo_files_up.isEnabled = false
                /*
                    up button replaces this adapter with stacks adapter, eventually moving "up" in memory, if there is nothing pushed we are at root directory
                 */
                repo_files_up.setOnClickListener{if(stack.empty())
                    return@setOnClickListener
                    else
                    repo_files_list.adapter = stack.pop()
                    /*
                        if after poping we emptied the stack, we should disable the button
                     */
                    if(stack.isEmpty())
                        repo_files_up.isEnabled = false
                }

                repo_view_progressbar.visibility = ProgressBar.INVISIBLE
            }
        }.start()
    }

    /*
        ideally enter_folder "reloads" the listview with a new directory of content
        what the algo does, is simply "save" the previous "folder" on a stack (therefre, last input is saved)
        and open a new one, when "up" button is pressed, the stacked adapter is reloaded
     */
    fun enter_folder(path:String,contentsService: ContentsService)
    {
        Thread{
            /*
                in this case, we are pushing last directory up in memory in stack
             */
            stack.push(repo_files_list.adapter)
            /*
                getting files content
             */
            val contents = GitHub_remote_service.get_content(repo_name,user_name,path)
            /*
                updating contents
             */
            RepoView_Activity.act!!.runOnUiThread { repo_files_list.adapter = repo_files_arrayadapter(context,contents!!.toTypedArray(),contentsService,this)
            repo_files_up.isEnabled = true
            }
        }.start()
    }


    /*
      generator
   */
    companion object
    {

        fun newInstance(context: Context, user:String, repo:String): repo_files_fragment {
            val bundle = Bundle()
            bundle.putString(context.getString(R.string.user_name_key),user)
            bundle.putString(context.getString(R.string.repo_name_key),repo)
            val fragment =  repo_files_fragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}// Required empty public constructor
