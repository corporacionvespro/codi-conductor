package codi.drive.conductor.chiclayo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import codi.drive.conductor.chiclayo.compartido.PrefUtil;

/**
 * By: el-bryant
 */

public class LugarActivity extends AppCompatActivity implements OnMapReadyCallback {
    Double latitud = 0.0, longitud = 0.0;
    GoogleMap mMap;
    LatLng origen;
    Marker markerLugar, markerMiUbicacion;
    PrefUtil prefUtil;
    String idLugar = "", nombreLugar = "", direccionDestino = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (getIntent().getExtras() != null) {
            latitud = getIntent().getExtras().getDouble("latitud");
            longitud = getIntent().getExtras().getDouble("longitud");
            idLugar = getIntent().getExtras().getString("idLugar");
            nombreLugar = getIntent().getExtras().getString("nombre");
            direccionDestino = getIntent().getExtras().getString("direccion");
        }
        prefUtil = new PrefUtil(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMinZoomPreference(10);
        mMap.setMaxZoomPreference(20);
        mMap.setPadding(0, 150, 0, 0);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}, 111);
        }
        if (prefUtil.getStringValue("noche").equals("SI")) {
            try {
                if (mMap != null) {
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapa_noche));
                    if (!success) {
                        Log.i("estilo de mapa", "Style parsing failed");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (mMap != null) {
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_gris));
                    if (!success) {
                        Log.i("estilo de mapa", "Style parsing failed");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ubicarLugar();
    }

    public void ubicarLugar() {
        try {
            if (mMap != null) {
                LatLng posicion = new LatLng(latitud, longitud);
                if (markerLugar != null) {
                    markerLugar.setPosition(posicion);
                } else {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(posicion)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bandera));
                    markerLugar = mMap.addMarker(markerOptions);
                }
                if (markerMiUbicacion != null) {
                    markerMiUbicacion.setPosition(new LatLng(MainActivity.latitud, MainActivity.longitud));
                } else {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(MainActivity.latitud, MainActivity.longitud))
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.carro));
                    markerMiUbicacion = mMap.addMarker(markerOptions);
                }
                origen = new LatLng(MainActivity.latitud, MainActivity.longitud);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(markerMiUbicacion.getPosition());
                builder.include(markerLugar.getPosition());
                LatLngBounds bounds = builder.build();
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.25);
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
