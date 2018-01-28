@file:Suppress("MemberVisibilityCanBePrivate")

package com.example.chaosruler.githubclient.SQLite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.dataclasses.User
import java.util.*
import kotlin.collections.HashMap


/**
 * an implentation of user database using the abstract database helper and User data class
 * @see User
 * @see local_SQL_Helper
 * @constructor gets only the context, the rest is generated from strings.xml file
 */
@Suppress("unused", "MemberVisibilityCanPrivate")
class user_database_helper(
        /**
         * a context that we use in order to do android operations, such as getting
         * strings.xml data
         */
        con: Context
) : local_SQL_Helper(
        con,
        con.getString(R.string.USER_database_filename),
        null, con.resources.getInteger(R.integer.USER_DB_VERSION),
        con.getString(R.string.USER_TABLE_NAME))
{
    /**
     * the User id field name
     */
    private val USERS_ID: String = con.getString(R.string.USER_COL_ID)
    /**
     * the password field name
     */
    private val PASSWORD: String = con.getString(R.string.USER_COL_PASSWORD)


    /**
     * MUST BE CALLED, it reports to the database about the table schema, is used by the abstracted
     * SQL class
     */
    init
    {
        val vector: Vector<String> = Vector()
        vector.add(USERS_ID)
        vector.add(PASSWORD)
        init_vector_of_variables(vector)


    }

    /**
     * provides info for the abstracted SQL class
     * on what the table schema is for creation
     * function is responsible for generating the data for CREATE DB statement
     * @param db an instance of the database we are working with
     */
    override fun onCreate(db: SQLiteDatabase) {

        val map: HashMap<String, String> = HashMap()
        map[USERS_ID] = "BLOB primary key"
        map[PASSWORD] = "BLOB"
        createDB(db,map)
    }


    /**
     *
     * add user mechanism
     * if user is invalid, forget about it
     * if user is valid, and it exists, update it
     * if its a new user, add a new user to table
     * @param username the username data
     * @param password the password data
     * @return true upon success, false otherwsise
     */
    fun add_user(username: String, password: String) // subroutine that manages the user adding operation to the database
            : Boolean {
        if ( username.isEmpty() || password.isEmpty())
            return false
        if (check_user( username)) // checks if user exists in database
            update_user(username, password) // if it does, lets update its password
        else // if it doesn't lets create a new entry for the user
            insert_user(username, password)
        return true
        /*
        var map:HashMap<String,String> = HashMap()
        map[USERS_ID] = username
        map[PASSWORD] = password
        return replace(map)
        */
    }

    /**
     * checks if user exists, query is not that smart, gets an ENTIRE table and than checks
     * if the user is there
     * @param username the name of the username we want to check
     * @return true if found, false otherwise
     */
    fun check_user(username: String) // subroutine to check if users exists on the database
            : Boolean {
        if ( username.isEmpty())
            return false
        val user = get_user_by_id( username)
        return user != null
    }


    /**
     * Inserts user to database, not caring if it exists already or not
     * @param username username data
     * @param password the password data
     */
    private fun insert_user(username: String, password: String) // subroutine to insert a user to the database
    {
        if ( username.isEmpty() || password.isEmpty())
            return
        val everything_to_add: Vector<HashMap<String, String>> = Vector()

        val data: HashMap<String, String> = HashMap()
        data[USERS_ID] = username
        data[PASSWORD] = password
        everything_to_add.addElement(data)
        add_data(everything_to_add)
    }

    /**
     * subroutine in charge of feeding information and database information to
     * SQL abstraction on update queries
     * updates user password
     * @param username the name of the username we want to update it's password
     * @param password the new password
     * @return true if successfull for at least one user, false otherwise
     */
    fun update_user(username: String, password: String) // subroutine to update data of a user that exists on the database
            : Boolean {
        if ( username.isEmpty() || password.isEmpty())
            return false

        val change_to: HashMap<String, String> = HashMap()
        change_to[PASSWORD] = password
        return update_data(USERS_ID, arrayOf(username),change_to)
    }

    /**
     *  subroutine in charge of feeding information and database information to
     *  SQL abstraction on delete queries
     *  deletes a user from the table by name
     *  @param username the username of the user we want to delete
     *  @return true if successfull, false otherwise
     */
    fun delete_user( username: String):Boolean // subroutine to delete a user from the database (local)
    {
        if ( username.isEmpty())
            return false
        if (!check_user( username))
            return false
        return remove_from_db(USERS_ID, arrayOf(username))

    }

    /**
     *    subroutine that converts the entire table from hashmap to vector of users
     *    @return the entire database as a vector of User objects
     */
    fun get_entire_db():Vector<User> // subroutine to get the entire database as an iterateable vector
    {
        val users: Vector<User> = Vector()
        val vector: Vector<HashMap<String, String>> = get_db()
        vector
                .map { User(it[USERS_ID].toString(), (it[PASSWORD]?:"").trim()) }
                .forEach { users.addElement(it) }
        return users
    }

    /**
     * subroutine that is in charge of getting the user class
     *  by query
     *  @param username the username of the user we want to get
     *  @return if the user was found, we return it's object representation, otherwise returns NULL
     */
    fun get_user_by_id(username: String) // subroutine to get a User object representing a user by the user id (username)
            : User?
    {
        if ( username.isEmpty())
            return null
        val input_map = HashMap<String, String>()
        input_map[USERS_ID] = "$username"
        val vector = get_rows(input_map)
        if(vector.size > 0)
        {
            return User( (vector.firstElement()[USERS_ID]?:"").trim(),(vector.firstElement()[PASSWORD]?:"").trim() )
        }


        return null
    }




}