package codi.drive.conductor.chiclayo.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sinaseyfi.advancedcardview.AdvancedCardView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import codi.drive.conductor.chiclayo.HistorialDetalleActivity;
import codi.drive.conductor.chiclayo.R;
import codi.drive.conductor.chiclayo.compartido.BorderedCircleTransform;
import codi.drive.conductor.chiclayo.entity.Historial;

/**
 * By: el-bryant
 */

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.viewHolder> {
    Activity activity;
    ArrayList<Historial> historials;
    String[] dias = {"", "Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom"};
    String[] meses = {"", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};

    public HistorialAdapter(Activity activity, ArrayList<Historial> historials) {
        this.activity = activity;
        this.historials = historials;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.item_historial, parent, false);
        return new HistorialAdapter.viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.tvApellidosPasajero.setText("");
        holder.tvNombresPasajero.setText("");
        holder.rbValoracion.setRating(0);
        holder.tvPagoFinal.setText("");
        holder.tvFecha.setText("");
        holder.tvHora.setText("");
        Historial historial = historials.get(position);
        String idSolicitud = historial.getIdSolicitud();
        String foto = historial.getFoto();
        String nombres = historial.getNombres();
        String apellidos = historial.getApellidos();
        float valoracion = historial.getValoracion();
        String telefono = historial.getTelefono();
        String direccionOrigen = historial.getDireccion_origen();
        String direccionDestino = historial.getDireccion_destino();
        int numDia = historial.getNumdia() - 1;
        int numMes = historial.getNummes();
        int numFecha = historial.getNumfecha();
        String hora = historial.getHora();
        Double pagoFinal = historial.getPagofinal();
        Picasso.get().load(foto).transform(new BorderedCircleTransform(5, Color.rgb(0, 214, 209))).into(holder.ivFotoPasajero);
        holder.rbValoracion.setRating(valoracion);
        char[] caracteres_apellidos = (apellidos.toLowerCase()).toCharArray();
        caracteres_apellidos[0] = Character.toUpperCase(caracteres_apellidos[0]);
        for (int i = 0; i < (apellidos.toLowerCase()).length(); i ++) {
            if (caracteres_apellidos[i] == ' ') {
                if (i < apellidos.length() - 1) {
                    caracteres_apellidos[i + 1] = Character.toUpperCase(caracteres_apellidos[i + 1]);
                }
            }
            holder.tvApellidosPasajero.setText("" + holder.tvApellidosPasajero.getText().toString() + caracteres_apellidos[i]);
        }
        holder.tvFecha.setText(dias[numDia] + " " + numFecha + " " + meses[numMes]);
        holder.tvHora.setText(hora);
        char[] caracteres_nombres = (nombres.toLowerCase()).toCharArray();
        caracteres_nombres[0] = Character.toUpperCase(caracteres_nombres[0]);
        for (int i = 0; i < (nombres.toLowerCase()).length(); i ++) {
            if (caracteres_nombres[i] == ' ') {
                if (i < nombres.length() - 1) {
                    caracteres_nombres[i + 1] = Character.toUpperCase(caracteres_nombres[i + 1]);
                }
            }
            holder.tvNombresPasajero.setText("" + holder.tvNombresPasajero.getText().toString() + caracteres_nombres[i]);
        }
        holder.tvPagoFinal.setText("S/ " + String.format("%.2f", pagoFinal));
        holder.acvHistorial.setOnClickListener(v -> {
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(activity,
                    Pair.create(holder.ivFotoPasajero, holder.ivFotoPasajero.getTransitionName()),
                    Pair.create(holder.tvNombresPasajero, holder.tvNombresPasajero.getTransitionName()),
                    Pair.create(holder.tvApellidosPasajero, holder.tvApellidosPasajero.getTransitionName()),
                    Pair.create(holder.rbValoracion, holder.rbValoracion.getTransitionName()),
                    Pair.create(holder.tvPagoFinal, holder.tvPagoFinal.getTransitionName())
            );
            Intent intent = new Intent(activity, HistorialDetalleActivity.class);
            intent.putExtra("idSolicitud", idSolicitud);
            intent.putExtra("foto", foto);
            intent.putExtra("nombres", nombres);
            intent.putExtra("apellidos", apellidos);
            intent.putExtra("valoracion", valoracion);
            intent.putExtra("telefono", telefono);
            intent.putExtra("direccionOrigen", direccionOrigen);
            intent.putExtra("direccionDestino", direccionDestino);
            intent.putExtra("fecha", dias[numDia] + " " + numFecha + " " + meses[numMes]);
            intent.putExtra("hora", hora);
            intent.putExtra("pagoFinal", pagoFinal);
            activity.startActivity(intent, activityOptions.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return historials.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        AdvancedCardView acvHistorial;
        ImageView ivFotoPasajero;
        RatingBar rbValoracion;
        TextView tvApellidosPasajero, tvFecha, tvHora, tvNombresPasajero, tvPagoFinal;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            acvHistorial = (AdvancedCardView) itemView.findViewById(R.id.acvHistorial);
            ivFotoPasajero = (ImageView) itemView.findViewById(R.id.ivFotoPasajero);
            rbValoracion = (RatingBar) itemView.findViewById(R.id.rbValoracion);
            tvApellidosPasajero = (TextView) itemView.findViewById(R.id.tvApellidosPasajero);
            tvFecha = (TextView) itemView.findViewById(R.id.tvFecha);
            tvHora = (TextView) itemView.findViewById(R.id.tvHora);
            tvNombresPasajero = (TextView) itemView.findViewById(R.id.tvNombresPasajero);
            tvPagoFinal = (TextView) itemView.findViewById(R.id.tvPagoFinal);
        }
    }
}
