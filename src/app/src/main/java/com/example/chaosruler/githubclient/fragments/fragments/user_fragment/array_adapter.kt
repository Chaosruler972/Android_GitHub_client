package com.example.chaosruler.githubclient.fragments.fragments.user_fragment

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.activities.RepoView_Activity
import com.example.chaosruler.githubclient.dataclasses.repo
import com.example.chaosruler.githubclient.services.themer
import java.util.*


/**
 * an array adapter that populates the listview inside user_fragment
 */
class array_adapter(context: Context,arr:Vector<repo>) : ArrayAdapter<repo>(context, R.layout.item_user_repos,arr.toTypedArray())
{

    /**
     * inflates a view from list at the item in position specified and sets the view data
     * and initates the view's logic
     * @param convertView the view of this item if recycled
     * @param parent the parent that holds this view (the listview)
     * @param position the position in the array we should take the data from
     * @return a view with initated logic and data
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        @Suppress("NAME_SHADOWING")
        val convertView:View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_user_repos,parent,false)

        /*
            ui objects
         */
        val name = themer.get_view_by_id(convertView,R.id.item_user_repo_name) as TextView
        val langauge = themer.get_view_by_id(convertView,R.id.item_user_repo_language) as TextView
        val created_at = themer.get_view_by_id(convertView,R.id.item_user_repo_created_at) as TextView
        val is_private = themer.get_view_by_id(convertView,R.id.item_user_repo_private) as CheckBox
        val goto = themer.get_view_by_id(convertView,R.id.item_user_goto) as ImageButton

        /*
            repo object
         */
        val repo = getItem(position)

        /*
            updat plain data from repo
         */
        name.text = repo.name
        langauge.text = repo.language
        created_at.text = repo.get_created_at()
        is_private.isChecked = repo.is_private
        /*
            checkbox is for visulation only, therefore disabled
         */
        is_private.isEnabled = false
        /*
            case we want to "enter" a repository, opening an activity designed for that, sending
            as arguements username and repo name
         */
        goto.setOnClickListener{ val new_intent = Intent(context,RepoView_Activity::class.java)
            new_intent.putExtra(context.getString(R.string.user_name_key),repo.owner)
            new_intent.putExtra(context.getString(R.string.repo_name_key),repo.name)
            context.startActivity(new_intent)}

        return convertView
    }
}