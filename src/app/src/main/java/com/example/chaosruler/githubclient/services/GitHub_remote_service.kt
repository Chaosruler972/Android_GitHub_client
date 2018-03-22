package com.example.chaosruler.githubclient.services

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.dataclasses.*
import com.github.kittinunf.fuel.httpGet
import org.eclipse.egit.github.core.RepositoryCommit
import org.eclipse.egit.github.core.RepositoryContents
import org.eclipse.egit.github.core.RepositoryId
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.client.RequestException
import org.eclipse.egit.github.core.service.*
import org.json.JSONArray
import org.json.JSONObject

import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * a Singleton class represnting the extetnsion of the API that I got from eclipse (egit)
 * that handles git requests, also usage of this functions are more favored by mr me developer
 * @constructor as a singleton object, this doesn't need to be constructed
 */
@Suppress("unused", "UNUSED_VARIABLE", "NAME_SHADOWING")
object GitHub_remote_service
{
    /**
     * a represention of egit Github client for API usage
     */
    private var client:GitHubClient = GitHubClient()
    @Suppress("unused")
    /**
     * a representation of egit Github Repository Service for API usage
     */
    private var repo_service:RepositoryService = RepositoryService()
    /**
     * a representation of egit Github User service for API usage
     */
    private var user_service:UserService = UserService()
    /**
     * a representation of egit Commit Service for API usage
     */
    private var commit_service:CommitService = CommitService()
    /**
     * a representation of egit gist service for API usage
     */
    private var gist_service:GistService = GistService()
    /**
     * a representation of egit data service for API usage
     */
    private var data_service:DataService = DataService()
    /**
     * a representation of egit Issue service for API usage
     */
    private var issue_service:IssueService = IssueService()

    /**
     * Login to github server - validates login
     * @param username the user name
     * @param password the password
     * @return true if successfull login, false otherwise
     */
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

    /**
     * Gets the username if exists
     * @return username as a String
     * @exception IllegalStateException when called before login was successful
     */
    fun get_name():String = user_service.user.name

    /**
     * Gets user profile picture URL
     * @return a string represneting the URL of user profile
     * @exception IllegalStateException when called before login was successfull
     */
    fun get_pic_url():String = user_service.user.avatarUrl


    /**
     * scan repositories and put them in a vector and return it (of loginned user)
     * @return a vector of the user's repositories
     */
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

    /**
     * the email of he login username
     * @return the email address of the username if configured, otherwise "Not provided" is returned
     */
    fun get_email():String = user_service.user.email?:"Not provided"

    /**
     * the location (country) of he login username
     * @return the country of the username if configured, otherwise "Not provided" is returned
     */
    fun get_location():String = user_service.user.location?:"Not provided"

    /**
     * the disk usage as computed by API
     * @return the disk usage in bytes
     */
    fun get_disk_usage():Int = user_service.user.diskUsage

    /**
     * returns the total repos that we have
     * @return the amount of repos user has
     */
    fun get_total_repos():Int = user_service.user.publicRepos + user_service.user.ownedPrivateRepos

    /**
     * query a repo by repo's id and username
     * @param repo_name the reponame that we want to query
     * @param user_name the username that we want to query
     * @return a repo matching the query criteria, empty repo otherwise)
     */
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

    /**
     * Get the login user Content Service for API usage
     * @param repo_name the repo name that we want to scan content service of
     * @param user_name the username that we want to scan content sevice of
     * @return the content service
     */
    fun get_ContentService(repo_name: String,user_name: String):ContentsService
    {
        Log.d("GitHub","Before CS")
        val server_data = repo_service.getRepository(user_name,repo_name)
        Log.d("GitHub","Found server data")
        val contenstService =  ContentsService(GitHubClient(server_data.url))
        Log.d("GitHub","Found CS")
        return contenstService
    }

    /**
     * returns RepoID( a key for each repo independt of username and reponame)
     * @param repo_name the reponame that we want to query
     * @param user_name the username that we want to query
     * @return the resulting key from Github that represents the repo without using reponame and username to query
     */
    private fun get_repository_ID(repo_name: String, user_name: String): RepositoryId
    {
        Log.d("GitHub","Before ID")
        val repoid =  RepositoryId(user_name,repo_name)
        Log.d("GitHub","After ID")
        return repoid
    }

    /**
     * get content of a repo in directory
     * @param path the path we want to scan, "/" is default
     * @param repo_name the reponame that we want to scan
     * @param user_name the username that we want to scan
     * @return a list of all repo content in path specified
     */
    fun get_content(repo_name: String,user_name: String, path:String = "/"): MutableList<RepositoryContents>? = ContentsService(client).getContents(get_repository_ID(repo_name,user_name),path)

    fun get_repo_url(repo_name: String,user_name: String): String? {
        val server_data = repo_service.getRepository(user_name,repo_name)
        return server_data.url
    }

    /**
     * get gists via HTTP, gists of username is scanned (page number is for recursive iteration)
     * @param context the context we need to grab url from strings.xml
     * @param page_number the page number we last visited, should start from 0, this function will iterate by itself knowing the amount from configuration
     * @param user_name the username that we want to scan his gists
     * @return a vector of gists upon success, empty vector otherwise
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
        {
            Log.d("Gists","Website returned error on request\nURL: $url")
            return vector
        }
        /*
            get JSON data
         */
        @Suppress("CanBeVal")
        var response_text:String?//= String(response.data)
        @Suppress("CanBeVal")
        var response_array:JSONArray?// = JSONArray(response_text)
        Log.d("Gists","Parsing Gist array")
        try
        {
            response_text = String(response.data)
            response_array = JSONArray(response_text)
        }
        catch (e:Exception)
        {
            return vector
        }
        Log.d("Gists","Parsing each gist")
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

    /**
     * gets the client login (not username)
     * @return clients login (usually email) as oppsoed to username, empty otherwise (not loginned)
     */
    fun get_login():String = client.user?:""

    /**
     * gets the client login (yes username lol)
     * @return clients login (this time username) as opposed to email, empty string otherwise
     */
    fun get_username():String = this.user_service.user.login?:""

    /**
     * search for repos by string arguement, this is a recursive function iterated by page number
     * @param context the context we work with for configuration managing
     * @param page_number the page number we are on, should be inited to 0, function would call the rest until settings configuration limit is set
     * @param search_arguements the arguements we search by
     * @return vector of repos that match the criteria
     */
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
        val amount = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.sync_frequency),"100").toInt()

        return if(vector.size == 0 || page_number == amount/100 && amount!=0)
            vector
        else
        {
            try
            {
                vector.addAll(search_for_repos(search_arguements, page_number + 1,context))

            }
            catch (e: RequestException)
            {

            }
            vector
        }
    }

    /**
     * self implented API call, gets all the issues in self made objectified vector
     * @param context the context we work with to generate configuration and urls
     * @param reponame the reponame that we want to grab its issues
     * @param user_name the username that has the repo we want to grab his issues
     * @return a vector of issues matching criteria
     */
    fun get_issues(reponame:String,user_name: String,context: Context): Vector<issue> {
        val vector:Vector<issue> = Vector()

        val url = context.getString(R.string.download_url)+context.getString(R.string.issues_url).replace("OWNER",user_name).replace("REPO",reponame)+context.getString(R.string.AuthToken)
        /*
            request URL for manual API request from GITHUB api... egit didn't do it, so we need to
         */
        val (request, response, result) = url.httpGet().responseString() // result is Result<String, FuelError>
        if(response.statusCode!=200)
            return vector
        /*
        http okay
         */
        val response_text = String(response.data)
        val response_array = JSONArray(response_text)
        /*
        means parse was okay... on issues we shouldn't be surprised
         */
        for(i in 0 until response_array.length())
        {
            /*
            for each issue
             */
            val json_issue = response_array.getJSONObject(i)
            var title:String?
            @Suppress("NAME_SHADOWING")
            var url:String?
            try
            {
                /*
                    case grab data was okay
                 */
                title = json_issue.getString(context.getString(R.string.issue_title))
                url = json_issue.getString(context.getString(R.string.issue_html_url))
            }
            catch (e:Exception)
            {
                continue
            }
            /*
                add that or nothing
             */
            vector.addElement(issue(title?:"",url?:""))
        }
        return vector
    }

    /**
     * self made API reimplentation, generally uses HTTP calls to get all commits and comments and put them in
     * objectified vector
     * @param context the context we work with to generate url and configuration
     * @param repo_name the reponame that we get its commits
     * @param user_name the username that has that repo
     * @return a vector of commits matching criteria
     */
    @SuppressLint("SimpleDateFormat")
    @Suppress("UNUSED_VALUE")
    fun get_commits(repo_name: String, user_name: String, @Suppress("UNUSED_PARAMETER") context: Context): Vector<commit> {
        val vector: Vector<commit> = Vector()
        val commits:List<RepositoryCommit>
        /*
            attempts to grab commits from repo
         */
        try
        {
             commits = commit_service.getCommits(get_repository_ID(repo_name, user_name))

        }
        catch (e: Exception)
        {
            return vector
        }

        /*
            runs for each commit -> enter to vector
         */
        commits.forEach {
            /*
                get general data (such as commit message, author name etcetra)
             */
            val comment_vector:Vector<commit_comment> = Vector()
            val commit_message = it.commit?.message?:""
            val author = it.author?.name?:""
            val date = it.commit.committer.date
            /*
                case there are comments, we need to grab it from server manually like bi***
             */
            if(it.commit.commentCount > 0)
            {
                /*
                    to parse dates from GIT to long
                 */
                val formatter = SimpleDateFormat("YYYY-MM-DD'T'HH:MM:SS'Z'")
                /*
                    commit general URL
                 */
                val url = it.url+context.getString(R.string.AuthToken)
                val (request, response, result) = url.httpGet().responseString() // result is Result<String, FuelError>
                if (response.statusCode != 200)
                    return vector
                /*
                    response ok
                 */
                val response_text = String(response.data)
                @Suppress("CanBeVal")

                var response_JSON:JSONObject? = null
                try
                {
                    response_JSON = JSONObject(response_text)
                }
                catch (e:Exception)
                {
                    Log.d("Commit","Error")
                }
                /*
                    means everything is okay so far, we parsed commit HTTP request
                 */
                if(response_JSON!=null)
                {
                    var comment_url = ""
                    try
                    {
                        /*
                            attempts to grab COMMENTS URL in case there is one to grab
                         */
                        comment_url = response_JSON.getString(context.getString(R.string.comment_url))
                    }
                    catch (e:Exception)
                    {
                        Log.d("Comments","Failed getting response\n${e.message} Check yourself at $url")
                    }
                    /*
                        case there is comment URL
                     */
                    if(comment_url != "")
                    {
                        val (request, response, result) = (comment_url+context.getString(R.string.AuthToken)).httpGet().responseString() // result is Result<String, FuelError>
                        if (response.statusCode != 200)
                            return vector
                        /*
                            response ok...
                         */
                        val response_text = String(response.data)
                        var responseJSON:JSONArray? = null

                        try
                        {
                            responseJSON = JSONArray(response_text)
                        }
                        catch (e:Exception)
                        {
                            Log.d("Comments","Failed getting comment array\n${e.message}")
                        }
                        /*
                            case everything is okay so ar - I loaded array of comments
                         */
                        @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
                        if(responseJSON!=null)
                        {
                            var j=0
                            for(j in 0 until responseJSON.length())
                            {
                                /*
                                    for each comment grab data relvent to it
                                 */
                                val obj = responseJSON.getJSONObject(j)
                                var commentuser:String?
                                var comment:String?

                                var date: Long
                                try
                                {
                                    val commentusr = obj.getJSONObject(context.getString(R.string.comment_user))
                                    commentuser = commentusr.getString(context.getString(R.string.comment_login))
                                    comment = obj.getString(context.getString(R.string.comment_body))
                                    date = formatter.parse(obj.getString(context.getString(R.string.comment_created_at))).time
                                }
                                catch (e:Exception)
                                {
                                    Log.d("Comments","Failed parsing info")
                                    continue
                                }
                                /*
                                    when done add relvent data to comment vector
                                 */
                                if(commentuser!=null && comment!=null && date != 0.toLong())
                                    comment_vector.addElement(commit_comment(commentuser,comment,date))
                            }
                        }

                    }
                }
            }
            /*
            after going through all the trouble, try adding the entire data to one object and add it to the vector
             */
            val commit = commit(author, commit_message, date.time, comment_vector)
            vector.addElement(commit)
        }
        return vector
    }

    /**
     * a self implentation function to query Github to find local users, this is a recursive function that uses page number to know when to stop
     * @param context the context we work with to generate URL and configurations
     * @param location the location we want to scan
     * @param page_number the page number we are on, inited to 0, and calls the rest until configartion limit is set
     * @return a list of users that match criteria
     */
    @Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
    fun search_user_by_location(location:String, context: Context, page_number: Int):Vector<search_user>
    {
        val vector:Vector<search_user> = Vector()
        val url = context.getString(R.string.download_url) + context.getString(R.string.location_URL).replace("LOCATION",location) +"&page=$page_number" + context.getString(R.string.AuthToken_addon)
        val (request, response, result) = url.httpGet().responseString() // result is Result<String, FuelError>
        if(response.statusCode != 200)
            return vector
        /*
            HTTP ok
         */
        var arr:JSONArray? = null
        try
        {
            /*
                attempt to parse JSON array
             */
            val obj= JSONObject(String(response.data))
            arr = obj.getJSONArray(context.getString(R.string.search_items))
        }
        catch (e:Exception)
        {
            Log.d("Search users","It went wrong..")
            Log.d("Search users",e.message)
        }
        if(arr == null)
            return vector
       // Log.d("Search Users",url)
        for(i in 0 until arr.length())
        {
            var username:String? = null
            var avatar_url:String? = null
            var http_url:String? = null
            var id:String? = null
            try
            {
                /*
                    parse data
                 */
                val obj = arr.getJSONObject(i)
                username = obj.getString(context.getString(R.string.search_user_login))
                avatar_url = obj.getString(context.getString(R.string.search_user_avatar))
                http_url = obj.getString(context.getString(R.string.search_user_url))
                id = obj.getString(context.getString(R.string.user_id))
            }
            catch (e:Exception)
            {
                /*
                    upon failure
                 */
                Log.d("Search user","Couldn't parse data")
                continue
            }
            /*
                success - if everything is not null, enter data
             */
            if(username!=null && avatar_url!=null && http_url!=null && id!=null)
            {
                /*
                    get email and put instead of null
                 */
                var phone:String? = null
                val userObj = UserService(client).getUser(username)
                if(userObj!=null)
                    phone=userObj.email
                vector.addElement(search_user(username, phone, avatar_url, http_url, context))
            }
        }
        val amount = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.sync_frequency),"100").toInt()
        return if(page_number<=amount && amount!=0)
        {
            vector.addAll(search_user_by_location(location,context,page_number+1))
            vector
        }
        else
            vector
    }

}
