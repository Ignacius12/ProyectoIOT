package com.example.proyectoiot.datosSensoresView;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class StartupIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent arg1)     {
        Log.d("Anuncio", "Anuncio recibido con éxito");
        Log.d("Receiver", "Receiver starts job");
        scheduleJob(context);
        Log.i("Autostart", "started");
    }

    public void scheduleJob(Context context){
        ComponentName componentName = new ComponentName(context, NotificacionesJobService.class);
        JobInfo info = new JobInfo.Builder(123,componentName)
                // Este .setPeriodic si detecta un valor menor a 15 minutos, lo cambiará a 15 minutos
                .build();
        JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(info);
    }
}
