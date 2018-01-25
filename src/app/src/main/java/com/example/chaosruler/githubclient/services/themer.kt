@file:Suppress("MemberVisibilityCanBePrivate")

package com.example.chaosruler.githubclient.services

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import android.view.View
import com.example.chaosruler.githubclient.R
import android.graphics.BitmapFactory
import kotlin.experimental.xor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.wifi.WifiManager
import android.util.Base64
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


@Suppress("unused")
/**
 * Singleton object that has general functions that we use to theme our application, style it
 * get views or a bridge-object to call encryption subroutines
 * @constructor as a singleton this object doesn't need to be manually constructed
 */
object themer
{

    /**
     *   change application background style
     *   @param context the context we are working with
     *   @return the resource id of the current configured style
     */
    fun style(context: Context):Int
    {
        val isDark: String
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        isDark = preferences.getString(context.getString(R.string.style),"Light")
        return getResourceId(context, isDark, "style", context.packageName)
    }

    /**
     * gets resourceid from context
     * @param context the context we are working with
     * @param pPackageName the package that consists this resource
     * @param pResourcename resouce name as configured in .xml files
     * @param pVariableName the variable that consists the resource (styles/strings/integers and so on)
     * @return the resource id
     */
    @Suppress("MemberVisibilityCanPrivate")
    fun getResourceId(context: Context, pVariableName: String, pResourcename: String, pPackageName: String): Int =
            context.resources.getIdentifier(pVariableName, pResourcename, pPackageName)


    /**
     * get view by id, for array adapter usage
     * @param convertView the view that we are scanning for children with IDs
     * @param id the id we are scanning
     * @return the View object that represents that ID
      */
    fun get_view_by_id(convertView: View, id: Int): View = convertView.findViewById(id) // grabs the correpsonding view by id from layout


    /**
     * try to parse image to make sure it is an image
     * @param arr the image data that we want to scan if its an image
     * @return true if it is an image, false otheriwse
     */
    fun is_image(arr:ByteArray):Boolean
    {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        @Suppress("UNUSED_VARIABLE")
        val bitmap = BitmapFactory.decodeByteArray(arr,0,arr.size,options)
        return options.outWidth != -1 && options.outHeight != -1
    }

    /**
     * get an imageview, exports ByteArray representing the picture
     * @param imageView the image view that we want to export the image from
     * @return the bytearray representing the image
     */
    fun getByteArrayFromImageView(imageView: ImageView): ByteArray
    {
        val bitmapDrawable = imageView.drawable as BitmapDrawable?
        val bitmap: Bitmap
        @Suppress("SENSELESS_COMPARISON")
        if (bitmapDrawable == null) {
            imageView.buildDrawingCache()
            bitmap = imageView.drawingCache
            imageView.buildDrawingCache(false)
        } else {
            bitmap = bitmapDrawable.bitmap
        }
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }



    /**
     * Encrypts and decrypts with Base64 encoding for hashing and security
     * also adds the functionality of AES encryption using the encryption class
     * @see encryption
     * @author Chaosruler972
     * @param a a bytearray to encrypt/decrypt
     * @param key a bytearray to use for decryp/encrypt key
     * @param con a base Context to work with
     * @param flag true = decryption, false= encryption
     */
    @SuppressLint("GetInstance")
    fun xorWithKey(a: ByteArray, key: ByteArray, flag: Boolean, con: Context): ByteArray {
        encryption.generate_key(con)
        val new_a = to_hebrew_unicode(String(a)).toByteArray()
        val out = ByteArray(new_a.size)
        for (i in new_a.indices) {
            out[i] = (new_a[i] xor key[i % key.size])
        }
        return if (flag)
            encryption.decrypt(Base64.decode(new_a, Base64.DEFAULT))
        else
            Base64.encode(encryption.encrypt(new_a), Base64.DEFAULT)

        //return new_a
    }

    /**
     * Gets this device WiFi MAC ID for device identification, COULD BE SPOOFED
     * @param con the basecontext to work with
     * @return the device ID
     */
    @SuppressLint("ServiceCast", "WifiManagerPotentialLeak", "HardwareIds")
    fun get_device_id(con: Context): String {
        val wifiManager = con.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wInfo = wifiManager.connectionInfo
        return wInfo.macAddress
    }

    /**
     * Converts a string to hebrew (unicode)
     * @param str the string to convert
     * @return a converted string with UTF8 this time
     */
    private fun to_hebrew_unicode(str: String): String {

        //WINDOWS-1255
        // UTF-8
        // Log.d("Char unicoded", toHex(str))
        val charSet = "UTF-8"
        // Log.d("Result after decode:",String(str.toByteArray(charset = Charset.forName(charSet)),Charset.forName(charSet)))
        return String(str.toByteArray(charset = Charset.forName(charSet)), Charset.forName(charSet))
    }

    /**
     * Finds the charset of a string
     * @param value the string to scan
     * @param charsets the list of possible charsets to scan
     * @return the charset that holds right to this string
     */
    @Suppress("unused")
    private fun charset(value: String, charsets: Array<String>): String {
        val probe = StandardCharsets.UTF_8.name()
        for (c in charsets) {
            val charset = Charset.forName(c)
            if (charset != null) {
                if (value == convert(convert(value, charset.name(), probe), probe, charset.name())) {
                    return c
                }
            }
        }
        return StandardCharsets.UTF_8.name()
    }

    /**
     * Converts a string from encoding a to encoding B
     * @param value the string to convert
     * @param fromEncoding the current encoding
     * @param toEncoding the desired encoding
     * @return the resulting string after convertion
     */
    private fun convert(value: String, fromEncoding: String, toEncoding: String): String = String(value.toByteArray(charset(fromEncoding)), Charset.forName(toEncoding))

    /**
     * Converts string to its hexadecimal representation (per byte)
     * @param arg the string to convert
     * @return the converted string to hexadecimal format
     */
    @Suppress("unused")
    fun toHex(arg: String): String {
        return String.format("%040x", BigInteger(1, arg.toByteArray()/*YOUR_CHARSET?*/))
    }
}