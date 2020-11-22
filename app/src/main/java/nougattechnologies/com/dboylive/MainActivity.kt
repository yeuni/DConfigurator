package nougattechnologies.com.dboylive

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.reactivex.disposables.CompositeDisposable
import nougattechnologies.com.dboylive.DBoyFragment.Companion.newInstance
import nougattechnologies.com.dboylive.MainActivity
import nougattechnologies.com.dboylive.Model.UserLocation
import nougattechnologies.com.dboylive.Rretrofit.IDrinkShopAPI
import nougattechnologies.com.dboylive.Services.LocationService
import nougattechnologies.com.dboylive.Utils.Common
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var mLocationPermissionGranted = false
    var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    var mycurrentlaitude: String? = null
    var mycurrentlongitude: String? = null
    var gettedphone: String? = null
    var currentuserphone: String? = null
    var mService: IDrinkShopAPI? = null
    var compositeDisposable: CompositeDisposable? = null
    private val mUserLocations = ArrayList<UserLocation>()
    val getteduserlatittude: String? = null
    val getteduserlongitude: String? = null
    val sharedpreferences: SharedPreferences? = null
    val mySharedPreferences: SharedPreferences? = null
    val editor: SharedPreferences.Editor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        mService = Common.aPI
        compositeDisposable = CompositeDisposable()
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        currentuserphone = "2019"
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun startLocationService() {
        if (!isLocationServiceRunning) {
            val serviceIntent = Intent(this, LocationService::class.java)
            //        this.startService(serviceIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }
    }

    private val isLocationServiceRunning: Boolean
        private get() {
            val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (" nougattechnologies.com.dboylive.Services.LocationService" == service.service.className) {
                    Log.d(TAG, "isLocationServiceRunning: location service is already running.")
                    return true
                }
            }
            Log.d(TAG, "isLocationServiceRunning: location service is not running.")
            return false
        }

    private fun checkMapServices(): Boolean {
        if (isServicesOK) {
            if (isMapsEnabled) {
                return true
            }
        }
        return false
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    val enableGpsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(enableGpsIntent, Constants.PERMISSIONS_REQUEST_ENABLE_GPS)
                }
        val alert = builder.create()
        alert.show()
    }

    val isMapsEnabled: Boolean
        get() {
            val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps()
                return false
            }
            return true
        }//            getChatrooms();
//            getUserDeatails();

    /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
    private val locationPermission: Unit
        private get() {
            /*
             * Request location permission, so that we can get the location of the
             * device. The result of the permission request is handled by a callback,
             * onRequestPermissionsResult.
             */
            if (ContextCompat.checkSelfPermission(this.applicationContext,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true
                Toast.makeText(this, "Good perm", Toast.LENGTH_SHORT).show()
                lastKnownLocation
                //            getChatrooms();
//            getUserDeatails();
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            }
        }//an error occured but we can resolve it

    //everything is fine and the user can make map requests
    val isServicesOK: Boolean
        get() {
            Log.d(TAG, "isServicesOK: checking google services version")
            val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this@MainActivity)
            if (available == ConnectionResult.SUCCESS) {
                //everything is fine and the user can make map requests
                Log.d(TAG, "isServicesOK: Google Play Services is working")
                return true
            } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
                //an error occured but we can resolve it
                Log.d(TAG, "isServicesOK: an error occured but we can fix it")
                val dialog = GoogleApiAvailability.getInstance().getErrorDialog(this@MainActivity, available, Constants.ERROR_DIALOG_REQUEST)
                dialog.show()
            } else {
                Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show()
            }
            return false
        }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        mLocationPermissionGranted = false
        when (requestCode) {
            Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: called.")
        when (requestCode) {
            Constants.PERMISSIONS_REQUEST_ENABLE_GPS -> {
                if (mLocationPermissionGranted) {
                    Toast.makeText(this, "Good Permission Granted", Toast.LENGTH_SHORT).show()


                    //to
                    lastKnownLocation
                    //                    getChatrooms();
                    //  getUserDeatails();
                } else {
                    locationPermission
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                Toast.makeText(this, "On REsume Granted successfully", Toast.LENGTH_SHORT).show()
                lastKnownLocation
                //                getChatrooms();
//                getUserDeatails();
            } else {
                locationPermission
            }
        }
    }

    //  GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
    private val lastKnownLocation: Unit
        private get() {
            Log.d(TAG, "getLastKnownLocation :called.")
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            mFusedLocationProviderClient!!.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val location = task.result
                    //  GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mycurrentlaitude = location!!.latitude.toString()
                    mycurrentlongitude = location.longitude.toString()
                    Log.d(TAG, "OnComplete: latitude: " + location.latitude)
                    Log.d(TAG, "OnComplete: longitude: " + location.longitude)
                    Toast.makeText(this@MainActivity, "CurrentLatitudeAndLongitude$mycurrentlaitude$mycurrentlongitude", Toast.LENGTH_SHORT).show()
                    updateUserLocationTOServer()
                    mySharedPreferences = getSharedPreferences(MYPREFERENCES, Activity.MODE_PRIVATE)
                    editor = mySharedPreferences.edit()
                    editor.putString("currentuserphone", currentuserphone)
                    editor.putString("currentuserlat", mycurrentlaitude)
                    editor.putString("currentuserlong", mycurrentlongitude)
                    editor.apply()
                    Log.d("EMBUYAMAIN", "simu: USERPHONE: $currentuserphone")
                    Log.d("EMBUYAMAIN", "updateUserLocationTOServer: LATITUDE: $mycurrentlaitude")
                    Log.d("EMBUYAMAIN", "updateUserLocationTOServer: LONGITUDE: $mycurrentlongitude")
                    startLocationService()
                } else {
                    locationPermission
                }
            }
        }

    fun updateUserLocationTOServer() {
        Log.d(TAG, "updateUserLocationTOServer: USERPHONE: $currentuserphone")
        Log.d(TAG, "updateUserLocationTOServer: LATITUDE: $mycurrentlaitude")
        Log.d(TAG, "updateUserLocationTOServer: LONGITUDE: $mycurrentlongitude")
        mService!!.updateDboy(currentuserphone,
                mycurrentlaitude, mycurrentlongitude)
                .enqueue(object : Callback<String?> {
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        Log.d("SIYOKOSA", response.toString())
                        Toast.makeText(this@MainActivity, "Successfull update user current location", Toast.LENGTH_SHORT).show()
                        // getUSerDetails();
                    }

                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        Log.d("KOSAAA", t.message)
                        Toast.makeText(this@MainActivity, "," + t.message, Toast.LENGTH_SHORT).show()
                    }
                })
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
        } else if (id == R.id.nav_slideshow) {
        } else if (id == R.id.nav_manage) {
        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_send) {
        }
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun ClickedLivelocations(view: View?) {
        inflateDeliveryBoyFragment()
    }

    private fun inflateDeliveryBoyFragment() {
        //startActivity(new Intent(Homectivity.this,MapsActivity.class));

//        Fragment mFragment = null;
//        mFragment = new DeliveryBoysFragment();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.frameLayout, mFragment).commit();
//
        val fragment = newInstance()
        val bundle = Bundle()
        bundle.putString("intent_deliveryboy", "delivery boy 1")
        fragment.arguments = bundle
        val transaction = supportFragmentManager.beginTransaction()
        // transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        transaction.replace(R.id.user_list_container, fragment, getString(R.string.fragment_user_list))
        transaction.addToBackStack(getString(R.string.fragment_user_list))
        transaction.commit()
    }

    companion object {
        private const val TAG = "HomeActivty"
        const val MYPREFERENCES = "MyPreferences_001"
    }
}