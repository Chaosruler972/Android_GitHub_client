package com.example.chaosruler.githubclient.services

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import com.example.chaosruler.githubclient.activities.MainActivity
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL


/**
 *   class responsible for downloading image and setting avy as that image
 *   @constructor url string - the url of the image, imgageview - the imageview we want to update
 */
@SuppressLint("StaticFieldLeak")
class load_Image_from_URL(
        /**
         * the url that has the image we want to download
         */
        private val url:String,
        /**
         * the imageView we want to update
         */
        private var imageView: ImageView
): AsyncTask<String, Void, Bitmap?>()
{

    /**
     * The download progress function, done in a seperate thread, that will download the data and return
     * either Bitmap of the downloaded image after parsing or null if download failed
     * @param params the parameters sent
     * @return the Bitmap if image download was successfull and parsing was successfull, otherwise NULL
     */
    override fun doInBackground(vararg params: String?): Bitmap?
    {
        try
        {
            /*
                builds a URL
             */
            val avatar_URL = URL(url)
            /*
                 opens a connection
             */
            val connection_to_download_avy = avatar_URL.openConnection()
            connection_to_download_avy.doInput = true
            connection_to_download_avy.connect()
            /*
                get input stream from data
             */
            val input_stream: InputStream = connection_to_download_avy.getInputStream()
            /*
                decode input stream into a image
             */
            return BitmapFactory.decodeStream(input_stream)
        }
        catch (e: MalformedURLException)
        {
            /*
                url is incorrect
             */
            Log.d("User Fragment","malformed URL")
        }
        catch (e:Exception)
        {
            /*
            anything wrong happened
             */
            Log.d("User Fragment","Something went wrong")
        }
        return null
    }

    /**
     * After download, set the new bitmap in imageView
     * @param result the downloaded bitmap if successfull and parsed successfull, otherwise NULL
     */
    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        /*
            upon success
         */
        Log.d("Image","There are results")
        if(result!=null)
        {
            /*
            update image on UI if download was successfull and decode was sucessfull
             */
            Log.d("Image","Loaded Image")
            MainActivity.act.runOnUiThread { imageView.setImageBitmap(result) }
        }

    }

}