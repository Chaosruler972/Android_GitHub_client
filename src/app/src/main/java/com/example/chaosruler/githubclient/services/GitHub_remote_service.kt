package com.example.chaosruler.githubclient.services

import android.content.Context
import android.util.Log
import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.dataclasses.gist
import com.example.chaosruler.githubclient.dataclasses.gist_file
import com.example.chaosruler.githubclient.dataclasses.repo
import com.github.kittinunf.fuel.httpGet
import org.eclipse.egit.github.core.RepositoryContents
import org.eclipse.egit.github.core.RepositoryId
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.*
import org.json.JSONArray
import org.json.JSONObject

import java.io.IOException
import java.util.*


@Suppress("unused")
object GitHub_remote_service
{
    private var client:GitHubClient = GitHubClient()
    @Suppress("unused")
    private var repo_service:RepositoryService = RepositoryService()
    private var user_service:UserService = UserService()
    private var commit_service:CommitService = CommitService()
    private var gist_service:GistService = GistService()
    private var data_service:DataService = DataService()
    private var issue_service:IssueService = IssueService()

    fun login(username:String,password:String): Boolean
    {
        client.setCredentials(username,password)

        /*
            inits user service to check login status
         */
        user_service = UserService(client)
        Log.d("GitHub","Logging in with user ${client.user}")
        return try
        {
            val user = user_service.user
            Log.d("GitHub","User is $user")
            /*
                case login succesfull, lets init all services
             */
            repo_service = RepositoryService(client)
            commit_service = CommitService(client)
            gist_service = GistService(client)
            data_service = DataService(client)
            issue_service = IssueService(client)
            true
        }
        catch (e:IOException)
        {
            Log.d("GitHub","Exception, couldn't get user")
            false
        }
    }

    fun get_name():String = user_service.user.name

    fun get_pic_url():String = user_service.user.avatarUrl

    fun list_self_repositories(): Vector<repo> {
        val list_of_repos:Vector<repo> = Vector()
        repo_service.repositories.map { list_of_repos.addElement(repo(
                it.name?:"",
                it.description?:"",
                it.url?:"",
                it.language?:"",
                it.isFork,
                it.forks,
                it.openIssues,
                it.createdAt.time,
                it.updatedAt.time,
                it.isPrivate,
                it.id,
                it.owner.login,
                it.isHasWiki
        )) }
        return list_of_repos
    }

    fun get_email():String = user_service.user.email?:"Not provided"

    fun get_location():String = user_service.user.location?:"Not provided"

    fun get_disk_usage():Int = user_service.user.diskUsage

    fun get_total_repos():Int = user_service.user.publicRepos + user_service.user.ownedPrivateRepos

    fun get_repo_by_id_and_name(repo_name:String,user_name:String):repo
    {
        val server_data = repo_service.getRepository(user_name,repo_name)

        return repo(server_data.name?:"",
                server_data.description?:"",
                server_data.url?:"",
                server_data.language?:"",
                server_data.isFork,
                server_data.forks,
                server_data.openIssues,
                server_data.createdAt.time,
                server_data.updatedAt.time,
                server_data.isPrivate,
                server_data.id,
                server_data.owner.login,
                server_data.isHasWiki)
    }

    fun get_ContentService(repo_name: String,user_name: String):ContentsService
    {
        Log.d("GitHub","Before CS")
        val server_data = repo_service.getRepository(user_name,repo_name)
        Log.d("GitHub","Found server data")
        val contenstService =  ContentsService(GitHubClient(server_data.url))
        Log.d("GitHub","Found CS")
        return contenstService
    }

    private fun get_repository_ID(repo_name: String, user_name: String): RepositoryId
    {
        Log.d("GitHub","Before ID")
        val repoid =  RepositoryId(user_name,repo_name)
        Log.d("GitHub","After ID")
        return repoid
    }

    fun get_content(repo_name: String,user_name: String, path:String = "/"): MutableList<RepositoryContents>? = ContentsService().getContents(get_repository_ID(repo_name,user_name),path)

    fun get_repo_url(repo_name: String,user_name: String): String? {
        val server_data = repo_service.getRepository(user_name,repo_name)
        return server_data.url
    }

    /*
        get gists via HTTP
     */
    @Suppress("UNUSED_VARIABLE")
    fun get_gists(user_name: String, context: Context, page_number:Int):Vector<gist>
    {
        val vector:Vector<gist> = Vector()
        /*
            case user is invalid
         */
        if(user_name.isEmpty())
            return vector
        /*
            get to link on http and get response in JSON
         */
        val url = context.getString(R.string.download_url)+context.getString(R.string.gists_url).replace("OWNER",user_name)+context.getString(R.string.AuthToken)+"&page=$page_number"
        val (request, response, result) = url.httpGet().responseString() // result is Result<String, FuelError>
        /*
            case response is invalid
         */
        if(response.statusCode!=200)
            return vector
        /*
            get JSON data
         */
        val response_text = String(response.data)
        val response_array = JSONArray(response_text)
        /*
            for Each GIST
         */
        for(i in 0 until response_array.length())
        {

            /*
                try to parse files and description
             */
            var files: JSONObject?
            var gist_desc: String?
            try
            {
                val obj = response_array.getJSONObject(i)
                gist_desc = obj.getString("description")
                files = obj.getJSONObject("files")
            }
            catch (e:Exception)
            {
               /*
                goto next on case of failure
                */
                continue
            }
            /*
                for each file
             */
            if(files!= null && gist_desc!=null)
            {

                /*
                    add a file name to the file vector along with file data and language
                 */
                val file_vector:Vector<gist_file> = Vector()
                for(j in 0 until files.names().length())
                {
                    var filename:String?
                    var data:String?
                    var language:String?
                    try
                    {
                        /*
                            try parsing JSON
                         */
                        val key = files.names().getString(j)
                        val file = files.getJSONObject(key)
                        filename = file.getString("filename")
                        val file_url = file.getString("raw_url")
                        /*
                            if we got a URL, try to get to that URL to get gist data
                         */
                        val (file_request, file_response, file_result) = file_url.httpGet().responseString() // result is Result<String, FuelError>
                        /*
                            if there isn't a gist data, goto next
                         */
                        if(response.statusCode!=200)
                            continue
                        data = String(file_response.data)
                        language = file.getString("language")

                    }
                    catch (e:Exception)
                    {
                        /*
                            case failure - goto next
                         */
                        continue
                    }

                    /*
                        add file to vector
                     */
                    val file = gist_file(filename?:"",data,language?:"")
                    file_vector.addElement(file)
                }
                /*
                    add gists to vector
                 */
                val gist = gist(gist_desc,file_vector)
                vector.addElement(gist)
            }

        }
        /*
            if this page was the last page, it would've been empty
         */
        return if(response_array.length()==0)
            vector
        else
        {
            /*
                case this page might have not been the last page, non empty array - try checking the next page
             */
            vector.addAll(get_gists(user_name,context,page_number+1))
            vector
        }

    }

    fun get_login():String = client.user?:""

    fun search_for_repos(search_arguements:String,page_number: Int,context: Context):Vector<repo>
    {
        val vector:Vector<repo> = Vector()
        val list = RepositoryService(client).searchRepositories(search_arguements,page_number)
        list.forEach { vector.addElement(repo(
                it.name?:"",
                it.description?:"",
                it.url?:"",
                it.language?:"",
                it.isFork,
                it.forks,
                it.openIssues,
                it.createdAt.time,
                it.createdAt.time,
                it.isPrivate,
                0,
                it.owner?:"",
                it.isHasWiki
        )) }
        return if(vector.size == 0 || page_number == context.resources.getInteger(R.integer.limit_amount_of_pages_on_search_repo))
            vector
        else
        {
            vector.addAll(search_for_repos(search_arguements, page_number + 1,context))
            vector
        }
    }

}
