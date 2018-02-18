package com.example.chaosruler.githubclient.fragments.fragments.gists_list



import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.activities.MainActivity
import com.example.chaosruler.githubclient.dataclasses.gist
import com.example.chaosruler.githubclient.dataclasses.gist_file
import com.example.chaosruler.githubclient.services.GitHub_remote_service
import kotlinx.android.synthetic.main.fragment_gists_list.*
import java.util.*
import kotlin.collections.HashMap


/**
 * a fragment that loads a user's gists
 */
class Gists_list : Fragment() {

    /**
     * inflates the view
     * @param container the container of this fragment (activity view holder)
     * @param inflater the inflater in chrage of infalting this view
     * @param savedInstanceState the last state of this fragment
     * @return a view of this fragment
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater?.inflate(R.layout.fragment_gists_list, container, false)

    /**
     * the username that we want to load his gists
     */
    private lateinit var username:String
    /**
     * builds the adapter that scans and loads a user gists
     * if no reponame\username was sent
     * @param savedInstanceState the last state of the fragment
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        try {
            username = arguments.getString(getString(R.string.user_name_key))
        } catch (e: Exception) {
            /*
            case we failed to load arguements
             */
            MainActivity.act.finish()
        }
        Thread {
            /*
                get all gists
             */
            val vector: Vector<gist> = GitHub_remote_service.get_gists(GitHub_remote_service.get_username(), context, 1)
            /*
                divide into headers and data for expandable listview
             */
            Log.d("Gists: ","Amount of gists loaded: " + vector.size.toString())
            val headers: Vector<String> = Vector()
            val map: HashMap<String, List<gist_file>> = HashMap()
            vector.forEach {
                /*
                    input data per header to a vector into a hashtable
                 */
                headers.addElement(it.desc)
                map.put(it.desc, it.vector.toList())
            }
            /*
                update expandable listview
             */
            MainActivity.act.runOnUiThread {
                try {
                    gists_listview.setAdapter(expandable_arrayadapter(context, headers.toList(), map))
                } catch (e: Exception) {
                    Log.d("Gists", "This is getting tiring..")
                }
            }
        }.start()
    }

    companion object
    {
        /**
         *  generator in a singleton-style of way, only this can be multi-instanced
         *  @param context the context that is required to generate keys from strings.xml
         *  @param user the user that has those gists
         *  @return a instance of this fragment with that data sent
         */
        @Suppress("unused")
        fun newInstance(user:String,context: Context): Gists_list
        {
            val bundle = Bundle()
            val frag =  Gists_list()
            bundle.putString(context.getString(R.string.user_name_key),user)
            frag.arguments = bundle
            return frag
        }
    }

}// Required empty public constructor
