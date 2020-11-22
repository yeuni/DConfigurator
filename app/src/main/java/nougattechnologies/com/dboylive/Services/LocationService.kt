package nougattechnologies.com.dboylive.Services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.android.gms.location.*
import nougattechnologies.com.dboylive.Rretrofit.IDrinkShopAPI
import nougattechnologies.com.dboylive.Utils.Common.aPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationService : Service() {
    var mycurrentlaitude: String? = null
    var mycurrentlongitude: String? = null
    var gettedphone: String? = null
    var mService: IDrinkShopAPI? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    var currentuserphone: String? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mService = aPI
        currentuserphone = "2019"
        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "my_channel_01"
            val channel = NotificationChannel(CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build()
            startForeground(1, notification)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: called.")
        location
        return START_NOT_STICKY
    }//dboy current loc
    // Looper.myLooper tells this to repeat forever until thread is destroyed

    // ---------------------------------- LocationRequest ------------------------------------
    // Create the location request to start receiving updates
    private val location:


    // new Google API SDK v11 uses getFusedLocationProviderClient(this)
            Unit
        private get() {

            // ---------------------------------- LocationRequest ------------------------------------
            // Create the location request to start receiving updates
            val mLocationRequestHighAccuracy = LocationRequest()
            mLocationRequestHighAccuracy.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            mLocationRequestHighAccuracy.interval = UPDATE_INTERVAL
            mLocationRequestHighAccuracy.fastestInterval = FASTEST_INTERVAL


            // new Google API SDK v11 uses getFusedLocationProviderClient(this)
            if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getLocation: stopping the location service.")
                stopSelf()
                return
            }
            Log.d(TAG, "getLocation: getting location information.")
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequestHighAccuracy, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
//dboy current loc
                    Log.d(TAG, "onLocationResult: got location result.")
                    val location = locationResult.lastLocation
                    if (location != null) {
                        mycurrentlaitude = location.latitude.toString()
                        mycurrentlongitude = location.longitude.toString()
                        Log.d(TAG, "OnComplete: latitude: " + location.latitude)
                        Log.d(TAG, "OnComplete: longitude: " + location.longitude)
                        saveDBoyLocation()
                    }
                }
            },
                    Looper.myLooper()) // Looper.myLooper tells this to repeat forever until thread is destroyed
        }

    private fun saveDBoyLocation() {
        Log.d(TAG, "updateUserLocationTOServer: USERPHONE: $currentuserphone")
        Log.d(TAG, "updateUserLocationTOServer: LATITUDE: $mycurrentlaitude")
        Log.d(TAG, "updateUserLocationTOServer: LONGITUDE: $mycurrentlongitude")
        mService!!.updateDboy(currentuserphone,
                mycurrentlaitude, mycurrentlongitude)
                .enqueue(object : Callback<String?> {
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        Log.d("SIYOKOSA", response.toString())
                        // Toast.makeText(Loc.this, "Successfull update user current location", Toast.LENGTH_SHORT).show();
                        // getUSerDetails();
                    }

                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        Log.d("KOSAAA", t.message)
                        stopSelf()
                        /// Toast.makeText(MainActivity.this, ","+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
    } //    private void saveUserLocation(final UserLocation userLocation){

    //
    //        try{
    //            DocumentReference locationRef = FirebaseFirestore.getInstance()
    //                    .collection(getString(R.string.collection_user_locations))
    //                    .document(FirebaseAuth.getInstance().getUid());
    //
    //            locationRef.set(userLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
    //                @Override
    //                public void onComplete(@NonNull Task<Void> task) {
    //                    if(task.isSuccessful()){
    //                        Log.d(TAG, "onComplete: \ninserted user location into database." +
    //                                "\n latitude: " + userLocation.getGeo_point().getLatitude() +
    //                                "\n longitude: " + userLocation.getGeo_point().getLongitude());
    //                    }
    //                }
    //            });
    //        }catch (NullPointerException e){
    //            Log.e(TAG, "saveUserLocation: User instance is null, stopping location service.");
    //            Log.e(TAG, "saveUserLocation: NullPointerException: "  + e.getMessage() );
    //            stopSelf();
    //        }
    //
    //    }
    companion object {
        private const val TAG = "LocationService"
        private const val UPDATE_INTERVAL = 4 * 1000 /* 4 secs */.toLong()
        private const val FASTEST_INTERVAL: Long = 2000 /* 2 sec */
    }
}