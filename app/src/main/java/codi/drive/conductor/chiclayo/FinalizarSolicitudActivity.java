package codi.drive.conductor.chiclayo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.conductor.chiclayo.compartido.CircleTransform;
import codi.drive.conductor.chiclayo.compartido.PrefUtil;
import static codi.drive.conductor.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class FinalizarSolicitudActivity extends AppCompatActivity {
    @BindView(R.id.etOpinion) EditText etOpinion;
    @BindView(R.id.ivFotoPasajero) ImageView ivFotoPasajero;
    @BindView(R.id.rbOpinion) RatingBar rbOpinion;
    @BindView(R.id.tvApellidosPasajero) TextView tvApellidosPasajero;
    @BindView(R.id.tvNombresPasajero) TextView tvNombresPasajero;
    @BindView(R.id.tvTarifa) TextView tvTarifa;
    Double pagoFinal = 0.0, nuevosaldo = 0.0;
    PrefUtil prefUtil;
    String idSolicitud = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizar_solicitud);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
        if (getIntent().getExtras() != null) {
            String nombresPasajero = getIntent().getStringExtra("nombresPasajero");
            String apellidosPasajero = getIntent().getStringExtra("apellidosPasajero");
            String fotoPasajero = getIntent().getStringExtra("fotoPasajero");
            pagoFinal = Double.parseDouble(getIntent().getStringExtra("pagoFinal"));
            idSolicitud = getIntent().getStringExtra("idSolicitud");
            tvNombresPasajero.setText(nombresPasajero);
            tvApellidosPasajero.setText(apellidosPasajero);
            Picasso.get().load(fotoPasajero).transform(new CircleTransform()).into(ivFotoPasajero);
            tvTarifa.setText("S/ " + String.format("%.2f", Double.parseDouble(prefUtil.getStringValue("pagoFinal"))));
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @OnClick(R.id.btnAceptar) void finalizar() {
        Log.i("finalizar", "FinalizarSolicitudActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/finalizar_solicitud.php?idsolicitud=" + idSolicitud + "&opinion=" + etOpinion.getText().toString()
                        + "&valoracion=" + rbOpinion.getRating());
                Log.i("finalizar", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                prefUtil.saveGenericValue("idSolicitud", "");
                                descontarSaldo();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }.start();
    }

    public void descontarSaldo() {
        Log.i("descontarSaldo", "FinalizarPedidoActivity");
        if (pagoFinal > 4.99) {
            nuevosaldo = Double.parseDouble(prefUtil.getStringValue("saldo")) - (pagoFinal * 0.1);
        } else {
            nuevosaldo = Double.parseDouble(prefUtil.getStringValue("saldo"));
        }
        Toast.makeText(this, "Gracias por su opinión.", Toast.LENGTH_LONG).show();
        prefUtil.saveGenericValue("saldo", "" + nuevosaldo);
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/descontar_saldo.php?idconductor=" + prefUtil.getStringValue("idConductor") + "&saldo=" +
                        nuevosaldo);
                Log.i("descontarSaldo", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                prefUtil.saveGenericValue("saldo", "" + nuevosaldo);
                                startActivity(new Intent(FinalizarSolicitudActivity.this, MainActivity.class));
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
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
