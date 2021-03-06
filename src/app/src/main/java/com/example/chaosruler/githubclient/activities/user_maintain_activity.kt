package com.example.chaosruler.githubclient.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.chaosruler.githubclient.R
import com.example.chaosruler.githubclient.SQLite.user_database_helper
import com.example.chaosruler.githubclient.dataclasses.User
import com.example.chaosruler.githubclient.services.themer
import kotlinx.android.synthetic.main.activity_user_maintain_activity.*

/**
 * an activity to maintain user and its database
 */
class user_maintain_activity : Activity() {

    /**
     * the db that has all the users
     */
    private lateinit var db: user_database_helper
    /**
     * the adapter that populates the spinner
     */
    private lateinit var adapter: ArrayAdapter<User>

    /**
     * initates all the logic by calling subroutines
     */
    override fun onCreate(savedInstanceState: Bundle?)
    {
        setTheme(themer.style(baseContext))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_maintain_activity)
        init_dbs()
        init_spinner()
        init_buttons()
    }

    /**
     *   inits dataases
     */
    private fun init_dbs()
    {
        db = user_database_helper(baseContext)
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, db.get_entire_db())
    }

    /**
     *    inits spinner
     */
    private fun init_spinner()
    {
        val users = db.get_entire_db()
        delete_spinner.adapter = adapter
        delete_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                // upon Spinner selecting a user, update the other fields
                delete_password1_edittext.setText(users.elementAt(i).get__password())
                delete_delete.isEnabled = true
                delete_password2_textview.isEnabled = true
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
                // case there was nothing to select (empty database)
                delete_password1_edittext.text.clear()
                delete_delete.isEnabled = false
                delete_password2_textview.isEnabled = false
            }
        }
    }

    /**
     * inits buttons
     */
    private fun init_buttons()
    {
        // button to activate subroutine to delete a user from database
        delete_delete.setOnClickListener({
            db.delete_user( (delete_spinner.selectedItem as User).get__username())
            adapter.remove(delete_spinner.selectedItem as User)
        })

        // button to activate the changing password mechanism and subroutine, in actual there's a hidden change password button and password confirm field
        // after pressing this button those fields become visible which allows the user to actually change the password
        delete_change_password.setOnClickListener({
            delete_password1_edittext.isEnabled = true
            delete_password2_edittext.visibility = View.VISIBLE
            delete_password2_textview.visibility = View.VISIBLE
            delete_send_changes_btn.visibility = View.VISIBLE
        })
        // part 2 of the subroutine to change the password - the actual job
        delete_send_changes_btn.setOnClickListener(View.OnClickListener {
            val new_pass = delete_password1_edittext.text.toString() // grabs the input password
            when {
                new_pass.isEmpty()
                    // checks validity
                -> {
                    Toast.makeText(this@user_maintain_activity, resources.getString(R.string.delete_empty_pw), Toast.LENGTH_SHORT).show() // invalid password was confirmed.
                    reset_password_fields()
                    return@OnClickListener
                }
                new_pass != delete_password2_edittext.text.toString()
                    // password field and password confirm field didn't match, user mistyped the password in this case
                -> {
                    Toast.makeText(this@user_maintain_activity, resources.getString(R.string.delete_mismatch), Toast.LENGTH_SHORT).show()
                    reset_password_fields()
                    return@OnClickListener
                }
                else -> {
                    Toast.makeText(this@user_maintain_activity, resources.getString(R.string.delete_confirmed), Toast.LENGTH_SHORT).show() // confirmed match, this is when action is sent and confirmed
                    db.update_user((delete_spinner.selectedItem as User).get__username(), new_pass)
                    reset_password_fields()
                    return@OnClickListener
                }
            }
        })
    }

    /**
     *  inits reset both password fields
     */
    private fun reset_password_fields() {
        // subroutine to reset the password fields to their defaults (meaning like it was when the activity first launched)
        delete_password1_edittext.isEnabled = false
        delete_password2_edittext.visibility = View.INVISIBLE
        delete_password2_textview.visibility = View.INVISIBLE
        delete_password2_textview.text.javaClass
        delete_send_changes_btn.visibility = View.INVISIBLE
    }

    /**
     * inits go back to new login activity
     */
    override fun onBackPressed() // overridden to make sure that pressing back right now will return us to the login activity, and won't exit the app, also reloading the login activity will reload the spinner on the login activity
    {
        val intent = Intent(this@user_maintain_activity, LoginActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }




}
