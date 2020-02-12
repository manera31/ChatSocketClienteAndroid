package com.joanmanera.chatsocket;

import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {

    private EditText etNick, etIP, etMensaje;
    private Button bEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNick = findViewById(R.id.etNick);
        etIP = findViewById(R.id.etIP);
        etMensaje = findViewById(R.id.etMensaje);
        bEnviar = findViewById(R.id.bEnviar);
        bEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cliente tarea = new Cliente(MainActivity.this);
                tarea.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, etNick.getText().toString(), etIP.getText().toString(), etMensaje.getText().toString());
                //tarea.execute(etNick.getText().toString(), etIP.getText().toString(), etMensaje.getText().toString());
            }
        });

        RecibirMensaje recibirMensaje = new RecibirMensaje(MainActivity.this);
        recibirMensaje.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private static class Cliente extends AsyncTask<String, String, Boolean>{

        private TextView tvTexto;


        public Cliente(MainActivity context){
            tvTexto = context.findViewById(R.id.tvTexto);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                Socket socket = new Socket("192.168.1.71", 9990);
                //Mensaje mensaje = new Mensaje(etNick.getText().toString(), etIP.getText().toString(), etMensaje.getText().toString());
                Mensaje mensaje = new Mensaje(strings[0], strings[1], strings[2]);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(mensaje);

                publishProgress(mensaje.getMensaje());

                objectOutputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            tvTexto.append( values[0] + "\n");
        }
    }

    private class RecibirMensaje extends AsyncTask<Void, String, Boolean>{

        private TextView tvTexto;

        public RecibirMensaje(MainActivity context) {
            tvTexto = context.findViewById(R.id.tvTexto);
        }

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

                    publishProgress(mensaje.getMensaje());
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            tvTexto.append( values[0] + "\n");
        }
    }
}
