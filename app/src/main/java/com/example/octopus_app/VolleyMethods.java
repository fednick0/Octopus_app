package com.example.octopus_app;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class VolleyMethods {

    private Context context; // necesita context para utilizar en diferentes class

    // hay un evento y necesito hacer un callback, se hacer con un interface
    private InterfaceVolleyResult interfaceVolleyResult;

    // direccion del servidor
    public final static String url = "http://octopusthefinalsolution.com";

    // funciones principales
    private StringRequest stringRequest;
    private RequestQueue requestQueue;

    // datos que se envian al webService
    private Map<String, String> params;

    // constructor, recibe context de la clase donde se genera
    // y la interface donde hace el callback
    VolleyMethods(Context context, InterfaceVolleyResult interfaceVolleyResult) {
        this.context = context;
        this.interfaceVolleyResult = interfaceVolleyResult;

        requestQueue = Volley.newRequestQueue(context);
        params = new HashMap<>();
    }

    // metodo para guardar datos que se van a enviar al webService
    public void mandarDatosWebService(String nombre, String valor) {
        params.put(nombre, valor);
    }

    // genera el evento y hace la request al webService
    public void hacerRequest(final String webService) {
        // justamente esto es la generacion del evento
        stringRequest = new StringRequest(Request.Method.POST,
                url + "/webService/" + webService, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) { // si hay respuesta
                params.clear(); // limpio
                interfaceVolleyResult.succes(response, webService); // mando datos
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { // no hay respuesta
                params.clear(); // limpio tambien
                interfaceVolleyResult.error(error.toString()); // envio el error
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                return params;
            } // se envia datos al server
        };

        // aqui se ejecuta el evento
        requestQueue.add(stringRequest);
    }
}
