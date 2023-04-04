
package com.example.proyectoiot.chartPackage;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.HoverMode;
import com.anychart.enums.TooltipPositionMode;
import com.example.proyectoiot.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ColumnChartActivity extends AppCompatActivity {
    private Handler mHandler;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_column_chart);
        Intent intentReciever = getIntent();
        String idEmpty = intentReciever.getStringExtra("ID");
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();

        if(Objects.equals(idEmpty, "cantidadAgua")){
            grafica("cantidadAgua");
        }
        else if(Objects.equals(idEmpty, "calidadAire")){
            grafica("calidadAire");
        }
        else if(Objects.equals(idEmpty, "humedadAire")){
            grafica("humedadAire");
        }
        else if(Objects.equals(idEmpty, "temperatura")){
            grafica("temperatura");
        }
    }


    //ALMACENAR DATOS EN FORMA DE FUNCION
    //NO SE UTILIZA PERO SIRVE POR SI
    //SE CAMBIA LA ALARMA EN EL FUTURO
    //O PARA ALMACENAR DATOS DE FORMA DIRECTA
    public void almacenarDatos(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String user = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        final String[] calidadAire = new String[1];
        final String[] cantidadAgua = new String[1];
        final Boolean[] flujoAgua = new Boolean[1];
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
                                    flujoAgua[0] = Boolean.valueOf(task.getResult().getString("flujoAgua"));
                                    humedadAire[0] = task.getResult().getString("humedadAire");
                                    temperatura[0] = task.getResult().getString("temperatura");
                                    almacenamiento[0] = Integer.parseInt(Objects.requireNonNull(task.getResult().getString("almacenamiento")));

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
                                    valores.put("fecha", date);
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




    void grafica(String tipo){
        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

        Cartesian cartesian = AnyChart.cartesian();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String user = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        final String[] valor = new String[1];
        final String[] fecha = new String[1];
        db.collection("datosSensores").document(user).get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    //LECTURA DEL VALOR
                                    final int[] almacenamiento = new int[1];
                                    almacenamiento[0] = Math.toIntExact(task.getResult().getLong("almacenamiento"));
                                    List<DataEntry> data = new ArrayList<>();
                                    String array[] = new String[almacenamiento[0]+1];
                                    for (int i = 0; i < almacenamiento[0]; i++) {
                                        final int finalI1 = i;
                                        db.collection("datosSensores").document(user).collection("almacenamiento").document(String.valueOf(i+1)).get()
                                                .addOnCompleteListener(
                                                        new OnCompleteListener<DocumentSnapshot>() {
                                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    valor[0] = task.getResult().getString(tipo);
                                                                    fecha[0] = task.getResult().getString("fecha");
                                                                    int idNumerica = task.getResult().getDouble("almacenamiento").intValue();
                                                                    data.add(new ValueDataEntry(String.valueOf(idNumerica), Double.valueOf(valor[0])));
                                                                    array[idNumerica] = fecha[0];
                                                                    if(idNumerica == almacenamiento[0]){
                                                                        Column column = cartesian.column(data);
                                                                        cartesian.animation(true);
                                                                        cartesian.title(tipo);
                                                                        cartesian.yScale().minimum(0d);

                                                                        cartesian.yAxis(0).labels().format("");

                                                                        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                                                                        cartesian.interactivity().hoverMode(HoverMode.BY_X);
                                                                        /*column.tooltip()
                                                                                .titleFormat("Valor {%X}")
                                                                                .position(Position.CENTER_BOTTOM)
                                                                                .anchor(Anchor.CENTER_BOTTOM)
                                                                                .offsetX(0d)
                                                                                .offsetY(5d)
                                                                                .format("{%Value}{groupsSeparator: }");

                                                                         */
                                                                        cartesian.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
                                                                            @Override
                                                                            public void onClick(Event event) {
                                                                                Toast.makeText(ColumnChartActivity.this, array[Integer.parseInt(Objects.requireNonNull(event.getData().get("x")))] + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();

                                                                                cartesian.title(array[Integer.parseInt(Objects.requireNonNull(event.getData().get("x")))]);
                                                                            }
                                                                        });

                                                                        cartesian.xAxis(0).title("datos");
                                                                        cartesian.yAxis(0).title("valor");

                                                                        anyChartView.setChart(cartesian);
                                                                    }

                                                                } else {
                                                                    Log.e("Firestore", "Error al leer", task.getException());
                                                                }
                                                            }
                                                        });
                                    }


                                } else {
                                    Log.e("Firestore", "Error al leer", task.getException());
                                }

                            }



                        });
    }
    public void cantidadAguaBoton(View view) {
        grafica("cantidadAgua");
    }

    public void calidadAireBoton(View view) {
        grafica("calidadAire");
    }
    public void humedadAireBoton(View view) {
        grafica("humedadAire");
    }
    public void temperaturaBoton(View view){
        grafica("temperatura");
    }
    public interface MyCallback {
        void onCallback(List<DataEntry> data);
    }
}
