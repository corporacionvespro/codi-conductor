package codi.drive.conductor.chiclayo.compartido;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * By: el-bryant
 */

public class WebService implements Response.Listener, Response.ErrorListener {
    private Activity actividad;
    private Context context;
    private final PrefUtil prefUtil;
    private final RequestQueue requestQueue;

    public WebService(Context ctx, boolean servicio) {
        this.context = ctx;
        requestQueue = Volley.newRequestQueue(context);
        prefUtil = new PrefUtil(context);
    }

    public WebService(Activity mActividad) {
        this.actividad = mActividad;
        requestQueue = Volley.newRequestQueue(actividad);
        prefUtil = new PrefUtil(actividad);
    }

    public void enviarUbicacion() {
        Map<String, String> params = new HashMap<>();
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("WebService", "actualizarToken", task.getException());
                return;
            }
            String token = task.getResult().getToken();
            params.put("idconductor", prefUtil.getStringValue("idConductor"));
            params.put("latitud", prefUtil.getStringValue("latitud"));
            params.put("longitud", prefUtil.getStringValue("longitud"));
            params.put("token", token);
            StringRequestApp jsObjRequest = new StringRequestApp(Request.Method.POST, "http://codidrive.com/vespro/logica/enviar_ubicacion_motorizado.php", this,this);
            jsObjRequest.setParametros(params);
            requestQueue.add(jsObjRequest);
        });
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        String message = "";
        if (error instanceof NetworkError) {
            message = "No se puede conectar a Internet ... Por favor, compruebe su conexión!";
        } else if (error instanceof ServerError) {
            message = "No se pudo encontrar el servidor. ¡Inténtalo nuevamente dentro de unos minutos!";
        } else if (error instanceof AuthFailureError) {
            message = "No se puede conectar a Internet ... Por favor, compruebe su conexión!";
        } else if (error instanceof ParseError) {
            message = "¡Error! ¡Inténtalo nuevamente dentro de unos minutos!";
        } else if (error instanceof TimeoutError) {
            message = "¡El tiempo de conexión expiro! Por favor revise su conexion a internet.";
        } else {
            message = "¡Error en obtención de datos! " + error.getMessage();
        }
        if (actividad != null) {
            Log.i("wsApp-onErrorResponse", message);
        } else if(context != null) {
            Log.i("wsApp-onErrorResponse", message);
        }
    }

    @Override
    public void onResponse(Object response) {
        Log.i("response", "" + response);
        JSONObject obj;
        try {
            obj = new JSONObject(response.toString());
            procesarLlamada(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void procesarLlamada(JSONObject jsonObject) {
        try {
            String accion = jsonObject.getString("accion");
            JSONObject data = null;
            Boolean correcto = false;
            if ("enviar_ubicacion_motorizado".equals(accion)) {
                if (context != null) {
                    if (context instanceof ServiceLocation) {
                        ((ServiceLocation) context).envioPosicionTerminado();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
