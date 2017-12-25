package com.example.chaosruler.githubclient.fragments.fragments.commits

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.activities.RepoView_Activity
import com.example.chaosruler.githubclient.dataclasses.commit
import com.example.chaosruler.githubclient.dataclasses.commit_comment
import com.example.chaosruler.githubclient.services.themer
import java.util.*


class array_adapter (private val _context: Context, private val _listDataHeader: Vector<commit> // header titles

                     ) : BaseExpandableListAdapter()
{
    override fun getChild(groupPosition: Int, childPosititon: Int): commit_comment? {
        return this._listDataHeader.elementAt(groupPosition).comments.elementAt(childPosititon)
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    /*
        this is the child view - the gist file
     */
    override fun getChildView(groupPosition: Int, childPosition: Int,
                              isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        @Suppress("NAME_SHADOWING")
        val convertView:View = convertView ?: LayoutInflater.from(_context).inflate(
                R.layout.item_comment_message,parent,false)

        val name = themer.get_view_by_id(convertView,R.id.comment_user) as TextView
        val message = themer.get_view_by_id(convertView,R.id.comment_message) as TextView
        val time = themer.get_view_by_id(convertView,R.id.comment_time) as TextView
        val play = themer.get_view_by_id(convertView,R.id.comment_play) as ImageButton

        val item = getChild(groupPosition,childPosition) as commit_comment

        name.text = item.username
        message.text = item.comment
        time.text = item.get_time()

        play.setOnClickListener{
                RepoView_Activity.speakOut(item.comment)
        }


        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return this._listDataHeader.elementAt(groupPosition).comments.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return this._listDataHeader.elementAt(groupPosition)
    }

    override fun getGroupCount(): Int {
        return this._listDataHeader.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    /*
        this is the father view - the list header
     */
    override fun getGroupView(groupPosition: Int, isExpanded: Boolean,
                              convertView: View?, parent: ViewGroup): View {
        @Suppress("NAME_SHADOWING")
        val convertView:View = convertView ?: LayoutInflater.from(_context).inflate(R.layout.item_commit,parent,false)

        val name = themer.get_view_by_id(convertView,R.id.commit_user) as TextView
        val message = themer.get_view_by_id(convertView,R.id.commit_message) as TextView
        val time = themer.get_view_by_id(convertView,R.id.commit_time) as TextView

        val item = getGroup(groupPosition) as commit
        name.text = item.username
        message.text = item.message
        time.text = item.get_time()

        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}