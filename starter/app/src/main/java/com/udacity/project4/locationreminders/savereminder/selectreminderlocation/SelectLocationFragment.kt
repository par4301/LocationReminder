package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var marker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        saveLocationHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapView: MapView = view.findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
    }

    private fun setMapMarkerLongClick() {
        googleMap.setOnMapLongClickListener { latLng ->
            marker?.remove()
            marker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            )
            _viewModel.reminderSelectedLocationStr.value =
                "%.5f, %.5f".format(latLng.latitude, latLng.longitude)
            _viewModel.reminderLatitude.value = latLng.latitude
            _viewModel.reminderLongitude.value = latLng.longitude
        }
    }

    private fun setPoiClick() {
        googleMap.setOnPoiClickListener { poi ->
            marker?.remove()
            marker = googleMap.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            )
            _viewModel.reminderSelectedLocationStr.value = poi.name
            _viewModel.reminderLatitude.value = poi.latLng.latitude
            _viewModel.reminderLongitude.value = poi.latLng.longitude
        }
    }

    private fun saveLocationHandler() {
        binding.saveButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        showMyCurrentLocation()
        setPoiClick()
        setMapMarkerLongClick()
    }

    @SuppressLint("MissingPermission")
    private fun showMyCurrentLocation() {
        if (isLocationPermissionGranted()) {
            googleMap.isMyLocationEnabled = true
            val location: Task<Location> = fusedLocationProviderClient.lastLocation
            location.addOnCompleteListener {
                if (it.isSuccessful) {
                    val lastKnownLocation = it.result
                    if (lastKnownLocation != null) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude),
                            DEFAULT_ZOOM_LEVEL
                        ))
                    }
                } else {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE),
                        DEFAULT_ZOOM_LEVEL
                    ))
                }
            }
        }
        else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun isLocationPermissionGranted() : Boolean {
        return ActivityCompat.checkSelfPermission( requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}

private val REQUEST_LOCATION_PERMISSION = 1
private val DEFAULT_ZOOM_LEVEL = 17f
private val DEFAULT_LATITUDE = 43.785294
private val DEFAULT_LONGITUDE =  -110.698560