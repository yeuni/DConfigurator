package nougattechnologies.com.dboylive

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.LatLng
import nougattechnologies.com.dboylive.Model.User
import nougattechnologies.com.dboylive.Model.UserLocation
import nougattechnologies.com.dboylive.Rretrofit.IDrinkShopAPI
import nougattechnologies.com.dboylive.Utils.Common
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class DBoyFragment : Fragment(), OnMapReadyCallback, OnInfoWindowClickListener {
    var googleMap: GoogleMap? = null
    var view: View? = null
    private val mMap: GoogleMap? = null
    var mService: IDrinkShopAPI? = null
    private var mMapView: MapView? = null
    private val mMapContainer: RelativeLayout? = null
    private val mUserList = ArrayList<User>()
    private val mUserListRecyclerView: RecyclerView? = null
    private var mGoogleMap: GoogleMap? = null
    private val mUserLocations = ArrayList<UserLocation>()
    private val mUserPosition: UserLocation? = null
    private var mMapBoundary: LatLngBounds? = null
    private val mMapLayoutState = 0
    private val mHandler = Handler()
    private val mRunnable: Runnable? = null
    private var mGeoApiContext: GeoApiContext? = null
    var userphone: String? = null

    //private UserLocation mUs;erPosition;
    var userlatitude: Double? = null
    var currentuserlong: Double? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dboy, container, false)
        mMapView = view.findViewById(R.id.user_list_map)
        mService = Common.aPI
        // compositeDisposable= new CompositeDisposable();
        initGoogleMap(savedInstanceState)
        val preferences = activity!!.getSharedPreferences("MyPreferences_001", Context.MODE_PRIVATE)
        userphone = preferences.getString("currentuserphone", null)
        userlatitude = java.lang.Double.valueOf(preferences.getString("currentuserlat", null))
        currentuserlong = java.lang.Double.valueOf(preferences.getString("currentuserlong", null))
        //
//
//
//        }
        return view
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) {


        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(Constants.MAPVIEW_BUNDLE_KEY)
        }
        mMapView!!.onCreate(mapViewBundle)
        mMapView!!.getMapAsync(this)
        if (mGeoApiContext == null) {
            mGeoApiContext = GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_api_key))
                    .build()
        }
    }

    private fun setCameraView() {

        // Set a boundary to start
        val bottomBoundary = userlatitude!! - .1
        val leftBoundary = currentuserlong!! - .1
        val topBoundary = userlatitude!! + .1
        val rightBoundary = currentuserlong!! + .1
        mMapBoundary = LatLngBounds(
                com.google.android.gms.maps.model.LatLng(bottomBoundary, leftBoundary),
                com.google.android.gms.maps.model.LatLng(topBoundary, rightBoundary)
        )

        //mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(your_location, 17.0f));
        mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 8))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(Constants.MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(Constants.MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
    }

    override fun onResume() {
        super.onResume()
        mMapView!!.onResume()
        // startUserLocationsRunnable(); // update user locations every 'LOCATION_UPDATE_INTERVAL'
    }

    override fun onStart() {
        super.onStart()
        mMapView!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView!!.onStop()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("ANGALIA", "LAT: $userlatitude")
        Log.d("ANGALIA", "LONG: $currentuserlong")
        val your_location = com.google.android.gms.maps.model.LatLng(userlatitude!!, currentuserlong!!)
        googleMap.addMarker(MarkerOptions()
                .position(your_location).title("Your Location")
                .snippet(userphone)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dboyicon)))


//        //create marker
//        LatLng ss = new LatLng(-3.36535, 36.688541);
//        MarkerOptions marker = new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dboy))
//                .position(ss)
//                .snippet("Delivery Boy")
//                .title("John Smith");
//
//// adding marker
//        googleMap.addMarker(marker);


        // googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        googleMap.uiSettings.isZoomControlsEnabled = true
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        googleMap.isMyLocationEnabled = false
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(com.google.android.gms.maps.model.LatLng(userlatitude!!, currentuserlong!!), 20.0f))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(20.0f))
        mGoogleMap = googleMap
        //        mGoogleMap.setOnInfoWindowClickListener(this);
        setCameraView()

//        addMapMarkers();
    }

    override fun onPause() {
        mMapView!!.onPause()
        // stopLocationUpdates(); // stop updating user locations
        super.onPause()
    }

    override fun onDestroy() {
        mMapView!!.onDestroy()
        //startUserLocationsRunnable();
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView!!.onLowMemory()
    }

    override fun onInfoWindowClick(marker: Marker) {
        if (marker.snippet == "This is you") {
            marker.hideInfoWindow()
        } else {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(marker.snippet)
                    .setCancelable(true)
                    .setPositiveButton("Yes") { dialog, id -> //                                resetSelectedMarker();
//                                mSelectedMarker = marker;
                        calculateDirections(marker)
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, id -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun calculateDirections(marker: Marker) {
        Log.d(TAG, "calculateDirections: calculating directions.")
        val destination = LatLng(
                marker.position.latitude,
                marker.position.longitude
        )
        val directions = DirectionsApiRequest(mGeoApiContext)
        directions.alternatives(true)
        directions.origin(
                LatLng(
                        userlatitude!!, currentuserlong!!
                )
        )
        Log.d(TAG, "calculateDirections: destination: $destination")
        directions.destination(destination).setCallback(object : PendingResult.Callback<DirectionsResult> {
            override fun onResult(result: DirectionsResult) {
                Log.d("YANGUU", "calculateDirections: routes: " + result.routes[0].toString())
                Log.d("YANGUU", "calculateDirections: duration: " + result.routes[0].legs[0].duration)
                Log.d("YANGUU", "calculateDirections: distance: " + result.routes[0].legs[0].distance)
                Log.d("YANGUU", "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString())
                Log.d(TAG, "onResult: successfully retrieved directions.")
                // addPolylinesToMap(result);
            }

            override fun onFailure(e: Throwable) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.message)
            }
        })
    } //    private void resetSelectedMarker(){

    //        if(mSelectedMarker != null){
    //            mSelectedMarker.setVisible(true);
    //            mSelectedMarker = null;
    //            removeTripMarkers();
    //        }
    //    }
    companion object {
        private const val TAG = "Userlisfrag"
        private const val MAP_LAYOUT_STATE_CONTRACTED = 0
        private const val MAP_LAYOUT_STATE_EXPANDED = 1
        private const val LOCATION_UPDATE_INTERVAL = 3000
        @JvmStatic
        fun newInstance(): DBoyFragment {
            return DBoyFragment()
        }
    }
}