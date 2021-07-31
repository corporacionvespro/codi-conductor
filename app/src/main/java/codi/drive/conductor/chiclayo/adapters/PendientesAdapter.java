package codi.drive.conductor.chiclayo.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import codi.drive.conductor.chiclayo.R;
import codi.drive.conductor.chiclayo.SolicitudTaxiActivity;
import codi.drive.conductor.chiclayo.compartido.BorderedCircleTransform;
import codi.drive.conductor.chiclayo.entity.SolicitudTaxi;

/**
 * By: el-bryant
 */

public class PendientesAdapter extends RecyclerView.Adapter<PendientesAdapter.viewHolder> {
    Activity activity;
    ArrayList<SolicitudTaxi> solicitudTaxis;

    public PendientesAdapter(Activity activity, ArrayList<SolicitudTaxi> solicitudTaxis) {
        this.activity = activity;
        this.solicitudTaxis = solicitudTaxis;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.item_pendiente, parent, false);
        return new PendientesAdapter.viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
//        holder.tvApellidosPasajero.setText("");
        holder.tvNombresPasajero.setText("");
        SolicitudTaxi solicitudTaxi = solicitudTaxis.get(position);
        String idSolicitud = solicitudTaxi.getIdSolicitudTaxi();
        String estado = solicitudTaxi.getEstado();
        Double pagoFinal = solicitudTaxi.getPagoFinal();
        Double tarifa = solicitudTaxi.getTarifa();
        String apellidosPasajero = solicitudTaxi.getApellidosPasajero();
        String nombresPasajero = solicitudTaxi.getNombresPasajero();
        String telefonoPasajero = solicitudTaxi.getTelefonoPasajero();
        String fotoPasajero = solicitudTaxi.getFotoPasajero();
        Double latitudRecojo = solicitudTaxi.getLatitudRecojo();
        Double longitudRecojo = solicitudTaxi.getLongitudRecojo();
        Double latitudDestino = solicitudTaxi.getLatitudDestino();
        Double longitudDestino = solicitudTaxi.getLongitudDestino();
        String direccionOrigen = solicitudTaxi.getDireccionOrigen();
        String direccionDestino = solicitudTaxi.getDireccionDestino();
        String referencia = solicitudTaxi.getReferencia();
        String codigo = solicitudTaxi.getCodigo();
        String fechaHora = solicitudTaxi.getFechaHora();
        char[] caracteres_nombres = nombresPasajero.toLowerCase().toCharArray();
        caracteres_nombres[0] = Character.toUpperCase(caracteres_nombres[0]);
        if (nombresPasajero.length() > 2) {
            for (int i = 0; i < nombresPasajero.toLowerCase().length(); i ++) {
                if (caracteres_nombres[i] == ' ') {
                    caracteres_nombres[i + 1] = Character.toUpperCase(caracteres_nombres[i + 1]);
                }
                holder.tvNombresPasajero.setText("" + holder.tvNombresPasajero.getText() + caracteres_nombres[i]);
            }
        }
        holder.tvDireccionOrigen.setText(direccionOrigen);
        holder.tvFechaHora.setText(fechaHora.substring(8, 10) + "/" + fechaHora.substring(5, 7) + "/" + fechaHora.substring(0, 4) + "     " + fechaHora.substring(11, 16));
        Picasso.get().load(fotoPasajero).transform(new BorderedCircleTransform(5, Color.rgb(0, 214, 209))).into(holder.ivFotoPasajero);
        holder.btnVer.setOnClickListener(v -> {
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(activity,
//                    Pair.create(holder.tvApellidosPasajero, holder.tvApellidosPasajero.getTransitionName()),
                    Pair.create(holder.tvNombresPasajero, holder.tvNombresPasajero.getTransitionName()),
                    Pair.create(holder.ivFotoPasajero, holder.ivFotoPasajero.getTransitionName())
            );
            Intent intent = new Intent(activity, SolicitudTaxiActivity.class);
            intent.putExtra("idSolicitud", idSolicitud);
            intent.putExtra("estado", estado);
            intent.putExtra("pagoFinal", pagoFinal);
            intent.putExtra("tarifa", tarifa);
            intent.putExtra("apellidosPasajero", apellidosPasajero);
            intent.putExtra("nombresPasajero", nombresPasajero);
            intent.putExtra("telefonoPasajero", telefonoPasajero);
            intent.putExtra("fotoPasajero", fotoPasajero);
            intent.putExtra("latitudRecojo", latitudRecojo);
            intent.putExtra("longitudRecojo", longitudRecojo);
            intent.putExtra("latitudDestino", latitudDestino);
            intent.putExtra("longitudDestino", longitudDestino);
            intent.putExtra("direccionOrigen", direccionOrigen);
            intent.putExtra("direccionDestino", direccionDestino);
            intent.putExtra("referencia", referencia);
            intent.putExtra("codigo", codigo);
            activity.startActivity(intent, activityOptions.toBundle());
            activity.finish();
        });
    }

    @Override
    public int getItemCount() {
        return solicitudTaxis.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        Button btnVer;
        ImageView ivFotoPasajero;
        TextView tvApellidosPasajero, tvFechaHora, tvNombresPasajero, tvDireccionOrigen;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            btnVer = (Button) itemView.findViewById(R.id.btnVer);
            ivFotoPasajero = (ImageView) itemView.findViewById(R.id.ivFotoPasajero);
            tvApellidosPasajero = (TextView) itemView.findViewById(R.id.tvApellidosPasajero);
            tvFechaHora = (TextView) itemView.findViewById(R.id.tvFechaHora);
            tvNombresPasajero = (TextView) itemView.findViewById(R.id.tvNombresPasajero);
            tvDireccionOrigen = (TextView) itemView.findViewById(R.id.tvDireccionOrigen);
        }
    }
}
