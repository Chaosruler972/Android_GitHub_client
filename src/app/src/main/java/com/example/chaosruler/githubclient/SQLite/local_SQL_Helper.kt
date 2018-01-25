package com.example.chaosruler.githubclient.SQLite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.chaosruler.githubclient.services.themer
import java.util.*
import kotlin.collections.HashMap

@Suppress("unused")
/**
 * Abstract implentation that helps us define the requirement of a SQLite database
 * also, implenets in an abstract way the select * from db and insert to db queries
 * @see SQLiteOpenHelper
 * @constructor constructs the table name and database name and version to the class file
 */
abstract class local_SQL_Helper(
                                /**
                                 * The context we are working with
                                 */
                                private var context: Context,
                                /**
                                 * The name of the database, will open as
                                 * file: DATABASE_NAME.db
                                 */
                                private var DATABASE_NAME: String,
                                factory: SQLiteDatabase.CursorFactory?,
                                version: Int,
                                /**
                                 * The name of the table, will open a table with this name
                                 * at the database
                                 */
                                private var TABLE_NAME: String
                ) : SQLiteOpenHelper(context, DATABASE_NAME, factory, version)
{

    /**
     *
     * Basic idea is to initate the vector with all the variables with a call to init_vector
     * before doing SQL functions and do call onCreate with the variable types per key
     */
    private lateinit var vector_of_variables: Vector<String>


    /**
     * Function that initates the vector of variables, required to call this function before usage of the
     * database class, right after construction
     * @param vector the vector that holds the field names
     */
    protected fun init_vector_of_variables(vector:Vector<String>)
    {
        vector_of_variables = vector
        try
        {
            val db = this.writableDatabase
            if(!isTableExists(TABLE_NAME))
                this.onCreate(db) // ensures this is called, android by itself will only do it if it needs to read/write the database
            //db.close()
        }
        catch (e:SQLiteException)
        {
            Log.d("Local SQL Exception","DB $DATABASE_NAME was not created yet")
        }
    }

    /**
     * This function checks if a specific table exists (or not) on our database
     * may return false even if table exists, only when table is empty!
     * @param tableName The name of the table we want to check if it exists or not
     * @return if the table exists in the database, or not
     * @exception SQLiteException when .raqQuery is not viable via the NDK
     */
    private fun isTableExists(tableName: String): Boolean {

        val mDatabase: SQLiteDatabase = readableDatabase

        val cursor = mDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '$tableName'", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.close()
                return true
            }
            cursor.close()
        }
        return false
    }

    /**
     * This function is an abstract implentation on the API that is responsible for the
     * ONCREATE statement on the SQLite databases
     * @param db an instance of the database object we want to create
     */
    abstract override fun onCreate(db: SQLiteDatabase)

    /**
     *
     *  checks if we should upgrade.. and drops and recreates
     *  @param db an instance of the database we are working on
     *  @param oldVersion the old version number, the version we upgrade from
     *  @param newVersion the new version number, the version we update to
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion > oldVersion)
        {
            dropDB(db)
            onCreate(db)
        }
    }


    /**
     * drop database, deletes all data and schema
     * @param db an instance of the database we are working on
     */
    private fun dropDB(db: SQLiteDatabase?) // subroutine to delete the entire database, including the file
    {
        db?.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME)
    }

    /**
     * clears all database values
     */
    fun clearDB()
    {
        this.writableDatabase.execSQL("delete from " + TABLE_NAME)
    }

    /**
     *
     *  subroutine to template a create table statement
     * takes parameters neccesery
     * @param db an insance of the database
     * @param variables the variables that we want to init on the database (format of <key> = name, <value> = type)
     */
    protected fun createDB(db: SQLiteDatabase ,variables: HashMap<String,String>) {

        var create_statement = "create table IF NOT EXISTS $TABLE_NAME ("
        for(element in vector_of_variables)
        {
            create_statement += element + " " + variables[element]
            if(vector_of_variables.lastElement() != element)
                create_statement+=" ,"
        }


        create_statement += ")"

        db.execSQL(create_statement)

    }

    /**
     *
     * subroutine to template a create table statement
     * takes parameters neccesery
     * with foreign key support*
     * @param db an instance of the database
     * @param variables the variables that we want to init on the database (format of <key> = field name, <value> = data type)
     * @param foregin a foreign key hashmap that we want to init with the format of <key> = local field name, <value> = a string with references table of and the table's referencing field
    **/
    protected fun createDB(db: SQLiteDatabase ,variables: HashMap<String,String>, foregin:HashMap<String,String>) {

        var create_statement = "create table $TABLE_NAME ("
        for(element in vector_of_variables)
        {
            create_statement += element + " " + variables[element]
            if(vector_of_variables.lastElement() != element)
                create_statement+=" ,"
        }
        for(element in foregin)
        {
            create_statement += ",FOREIGN KEY(${element.key}) REFERENCES ${element.value}"
        }
        create_statement += ")"


        db.execSQL(create_statement)

    }

    /**
     *
     * subroutine gets entire database to vector of hashmap values, self colum feeder
     * function is multi threadded, but until result vector is assigned, the function is blocked
     * @return a vector of the database, each element represents a row, each <key> in hashmap represents col, and it's value is in <value> of the hasmap
     */
    fun get_db(): Vector<HashMap<String, String>>
    {
        val db: SQLiteDatabase = this.readableDatabase
        val vector: Vector<HashMap<String, String>> = Vector()

        val syncToken = Object()
        // to not hang the ui
        Thread({
            @Suppress("CanBeVal")
            var c:Cursor? = null
            try
            {
                c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
                c.moveToFirst()
            }
            catch (e: Exception)
            {
                Log.d("Local SQL helper","Failed for some reason with DB $DATABASE_NAME ${e.message}" )
                synchronized(syncToken)
                {
                    syncToken.notify()
                }

            }
            while (c!=null && !c.isAfterLast)
            {
                val small_map: HashMap<String, String> = HashMap()
                for (variable in vector_of_variables)
                {
                    val input_str = String(themer.xorWithKey(c.getString(c.getColumnIndex(variable)).toByteArray(),themer.get_device_id(context).toByteArray(),true,context))
                    small_map[variable] = input_str
                }
                vector.addElement(small_map)
                c.moveToNext()
            }
            synchronized(syncToken)
            {
                syncToken.notify()
            }
            if(c!=null)
                c.close()
        }).start()


        synchronized(syncToken)
        {
            try
            {
                syncToken.wait()

            } catch (e: InterruptedException)
            {
                Log.d("Local SQL helper","sync done with $DATABASE_NAME")
            }
        }
        //db.close()
        return vector
    }

    /**
     * subroutine that templates an add query, multiple items
     * @param variables the variables that we want to add, each new "data" is in the hashmap, multiple data can be sent from the vector
     * @return if at least one of the data assigned was inserted to the table or updated, returns true, otherwise, false
     */
    protected fun add_data(variables: Vector<HashMap<String,String>>):Boolean
    {
        if(variables.size == 0)
            return false
        val db: SQLiteDatabase = this.writableDatabase
        //db.close()
        return variables.any { add_single_data(db, it) }
    }

    /**
     * adds a single data
     * @param db an instance of the datbase
     * @param items the data we want to add, in format of  <key> = field name, <value> = data
     * @return true if successfull, false otherwise
     */
    private fun add_single_data(db:SQLiteDatabase,items:HashMap<String,String>):Boolean
    {
        val values = ContentValues()
        for(item in items)
            values.put(item.key, String(themer.xorWithKey(item.value.toByteArray(),themer.get_device_id(context).toByteArray(),false,context) ) )
        if(db.insertWithOnConflict(TABLE_NAME,null,values,SQLiteDatabase.CONFLICT_REPLACE)>0)
            return true
        return false
    }

    /**
     * subroutine that templates remove query
     * @param where_clause the where clause string, what field we want to compare on the WHERE statement
     * @param equal_to what we compare it to, can be multiple strings and if at least one of them matchs, row will be removed from table
     * @return true if removal was succesfull, false otherwise
     */
    protected fun remove_from_db( where_clause:String,  equal_to: Array<String>) :Boolean
    {
        var result = false
        for(item in equal_to)
            equal_to[equal_to.indexOf(item)] = String( themer.xorWithKey(item.toByteArray(),themer.get_device_id(context).toByteArray() ,false,context) )
        val db: SQLiteDatabase = this.writableDatabase
        if(db.delete(TABLE_NAME,where_clause + "=?", equal_to) >0)
            result = true
        //db.close()
        return result
    }

    /**
     * subroutine that templates remove query with two or more where "fields"
     * @param where_clause an array of strings that holds the field name(s) that we compare each row to
     * @param equal_to the data that we compare each field to
     * @return true if at least one of the removal were succesfull, false otherwise
     */
    protected fun remove_from_db( where_clause:Array<String>,  equal_to: Array<String>) :Boolean
    {
        var result = false
        for(item in equal_to)
            equal_to[equal_to.indexOf(item)] = String( themer.xorWithKey(item.toByteArray(),themer.get_device_id(context).toByteArray() ,false,context) )
        val db: SQLiteDatabase = this.writableDatabase
        var where_clause_arguemnt = ""
        for(item in where_clause)
        {
            where_clause_arguemnt += item + " =?"
            if(item != where_clause.last())
                where_clause_arguemnt += " AND "
        }
        if(db.delete(TABLE_NAME,where_clause_arguemnt, equal_to) >0)
            result = true
        //db.close()
        return result
    }


    /**
     * subroutine that templates update query
     * @param where_clause the field we compare it to (which row to update)
     * @param equal_to the data to compare it to (which row to update)
     * @param update_to what columns fields update to what (<key> = column name, <value> = data)
     * @return if at least one row was updated, true, otherwise false
     */
    protected fun update_data(where_clause: String, equal_to: Array<String>, update_to: HashMap<String,String>):Boolean
    {
        var result = false
        for(item in equal_to)
            equal_to[equal_to.indexOf(item)] = String( themer.xorWithKey(item.toByteArray(),themer.get_device_id(context).toByteArray() ,false,context) )
        Log.d("Equal to",equal_to[0])
        val db: SQLiteDatabase = this.writableDatabase
        val values = ContentValues()
        for(item in update_to)
            values.put(item.key, String(themer.xorWithKey(item.value.toByteArray(),themer.get_device_id(context).toByteArray(),false,context)))
        if(db.update(TABLE_NAME,values,where_clause + "=?", equal_to)>0)
            result = true
        //db.close()
        return result
    }

    /**
     * Update data on multiple where fields
     * @param equal_to what data to compare the fields data to
     * @param update_to what columns should we update to what, in the format of <key> = field name, <value> = data
     * @param where_clause what columns should we compare to
     * @return if at least one row was updated, true, otherwise false
     */
    protected fun update_data(where_clause: Array<String>, equal_to: Array<String>, update_to: HashMap<String,String>):Boolean
    {
        var result = false
        for(item in equal_to)
            equal_to[equal_to.indexOf(item)] = String( themer.xorWithKey(item.toByteArray(),themer.get_device_id(context).toByteArray() ,false,context) )
        val db: SQLiteDatabase = this.writableDatabase
        val values = ContentValues()
        for(item in update_to)
            values.put(item.key, String(themer.xorWithKey(item.value.toByteArray(),themer.get_device_id(context).toByteArray(),false,context)))
        var where_str = ""
        for(item in where_clause)
        {
            where_str += item + "+? "
            if(item != where_clause.last())
                where_str+=" AND "
        }
        Log.d("Equal to",equal_to[0])
        if(db.update(TABLE_NAME,values,where_str, equal_to)>0)
            result = true
        //db.close()
        return result
    }

    /**
     * subroutine to look for and get a row from the database that accepts certain conditions (ALL)
     * @param map the fields we want to "filter" out on our result vector
     * @return a vector that each element represents a row in the database, and each <key>,<value> pair in the hasmap represents column in the database, <key> being the field name, and <value> being the data
     */
    fun get_rows(map:HashMap<String,String>):Vector<HashMap<String,String>>
    {
        val db = this.readableDatabase
        for(item in map)
            map[item.key] = String(themer.xorWithKey(item.value.toByteArray(),themer.get_device_id(context).toByteArray(),false,context))
        val vector = Vector<HashMap<String, String>>()
        val sync_token = Object()
        //to not hang the ui
        Thread({
            var sql_query = "SELECT * FROM $TABLE_NAME WHERE"
            var breaker = 0
            val where_args:Vector<String> = Vector()
            var where_clause = ""
            for (item in map)
            {
                where_args.addElement(item.value)
                where_clause+=item.key + " = ? "
                sql_query += " ${item.key} = '${item.value}' "
                breaker++
                if (breaker < map.size)
                {
                    where_clause+= " AND "
                    sql_query += " AND "
                }
                else
                    break
            }
            @Suppress("CanBeVal")
            var c:Cursor?
            try
            {
                Log.d("SQL raw query",sql_query)
                //c = db.rawQuery(sql_query, null)
                c = db.query(TABLE_NAME,null,where_clause,where_args.toTypedArray(),null,null,null)
            }
            catch (e:SQLException)
            {
                Log.d("Local SQL helper","SQL Exception $DATABASE_NAME ${e.message}")
                synchronized(sync_token)
                {
                    sync_token.notify()
                }
                return@Thread
            }
            catch (e:IllegalStateException)
            {
                Log.d("Local SQL helper","Illegal State ${e.message}")
                synchronized(sync_token)
                {
                    sync_token.notify()
                }
                return@Thread
            }
            try
            {
                c.moveToFirst()
            }
            catch (e: Exception)
            {
                Log.d("Local SQL helper","Error syncing ${e.message}")
                synchronized(sync_token)
                {
                    sync_token.notify()
                }
            }
            if(c!=null)
            {
                try
                {
                    while (!c.isAfterLast) {
                        val small_map: HashMap<String, String> = HashMap()
                        for (variable in vector_of_variables) {
                            val item = c.getString(c.getColumnIndex(variable))
                            val str_item = String(themer.xorWithKey(item.toByteArray(),themer.get_device_id(context).toByteArray(),true,context))
                            small_map[variable] = str_item
                        }
                        vector.addElement(small_map)
                        c.moveToNext()
                    }
                }
                catch (e:IllegalStateException)
                {
                    Log.d("local","Couldn't grab SQL table")
                }
            }
            synchronized(sync_token)
            {
                sync_token.notify()
            }

            if (c != null)
            {
                Log.d("Where Query",c.count.toString())
                c.close()
            }
        }).start()

        synchronized(sync_token)
        {
            try
            {
                sync_token.wait()
            }
            catch (e:InterruptedException)
            {
                Log.d("Local SQL helper","sync done with $DATABASE_NAME")
            }
        }

        //db.close()
        return vector
    }


    /**
     *
     * subroutine gets entire database to vector of hashmap values, self colum feeder
     * includes sorting
     * sorts by a certain field name
     * @param isAscending true = Ascending sortage, false = Descending
     * @param sort_by_value by which field name should we sort by?
     * @return vector that each element represents a row and each <key>,<value> pair in the hashmap represents a column, <key> being field name, <value> being data
     */
    @Suppress("unused")
    protected fun get_db(sort_by_value: String, isAscending: Boolean): Vector<HashMap<String, String>>
    {
        val db: SQLiteDatabase = this.readableDatabase
        val vector: Vector<HashMap<String, String>> = Vector()

        val syncToken = Object()
        // to not hang the ui
        Thread({
            val ascending_descending_str: String = if (isAscending) {
                " ASC"
            } else {
                " DESC"
            }
            val c = db.query(TABLE_NAME,null,null,null,null,null,sort_by_value+ascending_descending_str)
            try
            {
                c.moveToFirst()
            }
            catch (e: Exception)
            {
                Log.d("Local SQL helper","SQL Error with $DATABASE_NAME ${e.message}")
                synchronized(syncToken)
                {
                    syncToken.notify()
                }
            }
            while (c.isAfterLast.not())
            {
                val small_map: HashMap<String, String> = HashMap()
                for (variable in vector_of_variables)
                {
                    val item = c.getString(c.getColumnIndex(variable))
                    val item_str = String(themer.xorWithKey(item.toByteArray(),themer.get_device_id(context).toByteArray(),true,context))
                    small_map[variable] = item_str
                }
                vector.addElement(small_map)
                c.moveToNext()
            }
            synchronized(syncToken)
            {
                syncToken.notify()
            }
            c.close()
        }).start()


        synchronized(syncToken)
        {
            try
            {
                syncToken.wait()

            } catch (e: InterruptedException)
            {
                Log.d("Local SQL helper","sync done with $DATABASE_NAME")
            }
        }
        //db.close()
        return vector
    }


}