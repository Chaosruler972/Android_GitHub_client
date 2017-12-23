package com.example.chaosruler.githubclient.fragments.fragments.gists_list

import java.util.HashMap

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.activities.display_file_activity
import com.example.chaosruler.githubclient.dataclasses.gist_file
import com.example.chaosruler.githubclient.services.themer

class expandable_arrayadapter(private val _context: Context, private val _listDataHeader: List<String> // header titles
                            ,
        // child data in format of header title, child title
                            private val _listDataChild: HashMap<String, List<gist_file>>) : BaseExpandableListAdapter() {

    override fun getChild(groupPosition: Int, childPosititon: Int): gist_file? {
        return this._listDataChild[this._listDataHeader[groupPosition]]?.get(childPosititon)
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
        val convertView:View = convertView ?: LayoutInflater.from(_context).inflate(R.layout.item_gist,parent,false)

        val name = themer.get_view_by_id(convertView,R.id.item_gist_name) as TextView
        val goto = themer.get_view_by_id(convertView,R.id.item_gist_read) as ImageButton

        val item = getChild(groupPosition,childPosition) as gist_file

        name.text = item.filename

        goto.setOnClickListener {
            val new_intent = Intent(_context, display_file_activity::class.java)
            new_intent.putExtra(_context.getString(R.string.file_key),item.data)
            _context.startActivity(new_intent)
        }


        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return this._listDataChild[this._listDataHeader[groupPosition]]?.size!!
    }

    override fun getGroup(groupPosition: Int): Any {
        return this._listDataHeader[groupPosition]
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
        val convertView:View = convertView ?: LayoutInflater.from(_context).inflate(R.layout.gist_header,parent,false)

        val name = themer.get_view_by_id(convertView,R.id.item_gist_header) as TextView

        val item = getGroup(groupPosition) as String
        name.text = item

        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}