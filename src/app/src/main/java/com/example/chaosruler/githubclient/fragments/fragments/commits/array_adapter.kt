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

/**
 * an expendable listview array adapter
 */
class array_adapter (private val _context: Context,
                     /**
                      * header titles and data on the same item!
                      */
                     private val _listDataHeader: Vector<commit> // header titles

                     ) : BaseExpandableListAdapter()
{
    /**
     * get the child data at position childPosition from group groupPosition
     * @param childPosititon the position of the child in the group
     * @param groupPosition the position of the group in the entire list
     * @return the file if it exists, else null
     */
    override fun getChild(groupPosition: Int, childPosititon: Int): commit_comment? {
        return this._listDataHeader.elementAt(groupPosition).comments.elementAt(childPosititon)
    }
    /**
     * creates an ID from child positions in the listview
     * @param childPosition the position of the child in the group
     * @param groupPosition the position of the group in the entire list
     * @return the id of the specified child
     */
    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    /**
     *     this is the child view - the gist file, generator
     * @param childPosition the position of the child in the group
     * @param convertView the view if recycled, else null
     * @param groupPosition the group position in the entire list
     * @param isLastChild if the child is last on position
     * @param parent the parent that holds this child (group view)
     * @return the view initated with logic
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
    /**
     * how many children in group n?
     * @param groupPosition the group position
     * @return the amount of children in that group
     */
    override fun getChildrenCount(groupPosition: Int): Int {
        return this._listDataHeader.elementAt(groupPosition).comments.size
    }
    /**
     * get the group at position n
     * @param groupPosition the position of the group
     * @return the group item
     */
    override fun getGroup(groupPosition: Int): Any {
        return this._listDataHeader.elementAt(groupPosition)
    }
    /**
     * how many groups we got?
     * @return the amount of groups we got
     */
    override fun getGroupCount(): Int {
        return this._listDataHeader.size
    }
    /**
     * a unique id for group by position
     * @param  groupPosition the location of the group
     * @return a unique id of a group specified by location
     */
    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    /**
     *  this is the father view - the list header
     *  @param convertView the recycled view if exists
     *  @param groupPosition the position of the group in the list
     *  @param isExpanded if group should be expended (got items?)
     *  @param parent the parent that holds this group (listview)
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
    /**
     * API function that I had to override, if all IDS are stable
     * @return always false
     */
    override fun hasStableIds(): Boolean {
        return false
    }
    /**
     * API function that I had to override, if all children are selectable
     * @param childPosition the child position I want to know if is select
     * @param groupPosition the group position that has the child that I want to know if is selectable
     * @return always true
     */
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}