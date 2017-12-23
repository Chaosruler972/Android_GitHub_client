package com.example.chaosruler.githubclient.fragments.fragments.repo_files

import android.content.Context
import android.content.Intent
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.activities.RepoView_Activity
import com.example.chaosruler.githubclient.activities.display_file_activity
import com.example.chaosruler.githubclient.services.themer
import org.eclipse.egit.github.core.RepositoryContents
import org.eclipse.egit.github.core.service.ContentsService
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URL
import java.nio.charset.Charset


@Suppress("unused")
class repo_files_arrayadapter(context: Context, arr:Array<RepositoryContents>, private val contentsService: ContentsService, private val repo_files_fragment: repo_files_fragment) :ArrayAdapter<RepositoryContents>(context, R.layout.item_file,arr)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        @Suppress("NAME_SHADOWING")
        val convertView: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_file,parent,false)

        /*
        load UI content
         */
        val name = themer.get_view_by_id(convertView,R.id.file_name) as TextView
        val goto = themer.get_view_by_id(convertView,R.id.file_enter) as ImageButton

        /*
        load content item
         */
        val content = getItem(position)

        /*
        update data available to us
         */
        name.text = content.name

        /*
            case content type is a directory, therefore it can't be "read" as a file,
            so I change visibility of that button to GONE, also, if we press on that folder
            we can "open" it
         */
        if(content.type == RepositoryContents.TYPE_DIR)
        {
            name.setOnClickListener { repo_files_fragment.enter_folder(content.path,contentsService) }
            goto.visibility = ImageButton.GONE
        }
        /*
            case content type is a file
         */
        else if(content.type == RepositoryContents.TYPE_FILE)
        {
            /*
            therefore our button CAN work to load the file
             */
            goto.setOnClickListener{

                /*
                first we build the HTTP request to download the file from git server
                 */
                var http_request = context.getString(R.string.download_url) + context.getString(R.string.content_url)
                http_request = http_request.replace("REPO",repo_files_fragment.repo_name)
                http_request = http_request.replace("OWNER",repo_files_fragment.user_name)
                http_request = http_request.replace("PATH",content.path)
                http_request += context.getString(R.string.AuthToken)
                Log.d("URL is",http_request)
                val repo_view_progressbar = RepoView_Activity.act!!.findViewById(R.id.repo_view_progressBar) as ProgressBar
                repo_view_progressbar.visibility = ProgressBar.VISIBLE
                Thread{
                    Looper.prepare()
                    /*
                    creates a new URL and connects to it
                     */

                    val url = URL(http_request)
                    val con = url.openConnection()
                    con.connect()

                    try
                    {
                        /*
                        attempts download
                         */
                        val input_stream = BufferedInputStream(con.getInputStream())
                        /*
                            read entire data
                         */
                        val str = input_stream.readTextAndClose()
                        /*
                        cnverts data to JSON
                         */
                        val obj = JSONObject(str)
                        RepoView_Activity.act!!.runOnUiThread { repo_view_progressbar.visibility = ProgressBar.INVISIBLE }
                        if(obj.getString(context.getString(R.string.git_json_encoding)) == context.getString(R.string.git_json_base64)) // on our ideal world, GIT encodes with base64, we will determine it as "success"
                        {
                            /*
                                we will get that data
                             */
                            val data = Base64.decode(obj.getString(context.getString(R.string.git_json_content)),Base64.DEFAULT)
                            /*
                            decode it and send it to the data display intent
                             */
                            val new_intent = Intent(context,display_file_activity::class.java)
                            new_intent.putExtra(context.getString(R.string.file_key),String(data))
                            context.startActivity(new_intent)
                        }
                    }
                    catch (e:Exception)
                    {
                        /*
                            case anything went wrong, display error message
                         */
                        RepoView_Activity.act!!.runOnUiThread{ Toast.makeText(RepoView_Activity.act!!,context.getString(R.string.cant_display_data),Toast.LENGTH_SHORT).show() }
                        return@Thread
                    }
                }.start()

            }
        }



        return convertView
    }

    /*
        self made function, instead of looping
     */
    private fun InputStream.readTextAndClose(charset: Charset = Charsets.UTF_8): String {
        return this.bufferedReader(charset).use { it.readText() }
    }
}