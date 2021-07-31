package codi.drive.conductor.chiclayo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.conductor.chiclayo.adapters.PromocionesCategoriaAdapter;
import codi.drive.conductor.chiclayo.compartido.BorderedCircleTransform;
import codi.drive.conductor.chiclayo.compartido.PrefUtil;
import codi.drive.conductor.chiclayo.entity.PromocionesCategoria;
import static codi.drive.conductor.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class PromocionesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.dlayMenu) DrawerLayout dlayMenu;
    @BindView(R.id.nvMenu) NavigationView nvMenu;
    @BindView(R.id.rvCategorias) RecyclerView rvCategorias;
    ArrayList<PromocionesCategoria> categorias;
    ImageView ivFotoUsuario;
    PrefUtil prefUtil;
    PromocionesCategoriaAdapter promocionesCategoriaAdapter;
    SwitchCompat swNoche;
    TextView tvNombreUsuario, tvSaldo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promociones);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
        rvCategorias.setLayoutManager(new LinearLayoutManager(this));
        ivFotoUsuario = (ImageView) nvMenu.getHeaderView(0).findViewById(R.id.ivFotoUsuario);
        tvNombreUsuario = (TextView) nvMenu.getHeaderView(0).findViewById(R.id.tvNombreUsuario);
        tvSaldo = (TextView) nvMenu.getHeaderView(0).findViewById(R.id.tvSaldo);
        Picasso.get().load(prefUtil.getStringValue("foto")).transform(new BorderedCircleTransform(5, Color.rgb(0, 214, 209))).into(ivFotoUsuario);
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
        nvMenu.setNavigationItemSelectedListener(this);
        tvSaldo.setText("S/ " + prefUtil.getStringValue("saldo").substring(0, prefUtil.getStringValue("saldo").indexOf(".") + 2));
        cargarCategorias();
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
                startActivity(new Intent(PromocionesActivity.this, AcercaAppActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_cerrar:
                prefUtil.clearAll();
                startActivity(new Intent(PromocionesActivity.this, Acceso1Activity.class));
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
                startActivity(new Intent(PromocionesActivity.this, HistorialActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_inicio:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_lugares:
                startActivity(new Intent(PromocionesActivity.this, RecomendadosActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_ocupado:
                prefUtil.saveGenericValue("disponible", "NO");
                startActivity(new Intent(PromocionesActivity.this, OcupadoActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_perfil:
                startActivity(new Intent(PromocionesActivity.this, PerfilActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_promociones:
                dlayMenu.closeDrawer(GravityCompat.START);
                break;
        }
        dlayMenu.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.acvMenu) void abrirMenu() {
        dlayMenu.openDrawer(GravityCompat.START);
    }

    public void cargarCategorias() {
        Log.i("cargarCategorias", "PromocionesCategoriaActivity");
        categorias = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/obtener_promociones_conductor.php?idconductor=" + prefUtil.getStringValue("idConductor"));
                Log.i("cargarCategorias", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String nombreCategoria = jsonArray.getJSONObject(i).getString("categoria");
                            String foto = jsonArray.getJSONObject(i).getString("icono");
                            PromocionesCategoria categoria = new PromocionesCategoria(nombreCategoria, foto);
                            if (!existe(categoria, categorias)) {
                                categorias.add(categoria);
                            }
                            promocionesCategoriaAdapter = new PromocionesCategoriaAdapter(PromocionesActivity.this, categorias);
                            rvCategorias.setAdapter(promocionesCategoriaAdapter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public boolean existe(PromocionesCategoria categoria, ArrayList<PromocionesCategoria> categorias) {
        boolean existe = false;
        for (int i = 0; i < categorias.size(); i++) {
            if (categoria.getNombre().equals(categorias.get(i).getNombre())) {
                existe = true;
            }
        }
        return existe;
    }
}
