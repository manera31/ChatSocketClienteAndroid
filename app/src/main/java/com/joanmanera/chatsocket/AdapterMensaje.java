package com.joanmanera.chatsocket;

import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterMensaje extends RecyclerView.Adapter<AdapterMensaje.MensajeHolder> {

    private ArrayList<Mensaje> mensajes;

    public AdapterMensaje(ArrayList<Mensaje> mensajes){
        this.mensajes = mensajes;
    }

    @NonNull
    @Override
    public MensajeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, parent, false);
        return new MensajeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeHolder holder, int position) {
        holder.bindMensaje(position);
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public void addMensaje(Mensaje m){
        mensajes.add(m);
        notifyDataSetChanged();
    }

    public class MensajeHolder extends RecyclerView.ViewHolder {

        private TextView tvDe, tvMensaje;

        public MensajeHolder(@NonNull View itemView) {
            super(itemView);
            tvDe = itemView.findViewById(R.id.tvDe);
            tvMensaje = itemView.findViewById(R.id.tvMensaje);
        }

        public void bindMensaje(int position){
            Mensaje mensaje = mensajes.get(position);

            if (mensaje.isPrivate()){
                tvDe.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC), Typeface.BOLD_ITALIC);
                tvDe.setText(mensaje.getDe() + ": ");
                tvMensaje.setText(mensaje.getMensaje());
            } else {
                tvDe.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL), Typeface.NORMAL);
                tvDe.setText(mensaje.getDe() + ": ");
                tvMensaje.setText(mensaje.getMensaje());
            }

        }
    }

}
