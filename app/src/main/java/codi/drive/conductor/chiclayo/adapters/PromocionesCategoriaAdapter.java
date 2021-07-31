package codi.drive.conductor.chiclayo.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import codi.drive.conductor.chiclayo.PromocionesListaActivity;
import codi.drive.conductor.chiclayo.R;
import codi.drive.conductor.chiclayo.entity.PromocionesCategoria;

/**
 * By: El Bryant
 */

public class PromocionesCategoriaAdapter extends RecyclerView.Adapter<PromocionesCategoriaAdapter.ViewHolder> {
    Activity activity;
    ArrayList<PromocionesCategoria> categorias;

    public PromocionesCategoriaAdapter(Activity activity, ArrayList<PromocionesCategoria> categorias) {
        this.activity = activity;
        this.categorias = categorias;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.item_categoria, parent, false);
        return new PromocionesCategoriaAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PromocionesCategoria categoria = categorias.get(position);
        String nombre = categoria.getNombre();
        String foto = categoria.getFoto();
        holder.tvNombreCategoria.setText(nombre);
        Picasso.get().load(foto).into(holder.ivCategoria);
        holder.clayCategoria.setOnClickListener(view -> {
            Intent intent = new Intent(activity, PromocionesListaActivity.class);
            intent.putExtra("categoria", nombre);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout clayCategoria;
        TextView tvNombreCategoria;
        ImageView ivCategoria;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clayCategoria = (ConstraintLayout) itemView.findViewById(R.id.clayCategoria);
            ivCategoria = (ImageView) itemView.findViewById(R.id.ivCategoria);
            tvNombreCategoria = (TextView) itemView.findViewById(R.id.tvNombre);
        }
    }
}
