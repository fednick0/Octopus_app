package com.example.octopus_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter_medidas extends RecyclerView.Adapter<RecyclerViewAdapter_medidas.ViewHolderInfo> implements View.OnClickListener {

    ArrayList<ArrayList<String>> listDatos;
    private View.OnClickListener listener;

    public RecyclerViewAdapter_medidas(ArrayList<ArrayList<String>> listDatos) {
        this.listDatos = listDatos;
    }

    public void update(ArrayList<ArrayList<String>> listDatos) {
        this.listDatos = listDatos;
    }

    @NonNull
    @Override
    public ViewHolderInfo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recyclerview_medidas, null, false);

        view.setOnClickListener(this);

        return new ViewHolderInfo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderInfo holder, int position) {
        holder.t_medida.setTag(listDatos.get(position).get(0));         // id_m
        holder.t_medida.setText(listDatos.get(position).get(1));        // medida_m
        holder.t_existencia.setText(listDatos.get(position).get(2));    // existencia_local
    }

    @Override
    public int getItemCount() {
        return listDatos.size();
    }

    public void setOnClickListener(View.OnClickListener listener_) {
        this.listener = listener_;
    }

    @Override
    public void onClick(View v) {
        if (listener !=  null)
            listener.onClick(v);
    }

    public class ViewHolderInfo extends RecyclerView.ViewHolder {

        TextView t_medida, t_existencia;

        public ViewHolderInfo(@NonNull View itemView) {
            super(itemView);

            t_medida = itemView.findViewById(R.id.text_medida);
            t_existencia = itemView.findViewById(R.id.text_existencia);
        }
    }
}