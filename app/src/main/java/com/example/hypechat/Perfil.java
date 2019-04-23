package com.example.hypechat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Perfil extends Fragment {

    private View vista;

    private EditText nombre_perfil, apodo_perfil, email_perfil, contraseña_perfil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.perfil,container,false);

        Button modificar = (Button)view.findViewById(R.id.boton_modificar_perfil);
        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Apretaste el boton");
            }
        });


        this.nombre_perfil = (EditText) view.findViewById(R.id.nombre_perfil);
        this.apodo_perfil = (EditText) view.findViewById(R.id.apodo_perfil);
        this.email_perfil = (EditText) view.findViewById(R.id.email_perfil);
        this.contraseña_perfil = (EditText) view.findViewById(R.id.contraseña_perfil);


        return view;

    }

    public void completarDatosPerfil(String nombre, String apodo, String email, String contraseña){
        this.setNombrePerfil(nombre);
        this.setApodoPerfil(apodo);
        this.setEmailPerfil(email);
        this.setContraseñaPerfil(contraseña);
    }

    private void setContraseñaPerfil(String contraseña) {
        this.contraseña_perfil.setText(contraseña);
    }

    private void setEmailPerfil(String email) {
        this.email_perfil.setText(email);
    }

    private void setNombrePerfil(String nombre){
        this.nombre_perfil.setText(nombre);
    }

    private void setApodoPerfil(String apodo){
        this.apodo_perfil.setText(apodo);
    }


}
