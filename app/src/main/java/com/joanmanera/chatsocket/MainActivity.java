package com.joanmanera.chatsocket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
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

    private final String IP_SERVIDOR = "192.168.1.71";
    private EditText etNick, etMensaje;
    private Button bEnviar, bEnviarMensajePrivado;
    private TextView tvDestino;
    private RecyclerView recyclerView;
    private AdapterMensaje adapterMensaje;
    private ArrayList<Mensaje> mensajes;
    private String ipDestino;
    private boolean isPrivate;
    private MensajePrivadoDialogo dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Crea una conexión con el servidor para decirle que se acaba de conectar.
        Start tarea1 = new Start();
        tarea1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        bEnviarMensajePrivado = findViewById(R.id.bMensajePrivado);
        dialogo = new MensajePrivadoDialogo(this);
        bEnviarMensajePrivado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.show(getSupportFragmentManager(), "dialog");
            }
        });

        tvDestino = findViewById(R.id.tvDestinatario);
        etNick = findViewById(R.id.etNick);
        etMensaje = findViewById(R.id.etMensaje);
        bEnviar = findViewById(R.id.bEnviar);

        // Controla el evento del click del boton enviar.
        bEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cliente tarea = new Cliente();
                Mensaje m;

                if(isPrivate){
                    // Si el mensaje es privado construye el mensaje con la IP del destino.
                    m = new Mensaje(etNick.getText().toString(), ipDestino, etMensaje.getText().toString(), isPrivate);
                    isPrivate = false;
                    tvDestino.setText("All");
                } else {
                    // Si no es privado construye el mesaje con un destinatario en blanco.
                    // El servidor entiende que es para todos.
                    m = new Mensaje(etNick.getText().toString(), "todos", etMensaje.getText().toString(), isPrivate);
                }

                // Se crea la conexión al servidor pasandole el mensaje.
                tarea.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, m);
            }
        });

        // Se crea una conexión al servidor donde se recibiran todos los mensajes.
        RecibirMensaje recibirMensaje = new RecibirMensaje();
        recibirMensaje.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        // Se muestran los mensajes
        mensajes = new ArrayList<>();
        adapterMensaje = new AdapterMensaje(mensajes);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapterMensaje);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        setTitle(Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress()));
    }

    @Override
    public void onIpSelected(String ip) {
        ipDestino = ip;
        isPrivate = true;
        tvDestino.setText(ip);
    }

    private class Cliente extends AsyncTask<Mensaje, Mensaje, Boolean>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(Mensaje... mensajes) {
            try {
                Socket socket = new Socket( IP_SERVIDOR, 9990);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(mensajes[0]);

                publishProgress(mensajes[0]);

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

            if (values[0].isPrivate()) {
                adapterMensaje.addMensaje(values[0]);
            }
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

    private class Start extends AsyncTask<Void, Void, Boolean>{


        @Override
        protected Boolean doInBackground(Void... mensajes) {
            try {
                Socket socket = new Socket(IP_SERVIDOR, 9990);

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}
