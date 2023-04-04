package com.example.proyectoiot.chartPackage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ExecutableService extends BroadcastReceiver {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String user = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        final String[] calidadAire = new String[1];
        final String[] cantidadAgua = new String[1];
        final String[] flujoAgua = new String[1];
        final String[] humedadAire = new String[1];
        final String[] temperatura = new String[1];
        final int[] almacenamiento = new int[1];
        db.collection("datosSensores").document(user).get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                                if (task.isSuccessful()) {
                                    //LECTURA DEL VALOR
                                    calidadAire[0] = task.getResult().getString("calidadAire");
                                    cantidadAgua[0] = task.getResult().getString("cantidadAgua");
                                    flujoAgua[0] = task.getResult().getString("flujoAgua");
                                    humedadAire[0] = task.getResult().getString("humedadAire");
                                    temperatura[0] = task.getResult().getString("temperatura");
                                    almacenamiento[0] = Math.toIntExact(task.getResult().getLong("almacenamiento"));

                                    //ORGANIZACION DE DOCUMENTOS
                                    //aumenta en uno el numero de almacenamiento para
                                    //poder organizar los documentos
                                    int almacenamientoReal = almacenamiento[0]+1;
                                    Map<String, Object> cambio = new HashMap<>();
                                    cambio.put("almacenamiento", almacenamientoReal);
                                    db.collection("datosSensores").document(user).set(cambio, SetOptions.merge());

                                    //ALMACENAMIENTO DEL VALOR
                                    Date date = new Date();
                                    FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                                    Map<String, Object> valores = new HashMap<>();
                                    String pattern = "dd-MM-yyyy";
                                    String dateInString =new SimpleDateFormat(pattern).format(new Date());
                                    valores.put("almacenamiento",almacenamientoReal);
                                    valores.put("fecha", date.toString());
                                    valores.put("fechaAlt",dateInString);
                                    valores.put("temperatura", temperatura[0]);
                                    valores.put("flujoAgua", flujoAgua[0]);
                                    valores.put("calidadAire", calidadAire[0]);
                                    valores.put("cantidadAgua", cantidadAgua[0]);
                                    valores.put("humedadAire", humedadAire[0]);
                                    db.collection("datosSensores").document(user).collection
                                            ("almacenamiento").document(String.valueOf(almacenamientoReal)).set(valores, SetOptions.merge());
                                } else {
                                    Log.e("Firestore", "Error al leer", task.getException());
                                }
                            }
                        });}
    }

