package com.example.chaosruler.githubclient.fragments.fragments.search_users_by_location


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chaosruler.githubclient.R


/**
 * A simple [Fragment] subclass.
 */
class search_users_by_location : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater?.inflate(R.layout.fragment_search_users_by_location, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)


    }
    companion object
    {

        @Suppress("unused")
        fun newInstance(): search_users_by_location {
            return search_users_by_location()
        }
    }

}// Required empty public constructor
