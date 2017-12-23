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


class array_adapter(context: Context,arr:Vector<issue>):ArrayAdapter<issue>(context, R.layout.item_issue,arr.toTypedArray()) {
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