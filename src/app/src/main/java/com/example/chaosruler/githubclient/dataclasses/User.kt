package com.example.chaosruler.githubclient.dataclasses

/**
 *  an object representation of a user
 */
class User(
        /**
         * the username
         */
        private var __username: String,
        /**
         * the password
         */
        private var __password: String) {


    /**
     * identifies user
     * @return a string to identify the user
     */
    override fun toString(): String
    {
        return this.__username
    }

    /**
     * the username
     * @return the users login informoation
     */
    fun get__username(): String
    {
        return this.__username
    }

    /**
     * set a new username for this object
     * @param username the new username
     */
    @Suppress("unused")
    fun set__username(username: String)
    {
        this.__username = username
    }

    /**
     * gets user current password
     * @return the user's current password
     */
    fun get__password(): String
    {
        return __password
    }

    /**
     * sets a new password for user
     * @param password the new user password
     */
    @Suppress("unused")
    fun set__password(password: String)
    {
        this.__password = password
    }
}

