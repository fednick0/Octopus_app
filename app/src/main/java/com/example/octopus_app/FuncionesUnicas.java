package com.example.octopus_app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Vibrator;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class FuncionesUnicas {

    private Context context;
    private Toast toast;
    private Vibrator vibrator;
    private SharedPreferences sharedPreferences;

    FuncionesUnicas(Context context) {
        this.context = context;

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
    }

    public void borrarVendedor() {
        sharedPreferences.edit().clear().commit();
    }

    public boolean setVendedor(String imagen, String nombre, String username, String id) {
        SharedPreferences.Editor editor =
                sharedPreferences.edit();

        editor.putString("imagen", imagen);
        editor.putString("nombre", nombre);
        editor.putString("username", username);
        editor.putString("id", id);
        return editor.commit();
    }

    public String getImagenVendedor() {
        return sharedPreferences.getString("imagen", "");
    }

    public String getNombreVendedor() {
        return sharedPreferences.getString("nombre", "");
    }

    public String getUsernameVendedor() {
        return sharedPreferences.getString("username", "");
    }

    public String getIdVendedor() {
        return sharedPreferences.getString("id", "");
    }

    public boolean isVendedorRegistrado() {
        if (getIdVendedor().equals("")) {

            new AlertDialog.Builder(context)
                    .setTitle("No hay vendedor registrado")
                    .setMessage("Se le redireccionara a la ventana de vendedores")
                    .setCancelable(false)
                    .setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            context.startActivity(
                                    new Intent(context, CambiarVendedor.class));
                        }
                    })
                    .show();

            return false;
        }

        return true;
    }

    public void mostrarToast(String text) {
        if (toast != null)
            toast.cancel();

        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void vibrar() {
        if (vibrator != null)
            vibrator.cancel();

        vibrator.vibrate(100);
    }

    public void cerrarKeyboard() {
        View view = ((Activity) context).getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String getMacAddres() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());

            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null)
                    return "";

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes)
                    res1.append(Integer.toHexString(b & 0xFF) + ":");

                if (res1.length() > 0)
                    res1.deleteCharAt(res1.length() - 1);

                return res1.toString();
            }
        }
        catch (Exception e) {

        }

        return "02:00:00:00:00:00";
    }

    public String getDetalles() {
        String detalles = Build.MANUFACTURER +
                " " + Build.MODEL +
                " API" + Build.VERSION.SDK_INT;
        return detalles;
    }
}
