package com.example.hypechat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CrearOrganizacion extends Fragment {

    private EditText textName;
    private ValidadorDeCampos validador;
    private ProgressDialog progress;
    private Button crearOrganizacion;
    private Button cancelar;
    private EditText nombre;
    private EditText id;
    private EditText psw;
    private String mensaje;
    private ProgressDialog progressDialog;
    private String URL_CHECK_ID = "https://secure-plateau-18239.herokuapp.com/idOrganizationValid/";
    private String URL_SEND_ORGANIZACION_DATA = "https://secure-plateau-18239.herokuapp.com/organization";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.organizaciones,container,false);
        View view = inflater.inflate(R.layout.activity_crear_organizacion,container,false);
        validador = new ValidadorDeCampos();
        crearOrganizacion = (Button)view.findViewById(R.id.button_crearOrg_siguiente);
        cancelar = (Button) view.findViewById(R.id.button_crearOrg_cancelar);
        nombre = (EditText) view.findViewById(R.id.name_organizacion);
        id = (EditText) view.findViewById(R.id.id_organizacion);
        psw = (EditText) view.findViewById(R.id.pass_Organizacion);


        crearOrganizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Apretaste para crear una organizacion");
                //Intent launchactivity = new Intent(getActivity(),CrearOrganizacion.class);
                //startActivity(launchactivity);
                if(validarCampos()) {
                    chequearIdDuplicado();
                }


            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Cancelar la creacion una organizacion");

                getFragmentManager().popBackStackImmediate();


            }
        });

        return view;

    }

    private boolean validarCampos() {
        String nombre_org = this.nombre.getText().toString();
        String id_org = this.id.getText().toString();
        String psw_org = this.psw.getText().toString();
        if(validador.isNotCampoVacio(nombre_org,getContext(),"Nombre") && validador.isNotCampoVacio(id_org,getContext(),"ID") && validador.isValidPassword(psw_org,getContext())&&validador.noContieneEspacios(id_org,getContext(),"id")){
            return true;

        }else{
            return false;
        }


    }

    private void chequearIdDuplicado() {

        Log.i("INFO", "Chequeo si el ID ya existe");

        Log.i("INFO", "Json Request , check http status codes");
        String URL = this.URL_CHECK_ID+this.id.getText().toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println(response);
                        crearOrganizacion_server();

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();

                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(getActivity(),"El ID ya existe, intente con otro.", Toast.LENGTH_LONG).show();
                            case (500):
                                 Toast.makeText(getActivity(),"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde\"", Toast.LENGTH_LONG).show();


                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void crearOrganizacion_server(){

        progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Creando la organizacion...",true);

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", this.nombre.getText().toString());
            requestBody.put("id", this.id.getText().toString());
            requestBody.put("email", Usuario.getInstancia().getEmail());
            requestBody.put("psw", this.psw.getText().toString());
        }
        catch(JSONException except){
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Log.i("INFO", "Envio la creacion de la organizacion al server");


        Log.i("INFO", "Json Request , check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_SEND_ORGANIZACION_DATA, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        System.out.println(response);

                        agregarUser();

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode){
                            case (500):
                                Toast.makeText(getActivity(),"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();
                            case (400):
                                Toast.makeText(getActivity(),"El ID ya existe, intente con otro.", Toast.LENGTH_LONG).show();
                            case (404):
                                Toast.makeText(getActivity(),"El usuario es invalido", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    private void agregarUser(){



        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new AgregarUsuarioOrganizacion());
        //Esta es la linea clave para que vuelva al fragmento anterior!
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
        fragmentManager.executePendingTransactions();

        //Me traigo el fragmento sabiendo que es el de perfil para cargarle la información
        AgregarUsuarioOrganizacion add_Usuario = (AgregarUsuarioOrganizacion) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        add_Usuario.completarOrganizacionID(this.id.getText().toString(),true,this.psw.getText().toString(),Usuario.getInstancia().getToken(),null);

    }




}
