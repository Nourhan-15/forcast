package com.example.forcast247

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.forcast247_app.weather.utils.getAddressGeocoder
import com.example.forcast247_app.weather.viewModel.SettingViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.forcast247_app.weather.view.fragments.setting.MapFragment.SettingWeather

class MapsFragment : Fragment(R.layout.fragment_maps) {


    private lateinit var mMap: GoogleMap
    private val LOCATION_REQUEST_CODE = 101

    lateinit var model: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        model = ViewModelProvider(requireActivity()).get(SettingViewModel::class.java)

    }


    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        if (mMap != null) {
            val permission = context?.let {
                ContextCompat.checkSelfPermission(it,
                    Manifest.permission.ACCESS_FINE_LOCATION)
            }

            if (permission == PackageManager.PERMISSION_GRANTED) {
                mMap?.isMyLocationEnabled = true
            } else {
                requestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_REQUEST_CODE)
            }
        }

        val mapSettings = mMap?.uiSettings

        mapSettings?.isZoomControlsEnabled = true

        mapSettings?.isZoomGesturesEnabled = true

        mMap.setOnMapClickListener { latLon ->
            mMap.clear()

            var getcoordinates = LatLng(latLon.latitude, latLon.longitude)
            context?.let {
                val title = getAddressGeocoder(latLon.latitude, latLon.longitude, it)
                model.setAddressData(title)
                model.setLatData(latLon.latitude)
                model.setLonData(latLon.longitude)
                loadFragment(SettingWeather())
                val markerOption = MarkerOptions().position(getcoordinates)
                markerOption.title(title)

            }



            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(getcoordinates, 4f))
            mMap.addMarker(MarkerOptions()
                .position(getcoordinates)
                .title(getcoordinates.toString())
                .snippet("LatLon"))


        }

    }


    private fun loadFragment(fragment: Fragment) {
        val fm = fragmentManager
        val fragmentTransaction: FragmentTransaction = fm!!.beginTransaction().addToBackStack(null)
        fragmentTransaction.replace(R.id.relativeMap, fragment)
        fragmentTransaction.commit() // save the changes
    }


    fun requestPermission(permissionType: String, requestCode: Int) {

        ActivityCompat.requestPermissions(context as Activity, arrayOf(permissionType), requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {

        when (requestCode) {
            LOCATION_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(context, R.string.permission.toString(),
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }


}