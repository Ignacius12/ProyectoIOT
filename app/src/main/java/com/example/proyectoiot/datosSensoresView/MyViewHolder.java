package com.example.proyectoiot.datosSensoresView;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoiot.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView textView;
    RelativeLayout relativeView;



    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.textDatos);
        relativeView = itemView.findViewById(R.id.relativeLayoutSensores);
    }

}
