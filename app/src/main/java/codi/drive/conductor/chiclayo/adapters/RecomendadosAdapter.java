package codi.drive.conductor.chiclayo.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sinaseyfi.advancedcardview.AdvancedCardView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import codi.drive.conductor.chiclayo.LugarActivity;
import codi.drive.conductor.chiclayo.R;
import codi.drive.conductor.chiclayo.entity.Recomendado;

/**
 * By: el-bryant
 */

public class RecomendadosAdapter extends RecyclerView.Adapter<RecomendadosAdapter.viewHolder> {
    Activity activity;
    ArrayList<Recomendado> recomendados;

    public RecomendadosAdapter(Activity activity, ArrayList<Recomendado> recomendados) {
        this.activity = activity;
        this.recomendados = recomendados;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.item_recomendado, parent, false);
        return new RecomendadosAdapter.viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Recomendado recomendado = recomendados.get(position);
        String idRecomendado = recomendado.getIdRecomendado();
        String nombre = recomendado.getNombre();
        Double latitud = recomendado.getLatitud();
        Double longitud = recomendado.getLongitud();
        String foto = recomendado.getFoto();
        String direccion = recomendado.getDireccion();
        holder.tvNombre.setText(nombre);
        Picasso.get().load(foto).into(holder.ivRecomendado);
        holder.acvRecomendado.setOnClickListener(v -> {
            Intent intent = new Intent(activity, LugarActivity.class);
            intent.putExtra("idRecomendado", idRecomendado);
            intent.putExtra("nombre", nombre);
            intent.putExtra("latitud", latitud);
            intent.putExtra("longitud", longitud);
            intent.putExtra("foto", foto);
            intent.putExtra("direccion", direccion);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    @Override
    public int getItemCount() {
        return recomendados.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        AdvancedCardView acvRecomendado;
        ImageView ivRecomendado;
        TextView tvNombre;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            acvRecomendado = (AdvancedCardView) itemView.findViewById(R.id.acvRecomendado);
            ivRecomendado = (ImageView) itemView.findViewById(R.id.ivRecomendado);
            tvNombre = (TextView) itemView.findViewById(R.id.tvNombre);
        }
    }
}
