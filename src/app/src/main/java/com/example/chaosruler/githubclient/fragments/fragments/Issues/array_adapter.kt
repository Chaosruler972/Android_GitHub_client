package com.example.chaosruler.githubclient.fragments.fragments.Issues

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.dataclasses.issue
import com.example.chaosruler.githubclient.services.themer
import java.util.*
import android.content.Intent
import android.net.Uri
import android.widget.ImageButton
import com.example.chaosruler.githubclient.activities.RepoView_Activity

/**
 * an array adapter that populates the listview inside issues fragment
 */
class array_adapter(context: Context,arr:Vector<issue>):ArrayAdapter<issue>(context, R.layout.item_issue,arr.toTypedArray()) {
    /**
     * inflates a view from list at the item in position specified and sets the view data
     * and initates the view's logic
     * @param convertView the view of this item if recycled
     * @param parent the parent that holds this view (the listview)
     * @param position the position in the array we should take the data from
     * @return a view with initated logic and data
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        @Suppress("NAME_SHADOWING")
        val convertView: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_issue, parent, false)

        val name = themer.get_view_by_id(convertView, R.id.issue_name) as TextView
        val go = themer.get_view_by_id(convertView, R.id.issue_go) as ImageButton
        val issue = getItem(position)

        name.text = issue.title

        go.setOnClickListener {
            val new_intent = Intent(Intent.ACTION_VIEW)
            new_intent.data = Uri.parse(issue.url)
            RepoView_Activity.act!!.startActivity(new_intent)
        }

        return convertView
    }
}