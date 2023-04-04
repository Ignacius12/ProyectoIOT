package com.example.proyectoiot.chartPackage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmHandler {
    private Context context;

    public AlarmHandler(Context context){
        this.context = context;
    }
    // activa la 'alarma'
    public void setAlarmManager(){
        Intent intent = new Intent(context, ExecutableService.class);
        PendingIntent sender = PendingIntent.getBroadcast(context,2,intent,0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(am != null){
            //triggerea el servicio cada segundo
            long triggerAfter = 1000;
            long triggerEvery = 1000;
            am.setRepeating(AlarmManager.RTC_WAKEUP,triggerAfter,triggerEvery,sender);
        }

    }
    // cancela la 'alarma'
    public void cancelAlarmManager(){
        Intent intent = new Intent(context, ExecutableService.class);
        PendingIntent sender = PendingIntent.getBroadcast(context,2,intent,0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(am != null){
            am.cancel(sender);
        }

    }
}
