package codi.drive.conductor.chiclayo.compartido;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * By: el-bryant
 */

public class PrefUtil {
    private Context context;
    private static final String PREFERENCE_NAME = "prefconductor", PREFIJO = "conductor_";
    public static final String LOGIN_STATUS = "driver_login_status", CONDUCTOR_STATUS = "driver_conductor_status",
            BROADCAST_SOLICITUD_TAXI = "BroadcastSolicitudTaxi",
            BROADCAST_ACTUALIZAR_UBICACION = "BroadcastActualizarUbicacion";

    public PrefUtil(Context ctx) {
        this.context = ctx;
    }

    public void saveGenericValue(String campo, String valor) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFIJO + campo, valor);
        editor.apply();
    }

    public void clearAll() {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public String getStringValue(String campo) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PREFIJO + campo,"");
    }
}