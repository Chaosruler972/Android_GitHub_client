package com.example.chaosruler.githubclient.services

import android.content.Context
import android.preference.PreferenceManager
import android.view.View
import com.example.chaosruler.githubclient.R


object themer
{
    /*
    @Suppress("unused")
    fun <T : View> Activity.find(id: Int): T = this.findViewById(id) as T
*/
    fun style(context: Context):Int
    {
        val isDark: String
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        isDark = preferences.getString(context.getString(R.string.style),"Light")
        return getResourceId(context, isDark, "style", context.packageName)
    }
    @Suppress("MemberVisibilityCanPrivate")
/*
        gets resourceid from context
        */
    fun getResourceId(context: Context, pVariableName: String, pResourcename: String, pPackageName: String): Int =
            context.resources.getIdentifier(pVariableName, pResourcename, pPackageName)



    fun get_view_by_id(convertView: View, id: Int): View = convertView.findViewById(id) // grabs the correpsonding view by id from layout

}