package com.example.hypechat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class AgregarUsuarioCanal extends Fragment {

    private ValidadorDeCampos validador;
    private Button agregarUsers;
    private Button finalizar;
    private ProgressDialog progressDialog;
    private String URL_AGREGAR_USUARIOS_CANAL = "https://secure-plateau-18239.herokuapp.com/channel/users";
    private String URL_INFO_ORG = "https://secure-plateau-18239.herokuapp.com/organization/";
    private String token;
    private String id, nombre;
    private RecyclerView lista_miembros;
    private JSONArray members;
    private AdapterMiembrosCanal adaptador_para_usuarios_canal;
    private List<String> currentSelectedItems;



    public AgregarUsuarioCanal(String id, String canal_nombre) {
        this.token = Usuario.getInstancia().getToken();
        this.id = id;
        this.nombre = canal_nombre;
    }

    //metodo que se ejecuta cuando tocamos algun tarjeta de la recycleview
    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            //if(permiso_editar) confirma_eliminar_usuario(adaptador_para_usuarios.obtenerItemPorPosicion(position));

            Toast.makeText(getContext(), "TOCASTE el usuario: " + position, Toast.LENGTH_SHORT).show();

        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.organizaciones,container,false);
        View view = inflater.inflate(R.layout.activity_agregar_usuarios_canal,container,false);

        validador = new ValidadorDeCampos();

        finalizar = (Button)view.findViewById(R.id.button_finalizar_crear_canal);
        currentSelectedItems = new ArrayList<>();
        lista_miembros = (RecyclerView) view.findViewById(R.id.lista_organizacion_usuarios_a_agregar);

        adaptador_para_usuarios_canal = new AdapterMiembrosCanal(getContext());
        LinearLayoutManager l = new LinearLayoutManager(getContext());
        lista_miembros.setLayoutManager(l);
        lista_miembros.setAdapter(adaptador_para_usuarios_canal);
        adaptador_para_usuarios_canal.setOnItemClickListener(new AdapterMiembrosCanal.OnItemCheckListener() {
            @Override
            public void onItemCheck(String item) {
                currentSelectedItems.add(item);
            }

            @Override
            public void onItemUncheck(String item) {
                currentSelectedItems.remove(item);
            }
        });


        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Finaliza la actividad de agregar usuarios al canal.");
                agregarUsuarios_al_Canal();


            }
        });
        cargarMiembros();
        return view;

    }



    private void agregarUsuarios_al_Canal(){

        progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Actualizando los usuarios del canal...",true);

        ArrayList<String> usuarios_a_agregar =  adaptador_para_usuarios_canal.getChequeados();
        JSONArray array = new JSONArray();
        for(int i=0;i<usuarios_a_agregar.size();i++){
            array.put(usuarios_a_agregar.get(i));
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("token",this.token );
            requestBody.put("id", this.id);
            requestBody.put("name",this.nombre);
            requestBody.put("mo_email", Usuario.getInstancia().getEmail());
            requestBody.put("emails",array);

        }
        catch(JSONException except){
            Toast.makeText(getActivity(), except.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Log.i("INFO", "Agrego los usuarios al canal en el server "+currentSelectedItems.toString());

        Log.i("INFO", "Request body: "+requestBody.toString());
        Log.i("INFO", "Json Request , check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL_AGREGAR_USUARIOS_CANAL, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        System.out.println(response);
                        Log.i("INFO", "Actualizaste los usuarios del canal: "+nombre);
                        irAOrganizacion();

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode){
                            case (400):
                                Toast.makeText(getActivity(),"El usuario moderador no existe", Toast.LENGTH_LONG).show();
                                break;
                            case (402):
                                Toast.makeText(getActivity(),"No es privado el canal o no existe", Toast.LENGTH_LONG).show();
                                break;
                            case (404):
                                Toast.makeText(getActivity(),"No existe una organizacion con ese id", Toast.LENGTH_LONG).show();
                                break;
                            case (405):
                                Toast.makeText(getActivity(),"No tiene permisos para agregar al canal", Toast.LENGTH_LONG).show();
                                break;
                            case (500):
                                Toast.makeText(getActivity(),"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    private void irAOrganizacion() {
        Log.i("INFO","Se agregaron los usuarios al nuevo canal privado");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new OrganizacionFragment(this.id));
        //Esta es la linea clave para que vuelva al fragmento anterior!
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        //Linea clave para que el fragmento termine de ponerse si o si en la activity y poder editarla!
        fragmentManager.executePendingTransactions();

    }

    public void cargarMiembros(){
        Log.i("INFO", "Obteniendo miembros de la organizacion");
        this.progressDialog = ProgressDialog.show(
                getActivity(),"Hypechat","Obteniendo miembros de la organizacion...",true);

        //Preparo Body del POST

        String URL = URL_INFO_ORG + this.token+ "/" + this.id;

        Log.i("INFO", "Json Request get info organization, check http status codes");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        System.out.println(response);
                        actualizar_lista_members(response);

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        progressDialog.dismiss();
                        switch (error.networkResponse.statusCode){
                            case (400):
                                break;
                            //Toast.makeText(LoginActivity.this,"Usuario o Contraseña Invalidos!", Toast.LENGTH_LONG).show();
                            case (500):
                                Toast.makeText(getActivity(),"No fue posible conectarse al servidor, por favor intente de nuevo mas tarde", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });

        //Agrego la request a la cola para que se conecte con el server!
        HttpConexionSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void actualizar_lista_members(JSONObject res) {


        Log.i("INFO", "Actualizo lista de miembros");
        try {
            Log.i("INFO","Actualizo los miembros del listado para mostrar");
            JSONObject orga = res.getJSONObject("organization");
            members = orga.getJSONArray("members");
            adaptador_para_usuarios_canal.vaciar_lista();

            for(int i=0;i< members.length();i++){
                String email = members.getString(i);
                adaptador_para_usuarios_canal.agregarMiembro(email);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
