package codi.drive.conductor.chiclayo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import codi.drive.conductor.chiclayo.compartido.PrefUtil;
import static codi.drive.conductor.chiclayo.compartido.Funciones.primero;

/**
 * By: el-bryant
 */

public class CancelarAcvitity extends AppCompatActivity {
    @BindView(R.id.etOpinion) EditText etOpinion;
    PrefUtil prefUtil;
    String idSolicitud;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelar);
        ButterKnife.bind(this);
        prefUtil = new PrefUtil(this);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CancelarAcvitity.this, SolicitudTaxiActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @OnClick(R.id.btnAceptar) void cancelarServicio() {
        Log.i("cancelarServicio", "CancelarActivity");
        new Thread() {
            @Override
            public void run() {
                final String result = primero("http://codidrive.com/vespro/logica/cancelar_solicitud.php?idsolicitud=" + prefUtil.getStringValue("idSolicitud") + "&opinion="
                        + etOpinion.getText().toString());
                Log.i("cancelarServicio", result);
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.length() > 0) {
                            boolean correcto = jsonObject.getBoolean("correcto");
                            if (correcto) {
                                prefUtil.saveGenericValue("idSolicitud", "");
                                Intent intent = new Intent(CancelarAcvitity.this, MainActivity.class);
                                startActivity(intent);
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
