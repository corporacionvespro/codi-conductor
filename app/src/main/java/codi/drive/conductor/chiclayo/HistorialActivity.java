package codi.drive.conductor.chiclayo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
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
import org.json.JSONObject;
import java.util.Calendar;
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

public class HistorialActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.dlayMenu) DrawerLayout dlayMenu;
    @BindView(R.id.ivAnio) ImageView ivAnio;
    @BindView(R.id.ivHoy) ImageView ivHoy;
    @BindView(R.id.ivMes) ImageView ivMes;
    @BindView(R.id.ivSemana) ImageView ivSemana;
    @BindView(R.id.nvMenu) NavigationView nvMenu;
    @BindView(R.id.tvAnio) TextView tvAnio;
    @BindView(R.id.tvEntreFechas) TextView tvEntreFechas;
    @BindView(R.id.tvHoy) TextView tvHoy;
    @BindView(R.id.tvMes) TextView tvMes;
    @BindView(R.id.tvSemana) TextView tvSemana;
    ImageView ivFotoUsuario;
    PrefUtil prefUtil;
    SwitchCompat swNoche;
    TextView tvNombreUsuario, tvSaldo;
    int mDiaDesde, mMesDesde, mAnioDesde, mDiaHasta, mMesHasta, mAnioHasta;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
        ivFotoUsuario = (ImageView) nvMenu.getHeaderView(0).findViewById(R.id.ivFotoUsuario);
        tvNombreUsuario = (TextView) nvMenu.getHeaderView(0).findViewById(R.id.tvNombreUsuario);
        tvSaldo = (TextView) nvMenu.getHeaderView(0).findViewById(R.id.tvSaldo);
        String nombre_completo = prefUtil.getStringValue("nombres") + " " + prefUtil.getStringValue("apellidos");
        char[] caracteres = (nombre_completo.toLowerCase()).toCharArray();
        caracteres[0] = Character.toUpperCase(caracteres[0]);
        for (int i = 0; i < (nombre_completo.toLowerCase()).length(); i ++) {
            if (caracteres[i] == ' ') {
                if (i < nombre_completo.length() - 1) {
                    caracteres[i + 1] = Character.toUpperCase(caracteres[i + 1]);
                }
            }
            tvNombreUsuario.setText("" + tvNombreUsuario.getText().toString() + caracteres[i]);
        }
        Picasso.get().load(prefUtil.getStringValue("foto")).transform(new BorderedCircleTransform(15, Color.rgb(0, 214, 209))).into(ivFotoUsuario);
        tvSaldo.setText("S/ " + prefUtil.getStringValue("saldo").substring(0, prefUtil.getStringValue("saldo").indexOf(".") + 2));
        nvMenu.setNavigationItemSelectedListener(this);
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
                startActivity(new Intent(HistorialActivity.this, AcercaAppActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_cerrar:
                enviarEstadoOcupado();
                prefUtil.clearAll();
                stopService(new Intent(HistorialActivity.this, ServiceLocation.class));
                stopService(new Intent(HistorialActivity.this, LocationUpdatesIntentService.class));
                stopService(new Intent(HistorialActivity.this, LocationUpdatesBroadcastReceiver.class));
                MainActivity.ubicaciones.cancel();
                Utils.setRequestingLocationUpdates(this, false);
                prefUtil.saveGenericValue("disponible", "NO");
                startActivity(new Intent(HistorialActivity.this, Acceso1Activity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_compartir:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                String msj = "Te invito a descargar la App CODI CONDUCTOR y pertenecer a la red más grande de conductores." +
                        " https://play.google.com/store/apps/details?id=codi.drive.conductor.chiclayo ";
                intent.putExtra(Intent.EXTRA_TEXT, msj);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Compartir en"));
                break;
            case R.id.nav_historial:
                dlayMenu.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_inicio:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_lugares:
                startActivity(new Intent(HistorialActivity.this, CategoriaActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_noche:
                swNoche.setChecked(!swNoche.isChecked());
                break;
            case R.id.nav_perfil:
                startActivity(new Intent(HistorialActivity.this, PerfilActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_promociones:
                startActivity(new Intent(HistorialActivity.this, PromocionesActivity.class));
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

    @OnClick(R.id.clayAnio) void cargarAnio() {
        Calendar newCalendar = Calendar.getInstance();
        this.mDiaDesde = 1;
        this.mMesDesde = 0;
        this.mAnioDesde = newCalendar.get(Calendar.YEAR);
        this.mDiaHasta = newCalendar.get(Calendar.DAY_OF_MONTH);
        this.mMesHasta = newCalendar.get(Calendar.MONTH);
        this.mAnioHasta = newCalendar.get(Calendar.YEAR);
        consultarHistorial(tvAnio, ivAnio, R.drawable.ic_anio);
    }

    @OnClick(R.id.clayEntreFechas) void cargarEntreFechas() {
        Intent intent = new Intent(HistorialActivity.this, HistorialEntreFechasActivity.class);
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(tvEntreFechas, tvEntreFechas.getTransitionName()));
        startActivity(intent, activityOptions.toBundle());
    }

    @OnClick(R.id.clayHoy) void cargarHoy() {
        Calendar newCalendar = Calendar.getInstance();
        this.mDiaDesde = newCalendar.get(Calendar.DAY_OF_MONTH);
        this.mMesDesde = newCalendar.get(Calendar.MONTH);
        this.mAnioDesde = newCalendar.get(Calendar.YEAR);
        this.mDiaHasta = newCalendar.get(Calendar.DAY_OF_MONTH);
        this.mMesHasta = newCalendar.get(Calendar.MONTH);
        this.mAnioHasta = newCalendar.get(Calendar.YEAR);
        consultarHistorial(tvHoy, ivHoy, R.drawable.ic_dia);
    }

    @OnClick(R.id.clayMes) void cargarMes() {
        Calendar newCalendar = Calendar.getInstance();
        this.mDiaDesde = 1;
        this.mMesDesde = newCalendar.get(Calendar.MONTH);
        this.mAnioDesde = newCalendar.get(Calendar.YEAR);
        this.mDiaHasta = newCalendar.get(Calendar.DAY_OF_MONTH);
        this.mMesHasta = newCalendar.get(Calendar.MONTH);
        this.mAnioHasta = newCalendar.get(Calendar.YEAR);
        consultarHistorial(tvMes, ivMes, R.drawable.ic_mes);
    }

    @OnClick(R.id.claySemana) void cargarSemana() {
        Calendar newCalendar = Calendar.getInstance();
        this.mDiaHasta = newCalendar.get(Calendar.DAY_OF_MONTH);
        this.mMesHasta = newCalendar.get(Calendar.MONTH);
        this.mAnioHasta = newCalendar.get(Calendar.YEAR);
        newCalendar.set(Calendar.DAY_OF_WEEK, newCalendar.getFirstDayOfWeek());
        this.mDiaDesde = newCalendar.get(Calendar.DAY_OF_MONTH);
        this.mMesDesde = newCalendar.get(Calendar.MONTH);
        this.mAnioDesde = newCalendar.get(Calendar.YEAR);
        consultarHistorial(tvSemana, ivSemana, R.drawable.ic_semana);
    }

    public void consultarHistorial(TextView tvTitulo, ImageView ivIcono, int recurso) {
        String fechaDesde = mAnioDesde + "-" + (mMesDesde + 1) + "-" + mDiaDesde;
        String fechaHasta = mAnioHasta + "-" + (mMesHasta + 1) + "-" + mDiaHasta;
        Log.i("consultarHistorial", fechaDesde + ", " + fechaHasta);
        Intent intent = new Intent(HistorialActivity.this, HistorialListaActivity.class);
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this,
                Pair.create(tvTitulo, tvTitulo.getTransitionName()),
                Pair.create(ivIcono, ivIcono.getTransitionName())
        );
        intent.putExtra("fecha_desde", fechaDesde);
        intent.putExtra("fecha_hasta", fechaHasta);
        intent.putExtra("titular", tvTitulo.getText().toString());
        intent.putExtra("icono", recurso);
        startActivity(intent, activityOptions.toBundle());
    }

    public void enviarEstadoOcupado() {
        Log.i("enviarEstadoOcupado", "HistorialActivity");
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
}
