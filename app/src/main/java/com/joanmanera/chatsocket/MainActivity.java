package com.joanmanera.chatsocket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MensajePrivadoDialogo.IIPListener {

    private EditText etNick, etMensaje;
    private Button bEnviar, bEnviarMensajePrivado;
    private RecyclerView recyclerView;
    private AdapterMensaje adapterMensaje;
    private ArrayList<Mensaje> mensajes;
    private String ipDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bEnviarMensajePrivado = findViewById(R.id.bMensajePrivado);
        bEnviarMensajePrivado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MensajePrivadoDialogo dialogo = new MensajePrivadoDialogo(MainActivity.this);
            }
        });

        etNick = findViewById(R.id.etNick);
        //etIP = findViewById(R.id.etIP);
        etMensaje = findViewById(R.id.etMensaje);
        bEnviar = findViewById(R.id.bEnviar);
        bEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cliente tarea = new Cliente();
                tarea.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, etNick.getText().toString(), ipDestino, etMensaje.getText().toString());
                //tarea.execute(etNick.getText().toString(), etIP.getText().toString(), etMensaje.getText().toString());
            }
        });

        RecibirMensaje recibirMensaje = new RecibirMensaje();
        recibirMensaje.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mensajes = new ArrayList<>();
        adapterMensaje = new AdapterMensaje(mensajes);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapterMensaje);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

    }

    @Override
    public void onIpSelected(String ip) {
        ipDestino = ip;
    }

    private class Cliente extends AsyncTask<String, Mensaje, Boolean>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                Socket socket = new Socket("192.168.21.210", 9990);
                //Mensaje mensaje = new Mensaje(etNick.getText().toString(), etIP.getText().toString(), etMensaje.getText().toString());
                Mensaje mensaje = new Mensaje(strings[0], strings[1], strings[2]);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(mensaje);

                publishProgress(mensaje);

                objectOutputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Mensaje... values) {
            super.onProgressUpdate(values);

            adapterMensaje.addMensaje(values[0]);
        }
    }

    private class RecibirMensaje extends AsyncTask<Void, Mensaje, Boolean>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                ServerSocket serverSocket = new ServerSocket(9090);
                Socket socket;

                while (true){
                    socket = serverSocket.accept();
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    Mensaje mensaje = (Mensaje) objectInputStream.readObject();

                    publishProgress(mensaje);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Mensaje... values) {
            super.onProgressUpdate(values);
            adapterMensaje.addMensaje(values[0]);
        }
    }
}
