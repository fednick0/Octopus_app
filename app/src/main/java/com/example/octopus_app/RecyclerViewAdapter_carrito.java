package com.example.octopus_app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class RecyclerViewAdapter_carrito extends RecyclerView.Adapter<RecyclerViewAdapter_carrito.ViewHolderInfo> implements View.OnLongClickListener{

    private ArrayList<ArrayList<String>> listDatos;
    private View.OnLongClickListener listener;

    public RecyclerViewAdapter_carrito(ArrayList<ArrayList<String>> listDatos) {
        this.listDatos = listDatos;
    }

    @NonNull
    @Override
    public ViewHolderInfo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recyclerview_carrito, null, false);

        view.setOnLongClickListener(this);

        return new ViewHolderInfo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_carrito.ViewHolderInfo holder, int position) {
        // primero la posicion
        holder.cNumero.setText(Integer.toString(position + 1) + ".  ");

        // pongo idReservaSend en cModelo como tag, no es visible
        holder.cModelo.setTag(listDatos.get(position).get(0));

        // cargo la imagen
        byte[] decoString = Base64.decode(listDatos.get(position).get(1), Base64.DEFAULT);
        Bitmap decoded = BitmapFactory.decodeByteArray(decoString, 0, decoString.length);
        holder.fotoChancla.setImageBitmap(decoded);

        // los demas datos
        holder.cMarca.setText(listDatos.get(position).get(2));
        holder.cModelo.setText(listDatos.get(position).get(3));
        holder.cUbicacion.setText(listDatos.get(position).get(4));
        holder.cMaterial.setText(listDatos.get(position).get(5));
        holder.cTipo.setText(listDatos.get(position).get(6));
        holder.cMedida.setText(listDatos.get(position).get(7));
        holder.cColor.setText(listDatos.get(position).get(8));

        holder.cPrecio.setText("Mex" +
                NumberFormat.getCurrencyInstance(Locale.US).format(
                        Double.parseDouble(
                                listDatos.get(position).get(9)
                        )
                ));

        holder.cCantidad.setText(listDatos.get(position).get(10));

        holder.cTotal.setPaintFlags(holder.cTotal.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.cTotal.setText("Mex" +
                NumberFormat.getCurrencyInstance(Locale.US).format(
                        Double.parseDouble(
                                listDatos.get(position).get(11)
                        )
                ));
    }

    @Override
    public boolean onLongClick(View v) {
        if (listener !=  null)
            listener.onLongClick(v);

        return false;
    }

    @Override
    public int getItemCount() {
        return listDatos.size();
    }

    public void setOnLongClickListener(View.OnLongClickListener listener_) {
        this.listener = listener_;
    }

    public void recargarCarrito(ArrayList<ArrayList<String>> listDatos) {
        this.listDatos = listDatos;
        notifyDataSetChanged();
    }

    public double gettotalTotal() {
        double acum = 0;

        for (int i = 0; i < listDatos.size(); i++)
            acum += Double.parseDouble(listDatos.get(i).get(11));

        return acum;
    }

    public void restarCarrito(int eliminar, int position) {
        String cantidadActual = listDatos.get(position).get(10);
        int nuevaCantidad = Integer.parseInt(cantidadActual) - eliminar;

        if (nuevaCantidad > 0) {
            // cantidad
            listDatos.get(position).set(10, Integer.toString(nuevaCantidad));

            double precio = Double.parseDouble(listDatos.get(position).get(9));
            double nuevoTotal = precio * nuevaCantidad;

            // total
            listDatos.get(position).set(11, Double.toString(nuevoTotal));
        }
        else
            listDatos.remove(position);

        notifyDataSetChanged();
    }

    public class ViewHolderInfo extends RecyclerView.ViewHolder{

        ImageView fotoChancla;
        TextView cNumero, cMarca, cModelo, cUbicacion,
                cMaterial, cTipo, cColor, cMedida,
                cPrecio, cCantidad, cTotal;

        public ViewHolderInfo(@NonNull View itemView) {
            super(itemView);

            cNumero = itemView.findViewById(R.id.cNumero);
            fotoChancla = itemView.findViewById(R.id.fotoChancla);

            cMarca = itemView.findViewById(R.id.cMarca);
            cModelo = itemView.findViewById(R.id.cModelo);
            cUbicacion = itemView.findViewById(R.id.cUbicacion);
            cMaterial = itemView.findViewById(R.id.cMaterial);
            cTipo = itemView.findViewById(R.id.cTipo);
            cColor = itemView.findViewById(R.id.cColor);
            cMedida = itemView.findViewById(R.id.cMedida);
            cPrecio = itemView.findViewById(R.id.cPrecio);
            cCantidad = itemView.findViewById(R.id.cCantidad);
            cTotal = itemView.findViewById(R.id.cTotal);
        }
    }
}
