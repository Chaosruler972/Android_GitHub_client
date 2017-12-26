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
 * A simple [Fragment] subclass.
 */
class search_users_by_location : Fragment(), LocationListener
{


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater?.inflate(R.layout.fragment_search_users_by_location, container, false)

    private lateinit var locationManager:LocationManager
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        if(context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
        {
            search_user_text.setText(getString(R.string.location_not_granted))
            return
        }
        val main_progressbar = MainActivity.act.findViewById(R.id.main_progressbar) as ProgressBar
        main_progressbar.visibility = ProgressBar.VISIBLE
        locationManager = context.getSystemService(LOCATION_SERVICE) as android.location.LocationManager
        locationManager.requestSingleUpdate(best_location_provider(), this, null)

    }


    private fun best_location_provider():String
    {
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_MEDIUM // grab accuracy
        criteria.isAltitudeRequired = true // grab requirements
        criteria.powerRequirement = Criteria.POWER_HIGH // set requiremen'ts
        criteria.isCostAllowed = true

        return locationManager.getBestProvider(criteria, false)
    }



    override fun onLocationChanged(location: Location?)
    {
        val main_progressbar = MainActivity.act.findViewById(R.id.main_progressbar) as ProgressBar

        try
        {

            if (location != null)
            {
                val gcd = Geocoder(context, Locale.getDefault())
                val address = gcd.getFromLocation(location.latitude, location.longitude, 1)
                if (address.isEmpty()) {
                    search_user_text.setText(getString(R.string.couldnt_get_country))
                    main_progressbar.visibility = ProgressBar.INVISIBLE
                    return
                }
                val countryname = address[0].countryName
                search_user_text.setText(countryname)

                Thread {
                    val users = GitHub_remote_service.search_user_by_location(countryname, context, 1)
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

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

    companion object
    {

        @Suppress("unused")
        fun newInstance(): search_users_by_location {
            return search_users_by_location()
        }
    }

}// Required empty public constructor
