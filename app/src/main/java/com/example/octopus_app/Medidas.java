package com.example.octopus_app;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;

public class Medidas extends Fragment {

    private InterfaceVolleyResult interfaceVolleyResult;
    private VolleyMethods volleyMethods;
    private FuncionesUnicas f;

    private RecyclerViewAdapter_medidas reva;
    private Principal ma;

    private int nuevaExistencia;
    private String idMedida;
    private boolean firsTime = true;
    private ArrayList<ArrayList<String>> asd;

    public Medidas(ArrayList<ArrayList<String>> asd_) {
        this.asd = asd_;
    }

    private void inicializar() {
        ma = (Principal) getActivity();
        reva = new RecyclerViewAdapter_medidas(asd);

        f = new FuncionesUnicas(getContext());
    }

    private void eventos() {
        reva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (f.isVendedorRegistrado())
                    generarAlertDialog(v);
            }
        });
    }

    private void volleyEvents() {
        interfaceVolleyResult = new InterfaceVolleyResult() {
            @Override
            public void succes(String result, String tipo) {
                if (!result.contains("Error")) {

                    // esto solo es para que se vea reflejado en la aplicacion
                    editarArraylist();

                    f.mostrarToast(result);
                }

                else f.mostrarToast(result);
            }

            @Override
            public void error(String error) {
                f.mostrarToast("Puede que el servidor este caido o usted no tenga conexion a internet");
            }
        };

        volleyMethods = new VolleyMethods(getContext(), interfaceVolleyResult);
    }

    private void generarAlertDialog(View v) {

        // obtengo datos actuales
        final TextView exist = v.findViewById(R.id.text_existencia);
        final String cantidad = exist.getText().toString();

        // primero compruebo si hay productos
        if (Integer.parseInt(cantidad) > 0) {

            // idMedida
            idMedida = v.findViewById(R.id.text_medida).getTag().toString();

            // ahora toda la generacion del alertDialog

            // declaro
            AlertDialog.Builder al = new AlertDialog.Builder(getContext());
            al.setCancelable(false);

            // genero view
            LayoutInflater inflater = getLayoutInflater();
            final View v_al = inflater.inflate(R.layout.m_alertdialog, null);

            // establesco datos
            TextView alModelo = v_al.findViewById(R.id.alModelo);
            alModelo.setText(ma.modelo.getText());

            TextView alColor = v_al.findViewById(R.id.alColor);
            alColor.setText(ma.list_colores.get(ma.tabLayout.getSelectedTabPosition()));

            TextView alPrecio = v_al.findViewById(R.id.alPrecio);
            alPrecio.setText(ma.precio.getText());

            TextView rvMedida = v.findViewById(R.id.text_medida);
            TextView alMedida = v_al.findViewById(R.id.alMedida);
            alMedida.setText(rvMedida.getText());

            final NumberPicker np = v_al.findViewById(R.id.numberPicker);
            np.setMinValue(1);
            np.setMaxValue(Integer.parseInt(cantidad));
            np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // para que no se pueda escribir

            // pongo view al alertDialog
            al.setView(v_al);
            final AlertDialog alertDialog = al.create();

            // lo muestro
            alertDialog.show();

            // evento boton aceptar
            v_al.findViewById(R.id.anadir).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // obtengo cuantosComprar
                    int cuantosComprar = np.getValue();

                    // obtengo la nuevaExistencia
                    // cantidad actual menos cuantos compra
                    nuevaExistencia = Integer.parseInt(cantidad) - cuantosComprar;

                    // ejecuto webService
                    volleyMethods.mandarDatosWebService("idUsuario", f.getIdVendedor());
                    volleyMethods.mandarDatosWebService("idMedida", idMedida);
                    volleyMethods.mandarDatosWebService("cantidad", Integer.toString(cuantosComprar));

                    volleyMethods.hacerRequest("insertCarrito.php");

                    // cierro alertDialog
                    alertDialog.dismiss();
                }
            });

            // evento boton cancelar
            v_al.findViewById(R.id.cancelar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }

        else f.mostrarToast("No hay productos en existencia");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medidas, container, false);

        if (firsTime) {
            inicializar();
            eventos();
            volleyEvents();

            firsTime = false;
        }

        GridLayoutManager glm = new GridLayoutManager(getContext(), 1);
        RecyclerView rv = view.findViewById(R.id.recyclerViewMedidas);
        rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        rv.setLayoutManager(glm);
        rv.setAdapter(reva);

        return view;
    }

    private void editarArraylist() {
        for (int i = 0; i < asd.size(); i++)
            if (asd.get(i).indexOf(idMedida) >= 0) {
                asd.get(i).set(2, Integer.toString(nuevaExistencia));

                reva.update(asd);
                reva.notifyDataSetChanged();
                break;
            }
    }
}