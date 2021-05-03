package com.example.octopus_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.os.FileUtils.copy;

public class CambiarVendedor extends AppCompatActivity {

    private InterfaceVolleyResult interfaceVolleyResult;
    private VolleyMethods volleyMethods;
    private FuncionesUnicas f;

    private ImageView imgVendedor;
    private TextView tv_usernameVendedor;
    private TextView tv_idVendedor;
    private TextView tv_nombreVendedor;

    private TextInputLayout ed_nuevoUser;
    private TextInputLayout ed_nuevoPassword;

    private void inicializar() {

        Toolbar toolbar =
                findViewById(R.id.cambiarVendedorToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        f = new FuncionesUnicas(this);

        imgVendedor = findViewById(R.id.imgVendedor);
        tv_usernameVendedor = findViewById(R.id.tv_usernameVendedor);
        tv_idVendedor = findViewById(R.id.tv_idVendedor);
        tv_nombreVendedor = findViewById(R.id.tv_nombreVendedor);

        setDatosVendedor();

        ed_nuevoUser = findViewById(R.id.ed_nuevoUser);
        ed_nuevoPassword = findViewById(R.id.ed_nuevoPassword);
    }

    private void eventos() {
        ed_nuevoPassword.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO)
                    buscarVendedor();

                return false;
            }
        });
    }

    private void volleyEvents() {
        interfaceVolleyResult = new InterfaceVolleyResult() {
            @Override
            public void succes(String result, String tipo) {
                try {
                    guardarVendedor(new JSONObject(result));
                } catch (Exception e) {

                    if (result.equals("Sesion cerrada")) {
                        f.borrarVendedor();
                        setDatosVendedor();
                    }

                    f.mostrarToast(result);
                }
            }

            @Override
            public void error(String error) {
                f.mostrarToast("Puede que el servidor este caido o usted no tenga conexion a internet");
            }
        };

        volleyMethods = new VolleyMethods(this, interfaceVolleyResult);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cambiar_vendedor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.cerrar:
                volleyMethods.mandarDatosWebService("idVendedor", f.getIdVendedor());
                volleyMethods.hacerRequest("deleteVendedor.php");

                f.cerrarKeyboard();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_vendedor);

        inicializar();
        eventos();
        volleyEvents();
    }

    private void guardarVendedor(JSONObject obj) {
        try {
            String nuevaImagen = volleyMethods.url + obj.getString("imagen_u").substring(2);
            String nuevaId = obj.getString("id_u");
            String nuevoNombre = obj.getString("nombre_u");
            String nuevoUsuario = obj.getString("usuario_u");

            f.setVendedor(nuevaImagen, nuevoNombre, nuevoUsuario, nuevaId);

            setDatosVendedor();

            ed_nuevoPassword.getEditText().setText("");
            ed_nuevoUser.getEditText().setText("");

        } catch (Exception e) {
            f.mostrarToast("Error inesperado");
        }
    }

    private void buscarVendedor() {
        ed_nuevoPassword.clearFocus();
        f.cerrarKeyboard();

        volleyMethods.mandarDatosWebService("idVendedor", ed_nuevoUser.getEditText().getText().toString());
        volleyMethods.mandarDatosWebService("password", ed_nuevoPassword.getEditText().getText().toString());
        volleyMethods.mandarDatosWebService("nombreD", f.getDetalles());
        volleyMethods.mandarDatosWebService("macD", f.getMacAddres());

        volleyMethods.hacerRequest("selectVendedor.php");
    }

    public void btn_setVendedor(View v) {
        buscarVendedor();
    }

    public void btnInfo(View v) {
        f.mostrarToast("Solo un vendedor por dispositivo");
    }

    private void setDatosVendedor() {
        tv_usernameVendedor.setText(f.getUsernameVendedor());
        tv_idVendedor.setText(f.getIdVendedor());
        tv_nombreVendedor.setText(f.getNombreVendedor());

        if (!f.getIdVendedor().equals(""))
            // se instalo libreria porque no se guardan las img en la DB
            Picasso.get().load(f.getImagenVendedor()).into(imgVendedor);

        else imgVendedor.setImageResource(R.drawable.vector_team);

        findViewById(R.id.vendedorScroll).scrollTo(0, 0);
    }


}
