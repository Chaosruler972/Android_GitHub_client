package com.example.chaosruler.githubclient.fragments.fragments.search_users_by_location


import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chaosruler.githubclient.R
import kotlinx.android.synthetic.main.fragment_search_users_by_location.*
import android.util.Log
import android.widget.ProgressBar
import com.example.chaosruler.githubclient.activities.MainActivity
import com.example.chaosruler.githubclient.services.GitHub_remote_service
import java.util.*


/**
 * A simple Search by user fragment subclass, implenets location listener to grab location to search by it
 */
class search_users_by_location : Fragment(), LocationListener
{


    /**
     * inflates the view
     * @param container the container of this fragment (activity view holder)
     * @param inflater the inflater in chrage of infalting this view
     * @param savedInstanceState the last state of this fragment
     * @return a view of this fragment
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater?.inflate(R.layout.fragment_search_users_by_location, container, false)

    /**
     * the location manager responsible to get an update of the current location
     */
    private lateinit var locationManager:LocationManager
    /**
     * gets a location update and update list with the closest users to me
     * @param savedInstanceState the last state of the fragment
     */
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        if(context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            search_user_text.setText(getString(R.string.location_not_granted))
            return
        }
        val main_progressbar = MainActivity.act.findViewById(R.id.main_progressbar) as ProgressBar
        main_progressbar.visibility = ProgressBar.VISIBLE
        locationManager = context.getSystemService(LOCATION_SERVICE) as android.location.LocationManager
        //locationManager.requestLocationUpdates(best_location_provider(),0.toLong(),0.toFloat(), this, null)
        //locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,this,null)
        //locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,this,null)
        var l:Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if(l == null)
            l=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if(l!=null)
            onLocationChanged(l)
        else
        {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,this,null)
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,this,null)
        }
        //onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER))

        Log.d("Location","Request sent")
       // onLocationChanged(locationManager.getLastKnownLocation(best_location_provider())) // just in case
    }


    /**
     * a simple function to get better provider for the location query
     * @return the better provider for location
     */
    @Suppress("unused")
    private fun best_location_provider():String
    {
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_MEDIUM // grab accuracy
        criteria.isAltitudeRequired = true // grab requirements
        criteria.powerRequirement = Criteria.POWER_HIGH // set requiremen'ts
        criteria.isCostAllowed = true

        return locationManager.getBestProvider(criteria, false)
        //return LocationManager.GPS_PROVIDER
    }


    /**
     * if location was updated, we should query by this location (called once!)
     * @param location the location that was updated to me, null if no location present
     */
    override fun onLocationChanged(location: Location?)
    {
        Log.d("Location","Location recieved")
        val main_progressbar = MainActivity.act.findViewById(R.id.main_progressbar) as ProgressBar

        if(search_users_listview.adapter != null && search_users_listview.adapter.count > 0)
            return
        try
        {
            if (location != null)
            {
                if(!Geocoder.isPresent())
                {
                    Log.d("Geocoder","Not present")
                    return
                }
                locationManager.removeUpdates(this)
                val gcd = Geocoder(context, Locale.getDefault())
                val address = gcd.getFromLocation(location.latitude, location.longitude, 1)
                if (address.isEmpty())
                {
                    search_user_text.setText(getString(R.string.couldnt_get_country))
                    main_progressbar.visibility = ProgressBar.INVISIBLE
                    return
                }
                val countryname = address[0].countryName
                search_user_text.setText(countryname)
                Log.d("Location","Grabbed - $countryname")
                Thread {
                    Log.d("Location","getting users")
                    val users = GitHub_remote_service.search_user_by_location(countryname, context, 1)
                    Log.d("Location","Recieved users")
                    MainActivity.act.runOnUiThread {
                        try {
                            search_users_listview.adapter = array_adapter(context, users)
                        } catch (e: Exception) {
                            Log.d("Kotlin", "Null before assert thingy")
                        }
                        main_progressbar.visibility = ProgressBar.INVISIBLE
                    }
                }.start()
            } else {
                main_progressbar.visibility = ProgressBar.INVISIBLE
                search_user_text.setText(getString(R.string.no_location))
                Log.d("Location", "Was null")
            }
        }
        catch (e:Exception)
        {
            main_progressbar.visibility = ProgressBar.INVISIBLE
            search_user_text.setText(getString(R.string.no_location))
        }
    }

    /**
     * api implentation that I had to override
     */
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }
    /**
     * api implentation that I had to override
     */
    override fun onProviderEnabled(provider: String?) {
    }
    /**
     * api implentation that I had to override
     */
    override fun onProviderDisabled(provider: String?) {
    }

    /**
     * when we destroy this fragment, we should close the location service update request
     */
    override fun onDestroy()
    {
        @Suppress("SENSELESS_COMPARISON")
        if(locationManager!=null)
            locationManager.removeUpdates(this)
        super.onDestroy()
    }
    companion object
    {
        /**
         *  generator in a singleton-style of way, only this can be multi-instanced
         *  @return a instance of this fragment with that data sent
         */
        @Suppress("unused")
        fun newInstance(): search_users_by_location {
            return search_users_by_location()
        }
    }

}// Required empty public constructor
