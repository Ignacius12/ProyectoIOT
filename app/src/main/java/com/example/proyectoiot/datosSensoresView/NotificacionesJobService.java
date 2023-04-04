package com.example.proyectoiot.datosSensoresView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.proyectoiot.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class NotificacionesJobService extends JobService {
    private boolean jobCancelled = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    boolean flujoAgua;
    int flujoAguaInt;
    double cantidadAgua;
    double temperatura;
    double calidadAire;
    double humedadAire;
    static int notificacionCalidadAire = 1;
    static int notificacionCantidadAgua = 2;
    static int notificacionTemperatura = 3;
    static int notificacionHumedad = 4;
    static int notificacionFlujoAgua = 5;
    private NotificationManager notificationManager;
    static final String CANAL_ID = "mi_canal";
    private static final String TAG = "MyService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d("Jobnotificaciones", "onStartJob: ");
        notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // Aqui empieza el "timer" y el actualizar las notificaciones
        actualizarNotificaciones(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d("Jobnotificaciones", "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }

    public void lanzarNotificacion(String tituloNotificacion, String textoNotificacion, int idNotificacion) {
        Log.d("Notificacion", "empieza la función lanzarNotificacion()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CANAL_ID, "Mis Notificaciones",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Descripcion del canal");
            notificationManager.createNotificationChannel(notificationChannel);
            NotificationCompat.Builder notificacion =
                    new NotificationCompat.Builder(this, CANAL_ID)
                            // esto cambia el titulo de la notificacion
                            .setContentTitle(tituloNotificacion)
                            // esto cambia el texto de la notificacion
                            .setContentText(textoNotificacion)
                            // aqui se cambia la foto de la notificacion
                            .setSmallIcon(R.drawable.planta)
                            .setGroup("datosSensores")
                            .setOngoing(true)
                            .setOnlyAlertOnce(true);
            PendingIntent intencionPendiente = PendingIntent.getActivity(this, 0,
                    new Intent(this, SensoresActivity.class), PendingIntent.FLAG_IMMUTABLE);
            notificacion.setContentIntent(intencionPendiente);
            Log.d("Notificacion", "creando notificacion con id: " + idNotificacion);
            notificationManager.notify(idNotificacion, notificacion.build());
        } else {
            Log.d("Notificaciones", "La version es muy vieja o algo asi");
        }
    }

    public void actualizarNotificaciones(JobParameters jobParameters) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("Jobnotificaciones", "Vuelta del job");
                if (jobCancelled){
                    Log.d("Jobnotificaciones", "jobCancelled ha hecho return en actualizarNotificaciones()");
                    return;
                }
                db.collection("datosSensores").document((firebaseAuth.getCurrentUser()).getUid()).get()
                        .addOnCompleteListener(
                                new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            // Aqui cogemos los datos de los campos de la base de datos de Firestore
                                            Log.d("Timer", "Conexion a la base de datos");
                                            // QUEDA RELLENAR LA BASE DE DATOS CON STRINGS!! Y PROBAR???
                                            cantidadAgua = Double.parseDouble(Objects.requireNonNull(task.getResult().getString("cantidadAgua")));
                                            Log.d("Timer", "cantidad de agua: " + cantidadAgua);
                                            flujoAguaInt = Integer.parseInt(Objects.requireNonNull(task.getResult().getString("flujoAgua")));
                                            // Cogemos un 1 o 0 de flujoAgua en la base de datos y lo transformamos en Boolean
                                            if (flujoAguaInt == 1) {
                                                flujoAgua = true;
                                            } else {
                                                flujoAgua = false;
                                            }
                                            Log.d("Timer", "flujo de agua INTEGER: " + flujoAguaInt);
                                            Log.d("Timer", "flujo de agua: " + flujoAgua);
                                            temperatura = Double.parseDouble(Objects.requireNonNull(task.getResult().getString("temperatura")));
                                            Log.d("Timer", "temperatura: " + temperatura);
                                            calidadAire = Double.parseDouble(Objects.requireNonNull(task.getResult().getString("calidadAire")));
                                            Log.d("Timer", "calidad de aire: " + calidadAire);
                                            humedadAire = Double.parseDouble(Objects.requireNonNull(task.getResult().getString("humedadAire")));
                                            Log.d("Timer", "humedad de aire: " + humedadAire);
                                            // Si hay flujo de agua o no cambia el texto y manda una notificacion
                                            if (flujoAgua) {
                                                try {
                                                    Log.d("Notificacion", "Cancelando notificacion de flujoAgua");
                                                    notificationManager.cancel(notificacionFlujoAgua);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                lanzarNotificacion("Flujo de agua obstruido",
                                                        "Una o más plantas no están recibiendo agua debido a una obstrucción en las tuberías",
                                                        notificacionFlujoAgua);

                                            }
                                            if (calidadAire >= 67) {
                                                // Aqui tengo que hacer que ese borre la notificacion
                                                try {
                                                    Log.d("Notificacion", "Cancelando notificacion de calidadAire");
                                                    notificationManager.cancel(notificacionCalidadAire);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    // Descomentar para ver cuando NO se cancelan notificaciones
                                                    // Toast.makeText(SensoresActivity.this, "No se han borrado las notificaciones", Toast.LENGTH_SHORT).show();
                                                }

                                            } else if (calidadAire >= 33) {
                                                // notificacion de que la calidad es MALA

                                                lanzarNotificacion("Calidad de aire MALA",
                                                        "La calidad del aire es MALA, ¡es importante ventilar la habitación!",
                                                        notificacionCalidadAire);
                                            } else {
                                                // Hacer notificacion al cliente de que la calidad es HORRIBLE

                                                lanzarNotificacion("Calidad de aire HORRIBLE",
                                                        "¡La calidad del aire es HORRIBLE! ¡Tienes que ventilar la habitación INMEDIATAMENTE!",
                                                        notificacionCalidadAire);
                                            }
                                            // Aqui mandamos las notificaciones de temperatura
                                            if (temperatura > 32) {
                                                lanzarNotificacion("Temperatura de las plantas EXCESIVA",
                                                        "Tus plantas están recibiendo demasiado calor (temperatura = " + temperatura + "º)",
                                                        notificacionTemperatura);
                                            } else if (temperatura < 5) {
                                                lanzarNotificacion("Temperatura de las plantas BAJA",
                                                        "Tus plantas están pasando demasiado frío (temperatura = " + temperatura + "º)",
                                                        notificacionTemperatura);
                                            } else {
                                                try {
                                                    Log.d("Notificacion", "Cancelando notificacion de temperatura");
                                                    notificationManager.cancel(notificacionTemperatura);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                            // Aqui mandamos las notificaciones de la humedad del aire
                                            if (humedadAire > 45) {
                                                lanzarNotificacion("Humedad del aire EXCESIVA",
                                                        "El aire tiene demasiada humedad (" + humedadAire + " %)",
                                                        notificacionHumedad);
                                            } else if (humedadAire < 30) {
                                                lanzarNotificacion("Humedad del aire MUY BAJA",
                                                        "El aire tiene muy poca humedad (" + humedadAire + " %)",
                                                        notificacionHumedad);
                                            } else {
                                                try {
                                                    Log.d("Notificacion", "Cancelando notificacion de humedadAire");
                                                    notificationManager.cancel(notificacionHumedad);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                            // Aqui mandamos las notificaciones de la cantidad de agua en el deposito de agua
                                            if (cantidadAgua < 4) {
                                                lanzarNotificacion("Tanque de agua casi vacío",
                                                        "El tanque de agua está casi vacío (" + cantidadAgua + " %), las plantas ya no podrán recibir agua",
                                                        notificacionCantidadAgua);
                                            } else if (cantidadAgua < 15) {
                                                // No tiene que estar a 0% para estar vacío, pues la salida del agua a las plantas está un poco elevado al fondo del tanque
                                                lanzarNotificacion("Queda poca agua en el tanque de agua",
                                                        "El tanque de agua está a un " + cantidadAgua + " % de capacidad, se recomienda rellenar",
                                                        notificacionCantidadAgua);
                                            } else {
                                                try {
                                                    Log.d("Notificacion", "Cancelando notificacion de cantidadAgua");
                                                    notificationManager.cancel(notificacionCantidadAgua);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } else {
                                            Log.e("Firestore", "Error al leer", task.getException());
                                        }
                                    }
                                });
            }
        }).start();
        jobFinished(jobParameters, true);
    }
}
