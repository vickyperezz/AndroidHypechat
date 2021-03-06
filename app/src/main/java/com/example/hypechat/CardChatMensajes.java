package com.example.hypechat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CardChatMensajes extends RecyclerView.ViewHolder {

    private TextView nickname;
    private TextView mensaje_chat;
    private TextView hora;
    private ImageView foto_perfil;
    private ImageView imagen_mensaje;




    public CardChatMensajes(@NonNull View itemView) {
        super(itemView);
        nickname = (TextView) itemView.findViewById(R.id.chat_card_nickname);
        mensaje_chat = (TextView) itemView.findViewById(R.id.chat_card_mensaje);
        hora = (TextView) itemView.findViewById(R.id.chat_card_timestamp);
        foto_perfil = (ImageView) itemView.findViewById(R.id.chat_card_foto_perfil);
        imagen_mensaje = (ImageView) itemView.findViewById(R.id.imagen_mensaje);
    }


    public TextView getNickname() {
        return nickname;
    }

    public void setNickname(TextView nickname) {
        this.nickname = nickname;
    }

    public TextView getMensaje_chat() {
        return mensaje_chat;
    }

    public void setMensaje_chat(TextView mensaje_chat) {
        this.mensaje_chat = mensaje_chat;
    }

    public TextView getHora() {
        return hora;
    }

    public void setHora(TextView hora) {
        this.hora = hora;
    }

    public ImageView getFoto_perfil() {
        return foto_perfil;
    }

    public void setFoto_perfil(ImageView foto_perfil) {
        this.foto_perfil = foto_perfil;
    }

    public ImageView getImagen_mensaje() {
        return imagen_mensaje;
    }

    public void setImagen_mensaje(ImageView imagen_mensaje) {
        this.imagen_mensaje = imagen_mensaje;
    }
}
