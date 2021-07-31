package codi.drive.conductor.chiclayo;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import codi.drive.conductor.chiclayo.adapters.PromocionesListaAdapter;
import codi.drive.conductor.chiclayo.compartido.PrefUtil;
import codi.drive.conductor.chiclayo.entity.Promocion;
import static codi.drive.conductor.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class PromocionesListaActivity extends AppCompatActivity {
    @BindView(R.id.rvListaEmpresas)
    RecyclerView rvListaEmpresas;
    ArrayList<Promocion> promociones;
    PromocionesListaAdapter adapter;
    PrefUtil prefUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promociones_lista);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
        rvListaEmpresas.setLayoutManager(new LinearLayoutManager(this));
        if (getIntent().getExtras() != null) {
            String categoria = getIntent().getExtras().getString("categoria");
            Log.i("promociones", categoria);
            cargarEmpresas(categoria);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void cargarEmpresas(String categoria) {
        Log.i("cargarEmpresas", "PromocionesListaActivity");
        promociones = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/obtener_promociones.php?categoria=" + categoria);
                Log.i("cargarEmpresas", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String nombreEmpresa = jsonArray.getJSONObject(i).getString("empresa");
                            String descripcion = jsonArray.getJSONObject(i).getString("descripcion");
                            String logo = jsonArray.getJSONObject(i).getString("logo");
                            promociones.add(new Promocion(nombreEmpresa, descripcion, logo));
                        }
                        adapter = new PromocionesListaAdapter(PromocionesListaActivity.this, promociones);
                        rvListaEmpresas.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }
}
