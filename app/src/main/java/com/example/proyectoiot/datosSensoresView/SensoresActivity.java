package com.example.proyectoiot.datosSensoresView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoiot.IniciarSesionActivity;
import com.example.proyectoiot.R;
import com.example.proyectoiot.databinding.ActivityDatosSensoresBinding;
import com.example.proyectoiot.databinding.ActivityMainBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import java.util.ArrayList;
import java.util.List;

public class SensoresActivity extends AppCompatActivity {
    FirebaseFirestore db;
    boolean flujoAgua;
    double cantidadAgua;
    double temperatura;
    double calidadAire;
    double humedadAire;
    private @NonNull ActivityDatosSensoresBinding binding; //si no está
    public static MyAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Mis Sensores");
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_sensores);

        // Crear aqui el firestore recyclerView
        // !!!! Puede que este binding dé problemas, pero tenemos que llegar al "recyclerview" de alguna manera...
        binding = ActivityDatosSensoresBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); //si no está

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseFirestore.getInstance()

                .collection("datosSensores").document(currentUser.getUid()).collection("datos");
        FirestoreRecyclerOptions<DatosSensores> opciones = new FirestoreRecyclerOptions
                .Builder<DatosSensores>().setQuery(query, DatosSensores.class).build();
        adaptador = new MyAdapter(opciones, this);  
        binding.recyclerview.setAdapter(adaptador);
        binding.recyclerview.setLayoutManager(
                new LinearLayoutManager(this));
        //adaptador.startListening();
    }

    @Override protected void onStart() {
        super.onStart();
        adaptador.startListening();
    }
    @Override protected void onStop() {
        super.onStop();
        adaptador.stopListening();
    }
    public void botonVolver(View view) {
        finish();
    }
}