package codi.drive.conductor.chiclayo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.DecimalFormat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.conductor.chiclayo.compartido.BorderedCircleTransform;
import codi.drive.conductor.chiclayo.compartido.LocationUpdatesBroadcastReceiver;
import codi.drive.conductor.chiclayo.compartido.LocationUpdatesIntentService;
import codi.drive.conductor.chiclayo.compartido.PrefUtil;
import codi.drive.conductor.chiclayo.compartido.ServiceLocation;
import codi.drive.conductor.chiclayo.compartido.Utils;
import static codi.drive.conductor.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class PerfilActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.dlayMenu) DrawerLayout dlayMenu;
    @BindView(R.id.ivFotoPerfil) ImageView ivFotoPerfil;
    @BindView(R.id.nvMenu) NavigationView nvMenu;
    @BindView(R.id.rbValoracion) RatingBar rbValoracion;
    @BindView(R.id.tvAnio) TextView tvAnio;
    @BindView(R.id.tvApellidosPerfil) TextView tvApellidosPerfil;
    @BindView(R.id.tvCelular) TextView tvCelular;
    @BindView(R.id.tvColor) TextView tvColor;
    @BindView(R.id.tvCorreo) TextView tvCorreo;
    @BindView(R.id.tvDni) TextView tvDni;
    @BindView(R.id.tvEmpresa) TextView tvEmpresa;
    @BindView(R.id.tvMarca) TextView tvMarca;
    @BindView(R.id.tvModelo) TextView tvModelo;
    @BindView(R.id.tvNombresPerfil) TextView tvNombresPerfil;
    @BindView(R.id.tvPlaca) TextView tvPlaca;
    @BindView(R.id.tvUnidad) TextView tvUnidad;
    ImageView ivFotoUsuario;
    PrefUtil prefUtil;
    SwitchCompat swNoche;
    TextView tvNombreUsuario, tvSaldo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        prefUtil = new PrefUtil(this);
        ButterKnife.bind(this);
        ivFotoUsuario = (ImageView) nvMenu.getHeaderView(0).findViewById(R.id.ivFotoUsuario);
        tvNombreUsuario = (TextView) nvMenu.getHeaderView(0).findViewById(R.id.tvNombreUsuario);
        tvSaldo = (TextView) nvMenu.getHeaderView(0).findViewById(R.id.tvSaldo);
        nvMenu.setNavigationItemSelectedListener(this);
        promedioEstrellas();
        Menu menu = nvMenu.getMenu();
        swNoche = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_noche)).findViewById(R.id.switch_id);
        swNoche.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                prefUtil.saveGenericValue("noche", "SI");
            } else {
                prefUtil.saveGenericValue("noche", "NO");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_acerca_app:
                startActivity(new Intent(PerfilActivity.this, AcercaAppActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_cerrar:
                enviarEstadoOcupado();
                prefUtil.clearAll();
                stopService(new Intent(PerfilActivity.this, ServiceLocation.class));
                stopService(new Intent(PerfilActivity.this, LocationUpdatesIntentService.class));
                stopService(new Intent(PerfilActivity.this, LocationUpdatesBroadcastReceiver.class));
                MainActivity.ubicaciones.cancel();
                Utils.setRequestingLocationUpdates(this, false);
                startActivity(new Intent(PerfilActivity.this, Acceso1Activity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_compartir:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                String msj = "Te invito a descargar la App CODI CONDUCTOR y pertenecer a la red más grande de conductores https://play.google.com/store/apps/details?id=codi.drive.conductor.chiclayo";
                shareIntent.putExtra(Intent.EXTRA_TEXT, msj);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Compartir en"));
                break;
            case R.id.nav_historial:
                startActivity(new Intent(PerfilActivity.this, HistorialActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_inicio:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_lugares:
                startActivity(new Intent(PerfilActivity.this, CategoriaActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_noche:
                swNoche.setChecked(!swNoche.isChecked());
                break;
            case R.id.nav_ocupado:
                prefUtil.saveGenericValue("disponible", "NO");
                startActivity(new Intent(PerfilActivity.this, OcupadoActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_perfil:
                dlayMenu.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_promociones:
                startActivity(new Intent(PerfilActivity.this, PromocionesActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
        }
        dlayMenu.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.acvMenu) void abrirMenu() {
        dlayMenu.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.btnCambiarClave) void cambiarClave() {
        startActivity(new Intent(PerfilActivity.this, CambiarClaveActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void enviarEstadoOcupado() {
        Log.i("enviarEstadoOcupado", "PerfilActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/enviar_estado_ocupado.php?idConductor=" + prefUtil.getStringValue("idConductor"));
                Log.i("enviarEstadoOcupado", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                Log.i("enviarEstadoOcupado", "todo OK");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void promedioEstrellas() {
        Log.i("promedioEstrellas", "click");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/promedio_estrellas_conductor.php?idconductor=" + prefUtil.getStringValue("idConductor"));
                Log.i("promedioEstrellas", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() > 0) {
                            DecimalFormat df = new DecimalFormat("0.00");
                            if (jsonArray.getJSONObject(0).getString("promedio") != null) {
                                float promedio = Float.parseFloat(df.format(jsonArray.getJSONObject(0).getDouble("promedio")).replace(",", "."));
                                rbValoracion.setRating(promedio);
                            }
                            Picasso.get().load(prefUtil.getStringValue("foto"))
                                    .transform(new BorderedCircleTransform(5, Color.rgb(0, 214, 209)))
                                    .into(ivFotoUsuario);
                            Picasso.get().load(prefUtil.getStringValue("foto"))
                                    .transform(new BorderedCircleTransform(5, Color.rgb(0, 214, 209)))
                                    .into(ivFotoPerfil);
                            char[] caracteres = (prefUtil.getStringValue("nombres").toLowerCase() + " " + prefUtil.getStringValue("apellidos").toLowerCase()).toCharArray();
                            caracteres[0] = Character.toUpperCase(caracteres[0]);
                            if ((prefUtil.getStringValue("nombres") + " " + prefUtil.getStringValue("apellidos")).length() > 2) {
                                for (int i = 0; i < (prefUtil.getStringValue("nombres").toLowerCase() + " " + prefUtil.getStringValue("apellidos").toLowerCase()).length(); i ++) {
                                    if (caracteres[i] == ' ') {
                                        caracteres[i + 1] = Character.toUpperCase(caracteres[i + 1]);
                                    }
                                    tvNombreUsuario.setText("" + tvNombreUsuario.getText() + caracteres[i]);
                                }
                            }
                            char[] caracteres_nombres = prefUtil.getStringValue("nombres").toLowerCase().toCharArray();
                            caracteres_nombres[0] = Character.toUpperCase(caracteres_nombres[0]);
                            if (prefUtil.getStringValue("nombres").length() > 2) {
                                for (int i = 0; i < prefUtil.getStringValue("nombres").toLowerCase().length(); i ++) {
                                    if (caracteres_nombres[i] == ' ') {
                                        caracteres_nombres[i + 1] = Character.toUpperCase(caracteres_nombres[i + 1]);
                                    }
                                    tvNombresPerfil.setText("" + tvNombresPerfil.getText() + caracteres_nombres[i]);
                                }
                            }
                            char[] caracteres_apellidos = prefUtil.getStringValue("apellidos").toLowerCase().toCharArray();
                            caracteres_apellidos[0] = Character.toUpperCase(caracteres_apellidos[0]);
                            if (prefUtil.getStringValue("apellidos").length() > 2) {
                                for (int i = 0; i < prefUtil.getStringValue("apellidos").toLowerCase().length(); i ++) {
                                    if (caracteres_apellidos[i] == ' ') {
                                        caracteres_apellidos[i + 1] = Character.toUpperCase(caracteres_apellidos[i + 1]);
                                    }
                                    tvApellidosPerfil.setText("" + tvApellidosPerfil.getText() + caracteres_apellidos[i]);
                                }
                            }
                            tvSaldo.setText("S/ " + String.format("%.2f", Double.parseDouble(prefUtil.getStringValue("saldo"))));
                            tvDni.setText(prefUtil.getStringValue("dni"));
                            tvCorreo.setText(prefUtil.getStringValue("correo"));
                            tvCelular.setText(prefUtil.getStringValue("telefono"));
                            tvMarca.setText(prefUtil.getStringValue("marca"));
                            tvModelo.setText(prefUtil.getStringValue("modelo"));
                            tvColor.setText(prefUtil.getStringValue("color"));
                            tvAnio.setText(prefUtil.getStringValue("anio"));
                            tvEmpresa.setText(prefUtil.getStringValue("empresa"));
                            tvUnidad.setText(prefUtil.getStringValue("numunidad"));
                            tvPlaca.setText(prefUtil.getStringValue("placa").toUpperCase());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }
}
