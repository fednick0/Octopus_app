package com.example.octopus_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Principal extends AppCompatActivity {

    private InterfaceVolleyResult interfaceVolleyResult;
    private VolleyMethods volleyMethods;
    private FuncionesUnicas f;

    private ViewPagerAdapter_colores vpa;
    private TextInputLayout ed_codigo;
    private ImageView iv;
    private AppBarLayout appbar;
    private ViewPager viewPager;
    private NestedScrollView nsv;
    private Toolbar tb;

    private TextView marca, ubicacion, material, tipo;

    public TabLayout tabLayout;
    public TextView modelo, precio;
    public ArrayList<String> list_colores;

    private void inicializar() {
        appbar = findViewById(R.id.appbar);
        nsv = findViewById(R.id.nestedScroll);
        tb = findViewById(R.id.toolbar);

        setSupportActionBar(tb);

        iv = findViewById(R.id.imageView);
        ed_codigo = findViewById(R.id.codigo);

        marca = findViewById(R.id.marca);
        modelo = findViewById(R.id.modelo);
        ubicacion = findViewById(R.id.ubicacion);
        precio = findViewById(R.id.precio);
        material = findViewById(R.id.material);
        tipo = findViewById(R.id.tipo);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        vpa = new ViewPagerAdapter_colores(getSupportFragmentManager());
        viewPager.setAdapter(vpa);
        tabLayout.setupWithViewPager(viewPager);

        list_colores = new ArrayList<>();

        f = new FuncionesUnicas(this);
    }

    private void eventos() {
        ed_codigo.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                    realizarConsulta();

                return false;
            }
        });
    }

    private void volleyEvents() {
        interfaceVolleyResult = new InterfaceVolleyResult() {
            @Override
            public void succes(String result, String tipo) {
                if (!result.equals("null"))
                    algoritmoMostrar(result);

                else {
                    limpieza();
                    f.mostrarToast("No hay coincidencias");
                }
            }

            @Override
            public void error(String error) {
                limpieza();
                f.mostrarToast("Puede que el servidor este caido o usted no tenga conexion a internet");
            }
        };

        volleyMethods = new VolleyMethods(this, interfaceVolleyResult);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.carro:
                startActivity(new Intent(getApplicationContext(), Carrito.class));
                break;

            case R.id.recargar:
                ed_codigo.getEditText().setText(modelo.getText());
                realizarConsulta();
                break;

            case R.id.cambiar_vendedor:
                startActivity(
                        new Intent(this, CambiarVendedor.class));
                break;

            case R.id.limpiar:
                limpieza();
                appbar.setExpanded(true);
                ed_codigo.getEditText().setText("");
                break;

            case R.id.acerca:
                android.app.AlertDialog.Builder al = new AlertDialog.Builder(this);
                al.setView(getLayoutInflater().inflate(R.layout.a_alertdialog, null));
                al.show();
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
        if (requestCode == 1111 && resultCode == RESULT_OK) {
            ed_codigo.getEditText().setText(data.getStringExtra("valor"));
            realizarConsulta();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ed_codigo.clearFocus();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        inicializar();
        eventos();
        volleyEvents();

        limpieza();
    }

    private void realizarConsulta() {
        volleyMethods.mandarDatosWebService("id", ed_codigo.getEditText().getText().toString());
        volleyMethods.hacerRequest("selectProducto.php");
    }

    private void algoritmoMostrar(String response) {
        try {
            // resultado completo
            JSONArray completo_a = new JSONArray(response);

            // obtengo datos
            JSONObject basicos = completo_a.getJSONObject(0);

            // cargo la imagen
            byte[] decoString = Base64.decode(basicos.getString("imagen_p"), Base64.DEFAULT);
            Bitmap decoded = BitmapFactory.decodeByteArray(decoString, 0, decoString.length);
            iv.setImageBitmap(decoded);

            // introduzco datos basicos
            marca.setText(basicos.getString("marca_p"));
            modelo.setText(basicos.getString("modelo_p"));
            ubicacion.setText(basicos.getString("ubicacion_p"));
            material.setText(basicos.getString("material_p"));
            tipo.setText(basicos.getString("tipo_p"));

            // esto para el precio
            precio.setText("Mex" +
                    NumberFormat.getCurrencyInstance(Locale.US).format(
                            Double.parseDouble(
                                basicos.getString("precioventa_p")
                            )
                    ));

            // creo pestañas
            if (response.contains("color_c")) {

                // obtengo datos
                JSONArray conjuntos = completo_a.getJSONArray(1);

                // pongo color
                int c = getResources().getColor(R.color.colorClaro);
                tabLayout.setBackgroundColor(c);
                viewPager.setBackgroundColor(Color.WHITE);

                list_colores.clear();
                int conjuntos_len = conjuntos.length();
                vpa.removeFragment();

                // primer recorrido buscando colores diferente
                for (int i = 0; i < conjuntos_len; i++) {
                    String color = conjuntos.getJSONObject(i).getString("color_c");

                    // si el color aun no esta registrado
                    if (!list_colores.contains(color)) {
                        list_colores.add(color);

                        ArrayList<ArrayList<String>> datos_uncolor = new ArrayList<>();

                        // segundo recorrido buscando todas las Medidas y existencias de un solo color
                        for (int j = 0; j < conjuntos_len; j++) {

                            if (color.equals(conjuntos.getJSONObject(j).getString("color_c"))) {
                                ArrayList<String> me_ex = new ArrayList<>();

                                me_ex.add(conjuntos.getJSONObject(j).getString("id_m"));
                                me_ex.add(conjuntos.getJSONObject(j).getString("medida_m"));
                                me_ex.add(conjuntos.getJSONObject(j).getString("existencia_local"));

                                datos_uncolor.add(me_ex);
                            }
                        }

                        Medidas con_ = new Medidas(datos_uncolor);
                        vpa.addFragment(con_);
                    }
                }

                vpa.setPageTitle(list_colores);
                vpa.notifyDataSetChanged();
            } // creo pestañas

            else limpiezaFragment();

            // minimizo
            appbar.setExpanded(false);
            nsv.scrollTo(0,0);
            ed_codigo.clearFocus();
            f.cerrarKeyboard();

        } catch (Exception e) {
            limpieza();
            f.mostrarToast("Error mientras se intentaban cargar datos");
        }
    }

    /** podria cambiar, realmente no se */
    public void botonEscaner(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 999);

        else
            startActivityForResult( new Intent(this, Escaner.class), 1111);
    }

    private void limpieza() {
        marca.setText("");
        modelo.setText("");
        ubicacion.setText("");
        precio.setText("");
        material.setText("");
        tipo.setText("");

        iv.setImageResource(R.drawable.img_predef);

        nsv.scrollTo(0,0);

        limpiezaFragment();
    }

    private void limpiezaFragment() {
        tabLayout.setBackgroundColor(Color.TRANSPARENT);
        viewPager.setBackgroundColor(Color.TRANSPARENT);

        vpa.removeFragment();
        vpa.notifyDataSetChanged();
    }
}