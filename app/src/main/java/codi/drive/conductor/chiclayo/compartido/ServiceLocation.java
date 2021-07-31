package codi.drive.conductor.chiclayo.compartido;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Date;
import codi.drive.conductor.chiclayo.MainActivity;
import codi.drive.conductor.chiclayo.R;

/**
 * By: el-bryant
 */

public class ServiceLocation extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    DatabaseReference mDatabase;
    private GoogleApiClient mGoogleApiClient;
    private LocalBroadcastManager broadcaster;
    private LocationManager locationManager = null;
    private PrefUtil prefUtil;
    public static String latitud_service = "", longitud_service = "";
    private static final int LOCATION_INTERVAL = 10000;
    private static final float LOCATION_DISTANCE = 0;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    public boolean modoEnvioPos;
    private WebService ws;

    public ServiceLocation() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
        initComponents();
        iniciarObtencionGPS();
        initializeLocationManager();
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, (android.location.LocationListener) locationListeners[1]);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, (android.location.LocationListener) locationListeners[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initComponents() {
        prefUtil = new PrefUtil(this);
        ws = new WebService(this, true);
        broadcaster = LocalBroadcastManager.getInstance(this);
        buildGoogleApiClient();
    }

    public class LocationListener implements com.google.android.gms.location.LocationListener {
        Location lastLocation;
        public LocationListener(String provider) {
            lastLocation = new Location(provider);
        }
        @Override
        public void onLocationChanged(Location location) {
            lastLocation.set(location);
        }
    }

    LocationListener[] locationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    public void iniciarObtencionGPS() {
        if (prefUtil.getStringValue("disponible").equals("1")) {
            if (mGoogleApiClient != null) {
                if (!mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (prefUtil.getStringValue("disponible").equals("1")) {
            ws.enviarUbicacion();
            prepareForegroundNotification();
            startLocationUpdates();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            for (LocationListener locationListener : locationListeners) {
                try {
                    locationManager.removeUpdates((android.location.LocationListener) locationListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(UPDATE_INTERVAL_IN_MILLISECONDS / 3);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this, Looper.getMainLooper());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        latitud_service = String.valueOf(location.getLatitude());
        longitud_service = String.valueOf(location.getLongitude());
        if (location.getLatitude() != 0.0 && !String.valueOf(location.getLatitude()).equals("") && location.getLongitude() != 0.0 && !String.valueOf(location.getLongitude()).equals("")) {
            prefUtil.saveGenericValue("latitud", String.valueOf(location.getLatitude()));
            prefUtil.saveGenericValue("longitud", String.valueOf(location.getLongitude()));
            if (prefUtil.getStringValue("dni") != null && !prefUtil.getStringValue("dni").equals("")) {
                mDatabase = FirebaseDatabase.getInstance().getReference().child(prefUtil.getStringValue("dni"));
                DatabaseReference lat = mDatabase.child("lat"), lng = mDatabase.child("lng");
                lat.setValue(location.getLatitude());
                lng.setValue(location.getLongitude());
            }
            broadcastUbicacion();
        }
    }

    public void broadcastUbicacion() {
        Intent intent = new Intent(PrefUtil.BROADCAST_ACTUALIZAR_UBICACION);
        broadcaster.sendBroadcast(intent);
    }

    public void initializeLocationManager() {
        if (locationManager != null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        }
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location currentLocation = locationResult.getLastLocation();
            Date date = new Date();
            Log.d("Locations - Service",  date + " - " + currentLocation.getLatitude() + "," + currentLocation.getLongitude());
            prefUtil.saveGenericValue("latitud", "" + currentLocation.getLatitude());
            prefUtil.saveGenericValue("longitud", "" + currentLocation.getLongitude());
            ws = new WebService(ServiceLocation.this, true);
            ws.enviarUbicacion();
        }
    };

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(this.locationRequest, this.locationCallback, Looper.myLooper());
    }

    private void prepareForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    Utils.CHANNEL_ID,
                    "Location Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                52,
                notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, Utils.CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentTitle(getString(R.string.app_notification_description))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(520, notification);
    }

    private void initData() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
    }

    public void envioPosicionTerminado() {
        modoEnvioPos = false;
    }
}
