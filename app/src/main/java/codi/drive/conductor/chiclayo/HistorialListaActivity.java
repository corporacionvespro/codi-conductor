package codi.drive.conductor.chiclayo;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import codi.drive.conductor.chiclayo.adapters.HistorialAdapter;
import codi.drive.conductor.chiclayo.compartido.PrefUtil;
import codi.drive.conductor.chiclayo.entity.Historial;
import static codi.drive.conductor.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class HistorialListaActivity extends AppCompatActivity {
    @BindView(R.id.ivIcono) ImageView ivIcono;
    @BindView(R.id.rvHistorial) RecyclerView rvHistorial;
    @BindView(R.id.tvTitular) TextView tvTitular;
    @BindView(R.id.tvSumatoria) TextView tvSumatoria;
    ArrayList<Historial> historials;
    HistorialAdapter historialAdapter;
    PrefUtil prefUtil;
    double sumatoria;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_lista);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
        rvHistorial.setLayoutManager(new LinearLayoutManager(this));
        if (getIntent().getExtras() != null) {
            tvTitular.setText(getIntent().getStringExtra("titular"));
            ivIcono.setImageResource(getIntent().getExtras().getInt("icono"));
            cargarHistorial();
        }
        sumatoria = 0.0;
    }

    public void cargarHistorial() {
        Log.i("cargarHistorial", "HistorialListaActivity");
        historials = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/ws/obtener_historial_conductor.php?idconductor=" + prefUtil.getStringValue("idConductor") + "&fechadesde="
                        + getIntent().getStringExtra("fecha_desde") + "&fechahasta=" + getIntent().getStringExtra("fecha_hasta"));
                Log.i("cargarHistorial", result);
                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String idSolicitud = jsonArray.getJSONObject(i).getString("idsolicitud");
                            String foto = jsonArray.getJSONObject(i).getString("foto");
                            String nombres = jsonArray.getJSONObject(i).getString("nombres");
                            String apellidos = jsonArray.getJSONObject(i).getString("apellidos");
                            float valoracion = Float.parseFloat(jsonArray.getJSONObject(i).getString("valoracion"));
                            String telefono = jsonArray.getJSONObject(i).getString("telefono");
                            String direccionOrigen = jsonArray.getJSONObject(i).getString("direccion_origen");
                            String direccionDestino = jsonArray.getJSONObject(i).getString("direccion_destino");
                            int numDia = jsonArray.getJSONObject(i).getInt("numdia");
                            int numMes = jsonArray.getJSONObject(i).getInt("nummes");
                            int numFecha = jsonArray.getJSONObject(i).getInt("numfecha");
                            String hora = jsonArray.getJSONObject(i).getString("hora");
                            double pagoFinal = jsonArray.getJSONObject(i).getDouble("pagofinal");
                            sumatoria += pagoFinal;
                            Historial historial = new Historial(idSolicitud, foto, nombres, apellidos, valoracion, telefono, direccionOrigen, direccionDestino, numDia, numMes, numFecha, hora,
                                    pagoFinal);
                            historials.add(historial);
                        }
                        historialAdapter = new HistorialAdapter(HistorialListaActivity.this, historials);
                        rvHistorial.setAdapter(historialAdapter);
                        tvSumatoria.setText("Ganancias: S/ " + String.format("%.2f", sumatoria));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }
}
