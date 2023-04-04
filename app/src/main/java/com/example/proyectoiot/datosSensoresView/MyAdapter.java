package com.example.proyectoiot.datosSensoresView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoiot.Note;
import com.example.proyectoiot.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class MyAdapter extends
        FirestoreRecyclerAdapter<DatosSensores, MyViewHolder> {

    Context context;
    List<DatosSensores> items;

    public MyAdapter(@NonNull FirestoreRecyclerOptions<DatosSensores> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.elemento_datossensores,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull DatosSensores datos) {
        switch (datos.getColor()){
            case "#26ba0f": // calidadAire
                String calidadAireString;
                double calidadAireDouble = Double.parseDouble(datos.getTexto());
                if (calidadAireDouble >= 67) {
                    calidadAireString = "Calidad del aire BUENA (" + calidadAireDouble + "%)";
                } else if (calidadAireDouble >= 33) {
                    calidadAireString = "Calidad del aire MALA (" + calidadAireDouble + "%)";
                } else {
                    calidadAireString = "Calidad del aire HORRIBLE(" + calidadAireDouble + "%)";
                }
                holder.textView.setText(calidadAireString);
                break;
            case "#ba0f0f": // temperatura
                String temperaturaString = "La temperatura es " + Double.parseDouble(datos.getTexto()) + "ºC";
                holder.textView.setText(temperaturaString);

                break;
            case "#c7d111": // humedadAire
                String humedadAireString = "Humedad del aire " + Double.parseDouble(datos.getTexto()) + "%";
                holder.textView.setText(humedadAireString);
                break;
            case "#071fb8": // cantidadAgua
                String cantidadAguaString = "Depósito al " + Double.parseDouble(datos.getTexto()) + "%";
                holder.textView.setText(cantidadAguaString);
                break;
            case "#43b9d1": // flujoAgua
                String flujoAguaString;
                // Si hay flujo de agua o no cambia el texto y manda una notificacion
                if (datos.getTexto().equals("1")){
                    flujoAguaString = "No hay atascos";
                } else {
                    flujoAguaString = "Los tubos tienen un atasco";
                }
                holder.textView.setText(flujoAguaString);
                break;
        }

        //Log.d("datos","texto: "+ datos.getTexto());
        //Log.d("datos", "color " + datos.getColor());
        holder.relativeView.getBackground().setColorFilter(Color.parseColor("#FAF5EF"), PorterDuff.Mode.DARKEN);

    }



    /*@Override
    public int getItemCount() {
        return items.size();
    }*/
}
