package codi.drive.conductor.chiclayo;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.sinaseyfi.advancedcardview.AdvancedCardView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.conductor.chiclayo.compartido.BorderedCircleTransform;
import codi.drive.conductor.chiclayo.compartido.LocationUpdatesBroadcastReceiver;
import codi.drive.conductor.chiclayo.compartido.PrefUtil;
import codi.drive.conductor.chiclayo.compartido.ServiceLocation;
import codi.drive.conductor.chiclayo.compartido.Utils;
import codi.drive.conductor.chiclayo.entity.SolicitudTaxi;

import static codi.drive.conductor.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class SolicitudTaxiActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, DirectionCallback {
    @BindView(R.id.acvAuto) AdvancedCardView acvAuto;
    @BindView(R.id.acvAutoNoche) AdvancedCardView acvAutoNoche;
    @BindView(R.id.acvBandera) AdvancedCardView acvBandera;
    @BindView(R.id.acvBanderaNoche) AdvancedCardView acvBanderaNoche;
    @BindView(R.id.acvCancelar) AdvancedCardView acvCancelar;
    @BindView(R.id.acvCancelarNoche) AdvancedCardView acvCancelarNoche;
    @BindView(R.id.acvDatos) AdvancedCardView acvDatos;
    @BindView(R.id.btnAbordo) Button btnAbordo;
    @BindView(R.id.btnAceptar) Button btnAceptar;
    @BindView(R.id.btnFinalizar) Button btnFinalizar;
    @BindView(R.id.btnNotificar) Button btnNotificar;
    @BindView(R.id.flayCancelar) FrameLayout flayCancelar;
    @BindView(R.id.flayGratis) FrameLayout flayGratis;
    @BindView(R.id.ivFotoPasajero) ImageView ivFotoPasajero;
    @BindView(R.id.ivLlamada) ImageView ivLlamada;
    @BindView(R.id.ivMostrar) ImageView ivMostrar;
    @BindView(R.id.llayBotones) LinearLayout llayBotones;
    @BindView(R.id.llayTiempoPrecio) LinearLayout llayTiempoPrecio;
    @BindView(R.id.tvApellidosPasajero) TextView tvApellidosPasajero;
    @BindView(R.id.tvDescuento) TextView tvDescuento;
    @BindView(R.id.tvDireccionDestino) TextView tvDireccionDestino;
    @BindView(R.id.tvDireccionOrigen) TextView tvDireccionOrigen;
    @BindView(R.id.tvNombresPasajero) TextView tvNombresPasajero;
    @BindView(R.id.tvPrecio) TextView tvPrecio;
    @BindView(R.id.tvReferencia) TextView tvReferencia;
    @BindView(R.id.tvTiempo) TextView tvTiempo;
    AlertDialog alert = null;
    CountDownTimer verificarId;
    FusedLocationProviderClient fusedLocationClient;
    GoogleMap mMap;
    LocationCallback locationCallback;
    LocationManager locationManager;
    LocationRequest locationRequest;
    Marker markerMiUbicacion, markerOrigen, markerDestino;
    Polyline ruta;
    PrefUtil prefUtil;
    boolean auto = true;
    static Double latitud, longitud, latitudRecojo = 0.0, longitudRecojo = 0.0, latitudDestino = 0.0, longitudDestino = 0.0, pagoFinal = 0.0, tarifa;
    static String idSolicitud = "", fotoPasajero = "", estados, apellidosPasajero, nombresPasajero, telefonoPasajero, direccionOrigen, direccionDestino, codigo = "", referencia;
    private static final long UPDATE_INTERVAL = 1000; // Every 1 second.
    private static final long FASTEST_UPDATE_INTERVAL = 2000; // Every 2 seconds.
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 30; // Every 30 seconds.

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_taxi);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
        if (getIntent().getExtras() != null) {
            idSolicitud = getIntent().getStringExtra("idSolicitud");
            prefUtil.saveGenericValue("idSolicitud", idSolicitud);
            estados = getIntent().getStringExtra("estado");
            tarifa = getIntent().getDoubleExtra("tarifa", 0.0);
            pagoFinal = getIntent().getDoubleExtra("pagoFinal", 0.0);
            apellidosPasajero = getIntent().getStringExtra("apellidosPasajero");
            nombresPasajero = getIntent().getStringExtra("nombresPasajero");
            telefonoPasajero = getIntent().getStringExtra("telefonoPasajero");
            fotoPasajero = getIntent().getStringExtra("fotoPasajero");
            latitudRecojo = getIntent().getDoubleExtra("latitudRecojo", 0.0);
            longitudRecojo = getIntent().getDoubleExtra("longitudRecojo", 0.0);
            latitudDestino = getIntent().getDoubleExtra("latitudDestino", 0.0);
            longitudDestino = getIntent().getDoubleExtra("longitudDestino", 0.0);
            direccionOrigen = getIntent().getStringExtra("direccionOrigen");
            direccionDestino = getIntent().getStringExtra("direccionDestino");
            referencia = getIntent().getStringExtra("referencia");
            codigo = getIntent().getStringExtra("codigo");
            prefUtil.saveGenericValue("estado", estados);
            prefUtil.saveGenericValue("pagoFinal", "" + pagoFinal);
            prefUtil.saveGenericValue("tarifa", String.format("%.2f", tarifa));
            prefUtil.saveGenericValue("apellidosPasajero", apellidosPasajero);
            prefUtil.saveGenericValue("nombresPasajero", nombresPasajero);
            prefUtil.saveGenericValue("telefonoPasajero", telefonoPasajero);
            prefUtil.saveGenericValue("fotoPasajero", fotoPasajero);
            prefUtil.saveGenericValue("latitudRecojo", "" + latitudRecojo);
            prefUtil.saveGenericValue("longitudRecojo", "" + longitudRecojo);
            prefUtil.saveGenericValue("latitudDestino", "" + latitudDestino);
            prefUtil.saveGenericValue("longitudDestino", "" + longitudDestino);
            prefUtil.saveGenericValue("direccionOrigen", direccionOrigen);
            prefUtil.saveGenericValue("direccionDestino", direccionDestino);
            prefUtil.saveGenericValue("referencia", referencia);
            prefUtil.saveGenericValue("codigo", codigo);
            Picasso.get().load(fotoPasajero).transform(new BorderedCircleTransform(5, Color.rgb(0, 214, 209))).into(ivFotoPasajero);
            char[] caracteres_nombres = nombresPasajero.toLowerCase().toCharArray();
            caracteres_nombres[0] = Character.toUpperCase(caracteres_nombres[0]);
            if (nombresPasajero.length() > 2) {
                for (int i = 0; i < nombresPasajero.toLowerCase().length(); i ++) {
                    if (caracteres_nombres[i] == ' ') {
                        caracteres_nombres[i + 1] = Character.toUpperCase(caracteres_nombres[i + 1]);
                    }
                    tvNombresPasajero.setText("" + tvNombresPasajero.getText() + caracteres_nombres[i]);
                }
            }
            char[] caracteres_apellidos = apellidosPasajero.toLowerCase().toCharArray();
            caracteres_apellidos[0] = Character.toUpperCase(caracteres_apellidos[0]);
            if (apellidosPasajero.length() > 2) {
                for (int i = 0; i < apellidosPasajero.toLowerCase().length(); i ++) {
                    if (caracteres_apellidos[i] == ' ') {
                        caracteres_apellidos[i + 1] = Character.toUpperCase(caracteres_apellidos[i + 1]);
                    }
                    tvApellidosPasajero.setText("" + tvApellidosPasajero.getText() + caracteres_apellidos[i]);
                }
            }
            tvDireccionOrigen.setText("De: " + direccionOrigen);
            if (referencia.length() > 0) {
                tvReferencia.setVisibility(View.VISIBLE);
            }
            tvReferencia.setText("Referencia: " + referencia);
            tvDireccionDestino.setText("A: " + direccionDestino);
            tvPrecio.setText("S/ " + String.format("%.2f", tarifa));
            obtenerEstadoSolicitud();
        } else {
            Picasso.get().load(prefUtil.getStringValue("fotoPasajero")).transform(new BorderedCircleTransform(5, Color.rgb(0, 214, 209))).into(ivFotoPasajero);
            char[] caracteres_nombres = prefUtil.getStringValue("nombresPasajero").toLowerCase().toCharArray();
            caracteres_nombres[0] = Character.toUpperCase(caracteres_nombres[0]);
            if (prefUtil.getStringValue("nombresPasajero").length() > 2) {
                for (int i = 0; i < prefUtil.getStringValue("nombresPasajero").toLowerCase().length(); i ++) {
                    if (caracteres_nombres[i] == ' ') {
                        caracteres_nombres[i + 1] = Character.toUpperCase(caracteres_nombres[i + 1]);
                    }
                    tvNombresPasajero.setText("" + tvNombresPasajero.getText() + caracteres_nombres[i]);
                }
            }
            char[] caracteres_apellidos = prefUtil.getStringValue("apellidosPasajero").toLowerCase().toCharArray();
            caracteres_apellidos[0] = Character.toUpperCase(caracteres_apellidos[0]);
            if (prefUtil.getStringValue("apellidosPasajero").length() > 2) {
                for (int i = 0; i < prefUtil.getStringValue("apellidosPasajero").toLowerCase().length(); i ++) {
                    if (caracteres_apellidos[i] == ' ') {
                        caracteres_apellidos[i + 1] = Character.toUpperCase(caracteres_apellidos[i + 1]);
                    }
                    tvApellidosPasajero.setText("" + tvApellidosPasajero.getText() + caracteres_apellidos[i]);
                }
            }
            tvDireccionOrigen.setText("De: " + prefUtil.getStringValue("direccionOrigen"));
            if (prefUtil.getStringValue("referencia").length() > 0) {
                tvReferencia.setVisibility(View.VISIBLE);
            }
            tvReferencia.setText("Referencia: " + prefUtil.getStringValue("referencia"));
            tvDireccionDestino.setText("A: " + prefUtil.getStringValue("direccionDestino"));
            tvPrecio.setText("S/ " + prefUtil.getStringValue("tarifa"));
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                modalActivarGPS();
            } else {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        prefUtil.saveGenericValue("latitud", String.valueOf(location.getLatitude()));
                        prefUtil.saveGenericValue("longitud", String.valueOf(location.getLongitude()));
                    }
                });
            }
        }
        createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    actualizarUbicacion(location);
                }
            }
        };
        Intent intent = new Intent(SolicitudTaxiActivity.this, ServiceLocation.class);
        requestLocationUpdates();
        startLocationUpdates();
        startService(intent);
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i("contador", "" + millisUntilFinished);
            }
            @Override
            public void onFinish() {
                verificarId = new CountDownTimer(5000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        Log.i("contador", "" + millisUntilFinished);
                    }
                    @Override
                    public void onFinish() {
                        if (prefUtil.getStringValue("noche").equals("SI")) {
                            acvCancelarNoche.setVisibility(View.VISIBLE);
                            acvCancelar.setVisibility(View.GONE);
                        } else {
                            acvCancelar.setVisibility(View.VISIBLE);
                            acvCancelarNoche.setVisibility(View.GONE);
                        }
                        verificarIdConductor();
                        this.start();
                    }
                }.start();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        if (estados.equals("S")) {
            idSolicitud = "";
            prefUtil.saveGenericValue("idSolicitud", "");
            startActivity(new Intent(SolicitudTaxiActivity.this, PedidosActivity.class));
            finish();
        } else {
            finishAffinity();
        }
    }

    @Override
    protected void onDestroy() {
        if (verificarId != null) {
            verificarId.cancel();
        }
        if (estados != null) {
            if (estados.equals("S")) {
                idSolicitud = "";
                prefUtil.saveGenericValue("idSolicitud", "");
            }
        }
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (prefUtil.getStringValue("noche").equals("SI")) {
            try {
                if (mMap != null) {
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapa_noche));
                    if (!success) {
                        Log.i("estilo de mapa", "Style parsing failed");
                    }
                    btnAceptar.setBackground(getDrawable(R.drawable.boton_blanco));
                    btnAceptar.setTextColor(Color.rgb(1, 40, 107));
                    btnAbordo.setBackground(getDrawable(R.drawable.boton_blanco));
                    btnAbordo.setTextColor(Color.rgb(1, 40, 107));
                    btnNotificar.setBackground(getDrawable(R.drawable.boton_blanco));
                    btnNotificar.setTextColor(Color.rgb(1, 40, 107));
                    btnFinalizar.setBackground(getDrawable(R.drawable.boton_blanco));
                    btnFinalizar.setTextColor(Color.rgb(1, 40, 107));
                    ivMostrar.setColorFilter(getResources().getColor(R.color.blanco));
                    ivMostrar.setBackground(getDrawable(R.drawable.redondo_azul));
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
                    btnAceptar.setBackground(getDrawable(R.drawable.boton_azul));
                    btnAceptar.setTextColor(Color.rgb(255, 255, 255));
                    btnAbordo.setBackground(getDrawable(R.drawable.boton_azul));
                    btnAbordo.setTextColor(Color.rgb(255, 255, 255));
                    btnNotificar.setBackground(getDrawable(R.drawable.boton_azul));
                    btnNotificar.setTextColor(Color.rgb(255, 255, 255));
                    btnFinalizar.setBackground(getDrawable(R.drawable.boton_azul));
                    btnFinalizar.setTextColor(Color.rgb(255, 255, 255));
                    ivMostrar.setColorFilter(getResources().getColor(R.color.azul));
                    ivMostrar.setBackground(getDrawable(R.drawable.redondo_blanco));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnCameraIdleListener(this);
        mMap.setPadding(0, getResources().getDisplayMetrics().heightPixels / 4, 0, 0);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 111);
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locListener);
        if (markerOrigen != null) {
            markerOrigen.setPosition(new LatLng(Double.parseDouble(prefUtil.getStringValue("latitudRecojo")), Double.parseDouble(prefUtil.getStringValue("longitudRecojo"))));
        } else {
            MarkerOptions markerOptions = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pasajero))
                    .position(new LatLng(Double.parseDouble(prefUtil.getStringValue("latitudRecojo")), Double.parseDouble(prefUtil.getStringValue("longitudRecojo"))));
            markerOrigen = mMap.addMarker(markerOptions);
        }
        if (markerDestino != null) {
            markerDestino.setPosition(new LatLng(Double.parseDouble(prefUtil.getStringValue("latitudDestino")), Double.parseDouble(prefUtil.getStringValue("longitudDestino"))));
        } else {
            MarkerOptions markerOptions = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bandera))
                    .position(new LatLng(Double.parseDouble(prefUtil.getStringValue("latitudDestino")), Double.parseDouble(prefUtil.getStringValue("longitudDestino"))));
            markerDestino = mMap.addMarker(markerOptions);
        }
        if (markerMiUbicacion != null) {
            markerMiUbicacion.setPosition(new LatLng(latitud, longitud));
        } else {
            MarkerOptions markerOptions = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.carro))
                    .position(new LatLng(latitud, longitud));
            markerMiUbicacion = mMap.addMarker(markerOptions);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(markerMiUbicacion.getPosition())
                    .zoom(16)
                    .bearing(mMap.getCameraPosition().bearing)
                    .tilt(mMap.getCameraPosition().tilt)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        Log.i("verificarAceptado", "SolicitudTaxiActivity");
        btnAceptar.setVisibility(View.GONE);
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/verificar_aceptado.php?idsolicitud=" + prefUtil.getStringValue("idSolicitud"));
                Log.i("verificarAceptado", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            String estado = jsonArray.getJSONObject(0).getString("estado");
                            switch (estado) {
                                case "S":
                                    acvCancelar.setVisibility(View.GONE);
                                    acvCancelarNoche.setVisibility(View.GONE);
                                    showCurvedPolyline(markerMiUbicacion.getPosition(), markerOrigen.getPosition(), 0.1);
                                    btnAceptar.setVisibility(View.VISIBLE);
                                    break;
                                case "A":
                                    if (prefUtil.getStringValue("noche").equals("SI")) {
                                        acvCancelarNoche.setVisibility(View.VISIBLE);
                                        acvCancelar.setVisibility(View.GONE);
                                    } else {
                                        acvCancelarNoche.setVisibility(View.GONE);
                                        acvCancelar.setVisibility(View.VISIBLE);
                                    }
                                    acvAuto.setVisibility(View.GONE);
                                    acvBandera.setVisibility(View.GONE);
                                    acvAutoNoche.setVisibility(View.GONE);
                                    acvBanderaNoche.setVisibility(View.GONE);
                                    GoogleDirection.withServerKey("AIzaSyBk7ZaVD-gSnJMn0_LFjdqNXAz_kgrSnE0")
                                            .from(markerMiUbicacion.getPosition())
                                            .to(markerOrigen.getPosition())
                                            .transportMode(TransportMode.DRIVING)
                                            .execute(SolicitudTaxiActivity.this);
                                    llayBotones.setVisibility(View.VISIBLE);
//                                  llayTiempoPrecio.setVisibility(View.VISIBLE);
//                                  tvTiempo.setVisibility(View.VISIBLE);
                                    tvPrecio.setVisibility(View.VISIBLE);
                                    ivLlamada.setVisibility(View.VISIBLE);
                                    tvApellidosPasajero.setVisibility(View.VISIBLE);
                                    tvDireccionOrigen.setVisibility(View.VISIBLE);
                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    builder.include(markerMiUbicacion.getPosition());
                                    builder.include(markerOrigen.getPosition());
                                    builder.include(markerDestino.getPosition());
                                    LatLngBounds bounds = builder.build();
                                    int width = getResources().getDisplayMetrics().widthPixels;
                                    int height = getResources().getDisplayMetrics().heightPixels;
                                    int padding = (int) (width * 0.25);
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
                                    break;
                                case "B":
                                    acvCancelar.setVisibility(View.GONE);
                                    acvCancelarNoche.setVisibility(View.GONE);
                                    acvBandera.setVisibility(View.VISIBLE);
                                    acvBanderaNoche.setVisibility(View.VISIBLE);
                                    GoogleDirection.withServerKey("AIzaSyBk7ZaVD-gSnJMn0_LFjdqNXAz_kgrSnE0")
                                            .from(markerMiUbicacion.getPosition())
                                            .to(markerDestino.getPosition())
                                            .transportMode(TransportMode.DRIVING)
                                            .execute(SolicitudTaxiActivity.this);
                                    btnFinalizar.setVisibility(View.VISIBLE);
                                    ivLlamada.setVisibility(View.VISIBLE);
//                                  llayTiempoPrecio.setVisibility(View.VISIBLE);
//                                  tvTiempo.setVisibility(View.VISIBLE);
                                    tvPrecio.setVisibility(View.VISIBLE);
                                    tvDireccionOrigen.setVisibility(View.GONE);
                                    tvReferencia.setVisibility(View.GONE);
                                    tvDireccionDestino.setVisibility(View.VISIBLE);
                                    tvApellidosPasajero.setVisibility(View.VISIBLE);
                                    LatLngBounds.Builder builder2 = new LatLngBounds.Builder();
                                    builder2.include(markerMiUbicacion.getPosition());
                                    builder2.include(markerOrigen.getPosition());
                                    builder2.include(markerDestino.getPosition());
                                    LatLngBounds bounds2 = builder2.build();
                                    int width2 = getResources().getDisplayMetrics().widthPixels;
                                    int height2 = getResources().getDisplayMetrics().heightPixels;
                                    int padding2 = (int) (width2 * 0.25);
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds2, width2, height2, padding2));
                                    if (mMap != null) {
                                        if (markerOrigen != null) {
                                            markerOrigen.remove();
                                        }
                                    }
                                    break;
                                case "C":
                                    finish();
                                    Toast.makeText(SolicitudTaxiActivity.this, "Lo sentimos, el pasajero ha cancelado el servicio", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    @Override
    public void onCameraIdle() {
        new CountDownTimer(7000, 1000) {
            @Override
            public void onTick(long l) {
            }
            @Override
            public void onFinish() {
                centrarPosicion();
            }
        }.start();
    }

    @OnClick({R.id.acvAuto, R.id.acvAutoNoche}) void seguirAuto() {
        auto = true;
        acvAuto.setVisibility(View.GONE);
        acvAutoNoche.setVisibility(View.GONE);
        if (prefUtil.getStringValue("noche").equals("SI")) {
            acvBanderaNoche.setVisibility(View.VISIBLE);
            acvBandera.setVisibility(View.GONE);
        } else {
            acvBanderaNoche.setVisibility(View.GONE);
            acvBandera.setVisibility(View.VISIBLE);
        }
        if (mMap != null) {
            if (markerMiUbicacion != null) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(markerMiUbicacion.getPosition())
                        .zoom(mMap.getCameraPosition().zoom)
                        .bearing(mMap.getCameraPosition().bearing)
                        .tilt(mMap.getCameraPosition().tilt)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    @OnClick({R.id.acvBandera, R.id.acvBanderaNoche}) void seguirBandera() {
        auto = false;
        acvBandera.setVisibility(View.GONE);
        acvBanderaNoche.setVisibility(View.GONE);
        if (prefUtil.getStringValue("noche").equals("SI")) {
            acvAutoNoche.setVisibility(View.VISIBLE);
            acvAuto.setVisibility(View.GONE);
        } else {
            acvAutoNoche.setVisibility(View.GONE);
            acvAuto.setVisibility(View.VISIBLE);
        }
        if (mMap != null) {
            if (markerDestino != null) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(markerDestino.getPosition())
                        .zoom(mMap.getCameraPosition().zoom)
                        .bearing(mMap.getCameraPosition().bearing)
                        .tilt(mMap.getCameraPosition().tilt)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    @OnClick({R.id.acvCancelar, R.id.acvCancelarNoche}) void cancelarServicio() {
        flayCancelar.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btnAceptar) void verificarAceptado() {
        Log.i("verificarAceptado", "SolicitudTaxiActivity");
        btnAceptar.setVisibility(View.GONE);
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/verificar_aceptado.php?idsolicitud=" + prefUtil.getStringValue("idSolicitud"));
                Log.i("verificarAceptado", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            String estado = jsonArray.getJSONObject(0).getString("estado");
                            estados = estado;
                            switch (estado) {
                                case "S":
                                    aceptarServicio();
                                    break;
                                case "A":
                                case "B":
                                case "F":
                                    prefUtil.saveGenericValue("idSolicitud", "");
                                    finish();
                                    Toast.makeText(SolicitudTaxiActivity.this, "Lo sentimos, el pedido ha sido aceptado por alguien más", Toast.LENGTH_SHORT).show();
                                    break;
                                case "C":
                                    prefUtil.saveGenericValue("idSolicitud", "");
                                    finish();
                                    Toast.makeText(SolicitudTaxiActivity.this, "Lo sentimos, el pasajero ha cancelado el servicio", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    @OnClick(R.id.btnAbordo) void cambiarAbordo() {
        acvBandera.setVisibility(View.VISIBLE);
        Log.i("cambiarAbordo", "SolicitudTaxiActivity");
        llayBotones.setVisibility(View.GONE);
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/cambiar_a_bordo.php?idsolicitud=" + prefUtil.getStringValue("idSolicitud"));
                Log.i("cambiarAbordo", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                acvCancelar.setVisibility(View.GONE);
                                acvCancelarNoche.setVisibility(View.GONE);
                                prefUtil.saveGenericValue("estado", "B");
                                tvDireccionDestino.setVisibility(View.VISIBLE);
                                tvDireccionOrigen.setVisibility(View.GONE);
                                tvReferencia.setVisibility(View.GONE);
                                btnFinalizar.setVisibility(View.VISIBLE);
                                if (markerOrigen != null) {
                                    markerOrigen.remove();
                                }
                                GoogleDirection.withServerKey("AIzaSyBk7ZaVD-gSnJMn0_LFjdqNXAz_kgrSnE0")
                                        .from(markerMiUbicacion.getPosition())
                                        .to(markerDestino.getPosition())
                                        .transportMode(TransportMode.DRIVING)
                                        .execute(SolicitudTaxiActivity.this);
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(markerMiUbicacion.getPosition());
                                builder.include(markerDestino.getPosition());
                                LatLngBounds bounds = builder.build();
                                int width = getResources().getDisplayMetrics().widthPixels;
                                int height = getResources().getDisplayMetrics().heightPixels;
                                int padding = (int) (width * 0.25);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    @OnClick(R.id.btnAceptarGratis) void cerrarGratis() {
        Log.i("cerrarGratis", "SolicitudTaxiActivity");
        btnFinalizar.setVisibility(View.GONE);
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(SolicitudTaxiActivity.this,
                Pair.create(ivFotoPasajero, ivFotoPasajero.getTransitionName()),
                Pair.create(tvNombresPasajero, tvNombresPasajero.getTransitionName()),
                Pair.create(tvApellidosPasajero, tvApellidosPasajero.getTransitionName())
        );
        Intent intent = new Intent(SolicitudTaxiActivity.this, FinalizarSolicitudActivity.class);
        intent.putExtra("nombresPasajero", tvNombresPasajero.getText().toString());
        intent.putExtra("apellidosPasajero", tvApellidosPasajero.getText().toString());
        intent.putExtra("fotoPasajero", prefUtil.getStringValue("fotoPasajero"));
        intent.putExtra("pagoFinal", prefUtil.getStringValue("pagoFinal"));
        intent.putExtra("idSolicitud", prefUtil.getStringValue("idSolicitud"));
        startActivity(intent, activityOptions.toBundle());
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @OnClick(R.id.btnFinalizar) void finalizarServicio() {
        if (codigo != null) {
            if (codigo.length() > 3) {
                flayGratis.setVisibility(View.VISIBLE);
                tvDescuento.setText("El pasajero tiene un descuento especial de S/ " + (tarifa - pagoFinal) + "0");
            } else {
                Log.i("finalizarServicio", "SolicitudTaxiActivity");
                btnFinalizar.setVisibility(View.GONE);
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(SolicitudTaxiActivity.this,
                        Pair.create(ivFotoPasajero, ivFotoPasajero.getTransitionName()),
                        Pair.create(tvNombresPasajero, tvNombresPasajero.getTransitionName()),
                        Pair.create(tvApellidosPasajero, tvApellidosPasajero.getTransitionName())
                );
                Intent intent = new Intent(SolicitudTaxiActivity.this, FinalizarSolicitudActivity.class);
                intent.putExtra("nombresPasajero", tvNombresPasajero.getText().toString());
                intent.putExtra("apellidosPasajero", tvApellidosPasajero.getText().toString());
                intent.putExtra("fotoPasajero", prefUtil.getStringValue("fotoPasajero"));
                intent.putExtra("pagoFinal", prefUtil.getStringValue("pagoFinal"));
                intent.putExtra("idSolicitud", prefUtil.getStringValue("idSolicitud"));
                startActivity(intent, activityOptions.toBundle());
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        } else {
            Log.i("finalizarServicio", "SolicitudTaxiActivity");
            btnFinalizar.setVisibility(View.GONE);
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(SolicitudTaxiActivity.this,
                    Pair.create(ivFotoPasajero, ivFotoPasajero.getTransitionName()),
                    Pair.create(tvNombresPasajero, tvNombresPasajero.getTransitionName()),
                    Pair.create(tvApellidosPasajero, tvApellidosPasajero.getTransitionName())
            );
            Intent intent = new Intent(SolicitudTaxiActivity.this, FinalizarSolicitudActivity.class);
            intent.putExtra("nombresPasajero", tvNombresPasajero.getText().toString());
            intent.putExtra("apellidosPasajero", tvApellidosPasajero.getText().toString());
            intent.putExtra("fotoPasajero", prefUtil.getStringValue("fotoPasajero"));
            intent.putExtra("pagoFinal", prefUtil.getStringValue("pagoFinal"));
            intent.putExtra("idSolicitud", prefUtil.getStringValue("idSolicitud"));
            startActivity(intent, activityOptions.toBundle());
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    @OnClick(R.id.btnNo) void no() {
        flayCancelar.setVisibility(View.GONE);
    }

    @OnClick(R.id.btnNotificar) void cambiarNotificado() {
        Log.i("cambiarNotificado", "SolicitudTaxiActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/cambiar_notificado.php?idsolicitud=" + prefUtil.getStringValue("idSolicitud"));
                Log.i("cambiarNotificado", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                obtenerTokenPasajero();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    @OnClick(R.id.btnSi) void si() {
        Intent intent = new Intent(SolicitudTaxiActivity.this, CancelarAcvitity.class);
        intent.putExtra("idSolicitud", prefUtil.getStringValue("idSolicitud"));
        prefUtil.saveGenericValue("idSolicitud", idSolicitud);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @OnClick(R.id.ivLlamada) void llamarPasajero() {
        Log.i("llamarPasajero", "SolicitudTaxiActivity");
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + prefUtil.getStringValue("telefonoPasajero")));
        startActivity(intent);
    }

    @OnClick(R.id.ivMostrar) void mostrar() {
        acvDatos.setVisibility(View.VISIBLE);
        ivMostrar.setVisibility(View.GONE);
        mMap.setPadding(0, getResources().getDisplayMetrics().heightPixels / 4, 0, 0);
        centrarPosicion();
    }

    @OnClick(R.id.ivOcultar) void ocultar() {
        ivMostrar.setVisibility(View.VISIBLE);
        acvDatos.setVisibility(View.GONE);
        mMap.setPadding(0, 0, 0, 0);
        centrarPosicion();
    }

    public void aceptarServicio() {
        Log.i("aceptarServicio", "SolicitudTaxiActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/aceptar_servicio.php?idsolicitud=" + idSolicitud + "&idconductor="
                        + prefUtil.getStringValue("idConductor"));
                Log.i("aceptarServicio", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                estados = "A";
//                                if (prefUtil.getStringValue("noche").equals("SI")) {
//                                    acvCancelarNoche.setVisibility(View.VISIBLE);
//                                    acvCancelar.setVisibility(View.GONE);
//                                } else {
//                                    acvCancelarNoche.setVisibility(View.GONE);
//                                    acvCancelar.setVisibility(View.VISIBLE);
//                                }
                                btnAceptar.setVisibility(View.GONE);
                                llayBotones.setVisibility(View.VISIBLE);
                                ivLlamada.setVisibility(View.VISIBLE);
//                                llayTiempoPrecio.setVisibility(View.VISIBLE);
//                                tvTiempo.setVisibility(View.VISIBLE);
                                tvPrecio.setVisibility(View.VISIBLE);
                                tvApellidosPasajero.setVisibility(View.VISIBLE);
                                MainActivity.ubicaciones.cancel();
                                enviarEstado("O");
                                prefUtil.saveGenericValue("idSolicitud", idSolicitud);
                                prefUtil.saveGenericValue("estado", "A");
                                GoogleDirection.withServerKey("AIzaSyBk7ZaVD-gSnJMn0_LFjdqNXAz_kgrSnE0")
                                        .from(markerMiUbicacion.getPosition())
                                        .to(markerOrigen.getPosition())
                                        .transportMode(TransportMode.DRIVING)
                                        .execute(SolicitudTaxiActivity.this);
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(markerMiUbicacion.getPosition());
                                builder.include(markerOrigen.getPosition());
                                LatLngBounds bounds = builder.build();
                                int width = getResources().getDisplayMetrics().widthPixels;
                                int height = getResources().getDisplayMetrics().heightPixels;
                                int padding = (int) (width * 0.25);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
                                verificarId = new CountDownTimer(5000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                    }
                                    @Override
                                    public void onFinish() {
                                        if (prefUtil.getStringValue("noche").equals("SI")) {
                                            acvCancelarNoche.setVisibility(View.VISIBLE);
                                            acvCancelar.setVisibility(View.GONE);
                                        } else {
                                            acvCancelar.setVisibility(View.VISIBLE);
                                            acvCancelarNoche.setVisibility(View.GONE);
                                        }
                                        verificarIdConductor();
                                        this.start();
                                    }
                                }.start();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void actualizarUbicacion(Location location) {
        if (location != null) {
            latitud = location.getLatitude();
            longitud = location.getLongitude();
            prefUtil.saveGenericValue("latitud", "" + latitud);
            prefUtil.saveGenericValue("longitud", "" + longitud);
            if (markerMiUbicacion != null) {
                markerMiUbicacion.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            } else {
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.mipmap.carro));
                markerMiUbicacion = mMap.addMarker(markerOptions);
            }
        }
    }

    public void centrarPosicion() {
        if (mMap != null) {
            if (auto) {
                if (markerMiUbicacion != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(markerMiUbicacion.getPosition())
                            .zoom(mMap.getCameraPosition().zoom)
                            .bearing(mMap.getCameraPosition().bearing)
                            .tilt(mMap.getCameraPosition().tilt)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            } else {
                if (markerDestino != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(markerDestino.getPosition())
                            .zoom(mMap.getCameraPosition().zoom)
                            .bearing(mMap.getCameraPosition().bearing)
                            .tilt(mMap.getCameraPosition().tilt)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        }
    }

    public void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setMaxWaitTime(MAX_WAIT_TIME);
    }

    public void enviarEstado(String estado) {
        Log.i("enviarEstado", "MainActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/cambiar_estado.php?estado=" + estado + "&idconductor=" + prefUtil.getStringValue("idConductor"));
                Log.i("enviarEstado", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                Log.i("enviarEstado", "ok");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void modalActivarGPS() {
        if (alert == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("El GPS está desactivado, ¿desea activarlo?")
                    .setCancelable(false)
                    .setPositiveButton("Sí", (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("No", (dialog, which) -> dialog.cancel());
            alert = builder.create();
            alert.show();
        }
    }

    public void notificarPasajero(String token) {
        Log.i("notificarPasajero", "SolicitudTaxiActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/notificar_conductor.php?token=" + token + "&id_pedido=" + prefUtil.getStringValue("idSolicitud"));
                Log.i("notificarPasajero", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                Log.i("notificarPasajero", "OK");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void obtenerEstadoSolicitud() {
        Log.i("obtenerEstadoSolicitud", "SolicitudTaxiActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/obtener_estado.php?idsolicitud=" + prefUtil.getStringValue("idSolicitud"));
                Log.i("obtenerEstadoSolicitud", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            String estado = jsonArray.getJSONObject(0).getString("estado");
                            if (estado.equals("S")) {
                                btnAceptar.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void obtenerTokenPasajero() {
        Log.i("obtenerTokenPasajero", "SolicitudTaxiActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/obtener_token_pasajero.php?idsolicitud=" + prefUtil.getStringValue("idSolicitud"));
                Log.i("obtenerTokenPasajero", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            String token = jsonArray.getJSONObject(0).getString("token");
                            btnNotificar.setVisibility(View.GONE);
                            notificarPasajero(token);
                            Toast.makeText(SolicitudTaxiActivity.this, "Ya se ha notificado al pasajero.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void requestLocationUpdates() {
        try {
            Log.i("MainActivity", "Starting location updates");
            Utils.setRequestingLocationUpdates(this, true);
            fusedLocationClient.requestLocationUpdates(locationRequest, getPendingIntent());
        } catch (SecurityException e) {
            Utils.setRequestingLocationUpdates(this, false);
            e.printStackTrace();
        }
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void showCurvedPolyline (LatLng p1, LatLng p2, double k) {
        double d = SphericalUtil.computeDistanceBetween(p1, p2);
        double h = SphericalUtil.computeHeading(p1, p2);
        LatLng p = SphericalUtil.computeOffset(p1, d * 0.5, h);
        double x = (1 - k * k) * d * 0.5 / (2 * k);
        double r = (1 + k * k) * d * 0.5 / (2 * k);
        LatLng c = SphericalUtil.computeOffset(p, x, h > 40 ? h + 90.0 : h - 90.0);
        PolylineOptions options = new PolylineOptions();
        List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dash(30), new Gap(20));
        double h1 = SphericalUtil.computeHeading(c, p1);
        double h2 = SphericalUtil.computeHeading(c, p2);
        int numpoints = 100;
        double step = (h2 - h1) / numpoints;
        for (int i = 0; i < numpoints; i++) {
            LatLng pi = SphericalUtil.computeOffset(c, r, h1 + i * step);
            options.add(pi);
        }
        if (ruta != null) {
            ruta.remove();
        }
        if (prefUtil.getStringValue("nodhe").equals("SI")) {
            ruta = mMap.addPolyline(options.width(10).color(Color.rgb(255, 255, 255)).geodesic(false).pattern(pattern));
        } else {
            ruta = mMap.addPolyline(options.width(10).color(Color.rgb(96, 121, 133)).geodesic(false).pattern(pattern));
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(markerMiUbicacion.getPosition());
        builder.include(markerOrigen.getPosition());
        builder.include(markerDestino.getPosition());
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.25);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
    }

    public void verificarIdConductor() {
        Log.i("verificarIdConductor", "SolicitudTaxiActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/verificar_id_conductor.php?idsolicitud=" + prefUtil.getStringValue("idSolicitud"));
                Log.i("verificarIdConductor", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            String idConductor = jsonArray.getJSONObject(0).getString("idconductor");
                            Log.i("verificarIdConductor", idConductor);
                            if (!prefUtil.getStringValue("idConductor").equals(idConductor)) {
                                prefUtil.saveGenericValue("idSolicitud", "");
                                startActivity(new Intent(SolicitudTaxiActivity.this, PedidosActivity.class));
                                finish();
                                Toast.makeText(SolicitudTaxiActivity.this, "Lo sentimos, alguien más tomó el servicio antes que usted", Toast.LENGTH_SHORT).show();
                            } else {
                                if (prefUtil.getStringValue("noche").equals("SI")) {
                                    acvCancelarNoche.setVisibility(View.VISIBLE);
                                    acvCancelar.setVisibility(View.GONE);
                                } else {
                                    acvCancelarNoche.setVisibility(View.GONE);
                                    acvCancelar.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            Route route = direction.getRouteList().get(0);
            Leg leg = route.getLegList().get(0);
            ArrayList<LatLng> pointList = leg.getDirectionPoint();
            if (pointList != null) {
                if (pointList.size() > 0) {
                    if (ruta != null) {
                        ruta.remove();
                    }
                    PolylineOptions polylineOptions;
                    if (prefUtil.getStringValue("noche").equals("SI")) {
                        polylineOptions = DirectionConverter.createPolyline(this, pointList, 5, Color.rgb(255, 255, 255));
                    } else {
                        polylineOptions = DirectionConverter.createPolyline(this, pointList, 5, Color.rgb(45, 45, 115));
                    }
                    ruta = mMap.addPolyline(polylineOptions);
                    Info durationInfo = leg.getDuration();
                    String duration = durationInfo.getText();
                    if (duration.startsWith("h", 2)) {
                        duration = duration.substring(0, duration.indexOf("h") + 1) + duration.substring(duration.indexOf("h") + 5);
                    }
                    if (duration.startsWith("s", duration.length() - 1)) {
                        duration = duration.substring(0, duration.length() - 1);
                    }
                    tvTiempo.setText(duration);
                }
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
    }
}