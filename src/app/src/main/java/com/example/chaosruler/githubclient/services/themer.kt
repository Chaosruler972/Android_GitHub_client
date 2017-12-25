package com.example.chaosruler.githubclient.services

import android.content.Context
import android.preference.PreferenceManager
import android.view.View
import com.example.chaosruler.githubclient.R
import android.graphics.BitmapFactory
import kotlin.experimental.xor


object themer
{
    /*
    @Suppress("unused")
    fun <T : View> Activity.find(id: Int): T = this.findViewById(id) as T
*/


    /*
    change application background style
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
    /*
        get resource id as raw id
     */
    fun getResourceId(context: Context, pVariableName: String, pResourcename: String, pPackageName: String): Int =
            context.resources.getIdentifier(pVariableName, pResourcename, pPackageName)


/*
    get view by id, for array adapter usage
 */
    fun get_view_by_id(convertView: View, id: Int): View = convertView.findViewById(id) // grabs the correpsonding view by id from layout


    /*
        try to parse image to make sure it is an image
     */
    fun is_image(arr:ByteArray):Boolean
    {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        @Suppress("UNUSED_VARIABLE")
        val bitmap = BitmapFactory.decodeByteArray(arr,0,arr.size,options)
        return options.outWidth != -1 && options.outHeight != -1
    }

    /*
        xors both byte arrays, encryption
     */

     fun xorWithKey(a: ByteArray, key: ByteArray): ByteArray {
        val out = ByteArray(a.size)
        for (i in a.indices) {
            out[i] = (a[i] xor key[i % key.size])
        }
        return out
    }
}