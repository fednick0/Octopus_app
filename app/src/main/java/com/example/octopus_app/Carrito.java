package com.example.octopus_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Carrito extends AppCompatActivity {

    private InterfaceVolleyResult interfaceVolleyResult;
    private VolleyMethods volleyMethods;
    private FuncionesUnicas f;

    private String idReservaSend, upDel;
    private String medidaSend, modeloSend, colorSend;

    private ProgressBar progresBar;
    private TextView totalTotal;

    private int eliminarSend, position;
    private boolean firstTime, escaneo;

    private String idUsuario;
    private String noPedido;
    private String tipo;

    private RecyclerView rv;
    private RecyclerViewAdapter_carrito reva;
    private ArrayList<ArrayList<String>> datosParaRVA;

    private void inicializar() {
        Toolbar toolbar = findViewById(R.id.carritoToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        f = new FuncionesUnicas(this);

        firstTime = true;

        rv = findViewById(R.id.recyclerViewCarrito);
        totalTotal = findViewById(R.id.totalTotal);
        progresBar = findViewById(R.id.progressBar);

        escaneo = false;

        idUsuario = f.getIdVendedor();
        noPedido = "0";
        tipo = "0";
    }

    private void volleyEvents() {
        interfaceVolleyResult = new InterfaceVolleyResult() {
            @Override
            public void succes(String result, String tipo) {
                if (tipo.equals("selectCarrito.php")) {
                    if (!result.equals("null"))
                        mandarDatos(result);
                    else f.mostrarToast("Carrito vacio");

                    progresBar.setVisibility(View.GONE);
                }

                else {
                    if (!result.contains("Error")) {
                        reva.restarCarrito(eliminarSend, position);
                        setTotalTotal();
                    }

                    f.mostrarToast(result);
                }
            }

            @Override
            public void error(String error) {
                f.mostrarToast("Puede que el servidor este caido o usted no tenga conexion a internet");
                progresBar.setVisibility(View.GONE);
            }
        };

        volleyMethods = new VolleyMethods(this, interfaceVolleyResult);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_carrito, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

            case R.id.cambiar_vendedor:
                startActivityForResult(new Intent(this, CambiarVendedor.class), 3333);
                break;

            case R.id.recargar:
                if (f.isVendedorRegistrado()) {
                    progresBar.setVisibility(ProgressBar.VISIBLE);
                    selectCarrito();
                }
                break;

            case R.id.escaner:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED)
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 999);

                else
                    startActivityForResult( new Intent(this, Escaner.class), 2222);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 999 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            startActivityForResult( new Intent(this, Escaner.class), 1111);

        else f.mostrarToast("No puede escanear sin permisos de camara");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {

                case 2222: // escaner
                    try {
                        escaneo = true;

                        String[] dato = data.getStringExtra("valor").split("%");

                        idUsuario = dato[0];
                        noPedido = dato[1];
                        tipo = dato[2];

                        selectCarrito();
                    }
                    catch (Exception e) {
                        f.mostrarToast("Esta funcion es para escanear ticket hechos en la pagina web");
                    }
                    break;
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        inicializar();
        volleyEvents();

        if (!idUsuario.equals("")) selectCarrito();
        else progresBar.setVisibility(ProgressBar.GONE);
    }

    private void selectCarrito() {
        volleyMethods.mandarDatosWebService("idUsuario", idUsuario);
        volleyMethods.mandarDatosWebService("noPedido", noPedido);
        volleyMethods.mandarDatosWebService("tipo", tipo);

        volleyMethods.hacerRequest("selectCarrito.php");
    }

    private void deleteCarrito() {
        volleyMethods.mandarDatosWebService("idReserva", idReservaSend);
        volleyMethods.mandarDatosWebService("eliminar", Integer.toString(eliminarSend));
        volleyMethods.mandarDatosWebService("modelo", modeloSend);
        volleyMethods.mandarDatosWebService("color", colorSend);
        volleyMethods.mandarDatosWebService("medida", medidaSend);
        volleyMethods.mandarDatosWebService("upDel", upDel);

        volleyMethods.hacerRequest("deleteCarrito.php");
    }

    private void generarAlertDialog(View v) {
        // declaro
        AlertDialog.Builder al = new AlertDialog.Builder(this);
        al.setCancelable(false);

        // genero view
        LayoutInflater inflater = getLayoutInflater();
        final View v_al = inflater.inflate(R.layout.m_alertdialog, null);

        // declaro cositas desde el item del carrito
        final TextView cModelo = v.findViewById(R.id.cModelo);
        final TextView cNumero = v.findViewById(R.id.cNumero);
        TextView cPrecio = v.findViewById(R.id.cPrecio);
        final TextView cColor = v.findViewById(R.id.cColor);
        final TextView cMedida = v.findViewById(R.id.cMedida);
        TextView cCantidad = v.findViewById(R.id.cCantidad);

        // establesco algunos valores que deben cambiar en el alertDialog
        Button cButton = v_al.findViewById(R.id.anadir);
        TextView cTitulo = v_al.findViewById(R.id.tvTitulo_adC);
        TextView cSubTitulo = v_al.findViewById(R.id.tv_adC);

        cButton.setText("Eliminar");
        cTitulo.setText("Eliminar del carrito");
        cSubTitulo.setText("Cuantos desea eliminar?");

        // declaro taxtview de alertDialog
        TextView alModelo = v_al.findViewById(R.id.alModelo);
        TextView alColor = v_al.findViewById(R.id.alColor);
        TextView alPrecio = v_al.findViewById(R.id.alPrecio);
        TextView alMedida = v_al.findViewById(R.id.alMedida);

        // establesco datos
        alModelo.setText(cModelo.getText());
        alColor.setText(cColor.getText());
        alPrecio.setText(cPrecio.getText());
        alMedida.setText(cMedida.getText());

        final int maxValue = Integer.parseInt(cCantidad.getText().toString());
        final NumberPicker np = v_al.findViewById(R.id.numberPicker);
        np.setMinValue(1);
        np.setMaxValue(maxValue);
        np.setValue(maxValue);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // para que no se pueda escribir

        // pongo view al alertDialog
        al.setView(v_al);
        final AlertDialog alertDialog = al.create();

        // lo muestro
        alertDialog.show();

        // evento boton aceptar
        cButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cPosition = cNumero.getText().toString()
                        .substring(0, cNumero.getText().length() - 3);

                position = Integer.parseInt(cPosition) - 1;

                // aqui los datos que voy a mandar al webService
                idReservaSend = cModelo.getTag().toString();
                eliminarSend = np.getValue();
                modeloSend = cModelo.getText().toString();
                colorSend = cColor.getText().toString();
                medidaSend = cMedida.getText().toString();

                // aqui esta el valor de cCantidad
                // solo verifico que no sea 0
                if (maxValue - eliminarSend > 0) upDel = "update";
                else upDel = "delete";

                // ejecuto webService
                deleteCarrito();
                alertDialog.dismiss(); // cierro alertDialog
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

    private void mandarDatos(String response) {
        try {
            datosParaRVA = new ArrayList<>();

            JSONArray datosCarrito = new JSONArray(response);

            for (int i = 0; i < datosCarrito.length(); i++) {
                ArrayList<String> temp = new ArrayList<>();

                temp.add(datosCarrito.getJSONObject(i).getString("id_r"));          // 0
                temp.add(datosCarrito.getJSONObject(i).getString("imagen_p"));      // 1
                temp.add(datosCarrito.getJSONObject(i).getString("marca_p"));       // 2
                temp.add(datosCarrito.getJSONObject(i).getString("modelo_p"));      // 3

                temp.add(datosCarrito.getJSONObject(i).getString("ubicacion_p"));   // 4
                temp.add(datosCarrito.getJSONObject(i).getString("material_p"));    // 5
                temp.add(datosCarrito.getJSONObject(i).getString("tipo_p"));        // 6

                temp.add(datosCarrito.getJSONObject(i).getString("medida_r"));      // 7
                temp.add(datosCarrito.getJSONObject(i).getString("color_r"));       // 8
                temp.add(datosCarrito.getJSONObject(i).getString("precioventa_p")); // 9

                temp.add(datosCarrito.getJSONObject(i).getString("cantidad_r"));    // 10
                temp.add(datosCarrito.getJSONObject(i).getString("total_r"));       // 11

                datosParaRVA.add(temp);
            }

            if (firstTime) {
                reva = new RecyclerViewAdapter_carrito(datosParaRVA);

                firstTime = false;
            }

            else reva.recargarCarrito(datosParaRVA);

            // esto no se si se debe ejecutar solo una vez
            reva.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!escaneo) {
                        f.vibrar();
                        generarAlertDialog(v);
                    }
                    return false;
                }
            });

            GridLayoutManager glm = new GridLayoutManager(this, 1);
            rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            rv.setLayoutManager(glm);
            rv.setAdapter(reva);

            setTotalTotal();
        } catch (Exception e) {
            f.mostrarToast("Error mientras se intentaban cargar datos");
        }
    }

    private void setTotalTotal() {
        totalTotal.setText("Mex" +
                NumberFormat.getCurrencyInstance(Locale.US).format(
                        reva.gettotalTotal()
                ));
    }

}
