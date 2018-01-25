package com.example.chaosruler.githubclient.activities.Settings

import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceActivity
import android.support.annotation.LayoutRes
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup

/**
 * A [android.preference.PreferenceActivity] which implements and proxies the necessary calls
 * to be used with AppCompat.
 */
abstract class AppCompatPreferenceActivity : PreferenceActivity() {

    /**
     * on creating a new view, generating views
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.installViewFactory()
        delegate.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    /**
     * adds post-data (such as summaries)
     */
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delegate.onPostCreate(savedInstanceState)
    }

    /**
     * representation of support action bar
     */
    val supportActionBar: ActionBar?
        get() = delegate.supportActionBar

    /**
     * sets a new support action bar
     */
    @Suppress("unused")
    fun setSupportActionBar(toolbar: Toolbar?) {
        delegate.setSupportActionBar(toolbar)
    }

    /**
     * get the menu inflater if required
     */
    override fun getMenuInflater(): MenuInflater {
        return delegate.menuInflater
    }

    /**
     * set the layout from layout id
     */
    override fun setContentView(@LayoutRes layoutResID: Int) {
        delegate.setContentView(layoutResID)
    }

    /**
     * sets the layout from view
     */
    override fun setContentView(view: View) {
        delegate.setContentView(view)
    }

    /**
     * sets the layout from view and parameters
     */
    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
        delegate.setContentView(view, params)
    }

    /**
     * adds a new layout to this from view and parameters
     */
    override fun addContentView(view: View, params: ViewGroup.LayoutParams) {
        delegate.addContentView(view, params)
    }

    /**
     * when returning to this, we should refresh headers
     */
    override fun onPostResume() {
        super.onPostResume()
        delegate.onPostResume()
    }

    /**
     * when configuration changed titles we should load that
     */
    override fun onTitleChanged(title: CharSequence, color: Int) {
        super.onTitleChanged(title, color)
        delegate.setTitle(title)
    }

    /**
     * when configuration itself changed we should load that
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        delegate.onConfigurationChanged(newConfig)
    }

    override fun onStop() {
        super.onStop()
        delegate.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        delegate.onDestroy()
    }

    override fun invalidateOptionsMenu() {
        delegate.invalidateOptionsMenu()
    }

    private val delegate: AppCompatDelegate by lazy {
        AppCompatDelegate.create(this, null)
    }
}
