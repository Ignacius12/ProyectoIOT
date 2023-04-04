package com.example.proyectoiot;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.proyectoiot.chartPackage.AlarmHandler;
import com.example.proyectoiot.chartPackage.ColumnChartActivity;
import com.example.proyectoiot.databinding.ActivityAplicacionBinding;
import com.example.proyectoiot.datosSensoresView.SensoresActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AplicacionActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityAplicacionBinding binding;
    private FirebaseAuth mAuth;
    private static final int SOLICITUD_PERMISO_CALL_PHONE = 0;
    TextToSpeech t1 = null;
    //--------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR){
                    t1.setLanguage(Locale.getDefault());
                }
            }
        });
        super.onCreate(savedInstanceState);
        //ALARMA (GUARDAR DATOS)
        AlarmHandler alarmHandler = new AlarmHandler(this);
        alarmHandler.cancelAlarmManager();
        alarmHandler.setAlarmManager();
        //
        binding = ActivityAplicacionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarAplicacion.toolbar);
        binding.appBarAplicacion.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //-----------------------------------------------------------------------------------------------
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.contactoFragment,R.id.communityFragment,R.id.informacionFragment)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_aplicacion);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
  }
    private void voiceAutomation() {
        Intent voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        voice.putExtra(RecognizerIntent.EXTRA_PROMPT,"Di a donde quieres ir");
        startActivityForResult(voice,1);
    }
    //--------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.aplicacion, menu);
        return true;
    }
    //--------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------
    private void loadData(){

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String correo =  Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();
        Map<String, Object> datos = new HashMap<>();
        final View vistaHeader = binding.navView.getHeaderView(0);
        final TextView textCorreo = vistaHeader.findViewById(R.id.textCorreo);
        final TextView textNombre = vistaHeader.findViewById(R.id.textNombre);
        textCorreo.setText(correo);

        //--------------------------------------------------------------------------------------------------------
        //Acceso al nombre del usuario
        //--------------------------------------------------------------------------------------------------------
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                                if (task.isSuccessful()) {
                                    textNombre.setText(task.getResult().getString("nombre"));

                                    Log.e("Firestore", task.getResult().getString("nombre"), task.getException());
                                } else {
                                    Log.e("Firestore", "Error al leer", task.getException());
                                }
                            }
                        });
        //--------------------------------------------------------------------------------------------------------
        //Ultima fecha de conexion
        //--------------------------------------------------------------------------------------------------------
        Date date = new Date();
        FirebaseFirestore db1 = FirebaseFirestore.getInstance();
        Map<String, Object> fecha = new HashMap<>();
        fecha.put("fechaUltimaConexion", date);
        db1.collection("users").document(firebaseAuth.getCurrentUser().getUid()).set(fecha, SetOptions.merge());
    }

    //--------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------
    public void irNotas(View view) {
        startActivity(new Intent(AplicacionActivity.this, CrearNotasActivity.class));
    }
    public void irMaps(View view) {
        startActivity(new Intent(AplicacionActivity.this, googleMapsActivity.class));
    }
    public void botonAsistente(View view){
        voiceAutomation();
        t1.stop();
    }
    //--------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------

    public void irCambiarFoto(View view) {
        startActivity(new Intent(AplicacionActivity.this, UploadProfilePicActivity.class));
    }

    //--------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------
    public void irSensores(View view) {
        startActivity(new Intent(AplicacionActivity.this, SensoresActivity.class));
    }

    public void irUpload(View view) {
        startActivity(new Intent(this, UploadActivity.class));
    }

    //--------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_aplicacion);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //--------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    //--------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------
    public void AbrirPagina(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://smartvalley0.wordpress.com/"));
        startActivity(intent);
    }

    //--------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------
    public void AbrirInstagram(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://instagram.com/smartvalleycontact_?igshid=YmMyMTA2M2Y="));
        startActivity(intent);
    }

    //--------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------
    public void mandarCorreo(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "asunto");
        intent.putExtra(Intent.EXTRA_TEXT, "texto del correo");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"smartValleyContact@gmail.com"});
        startActivity(intent);
    }

    //--------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------
    public void llamarTelefono( View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                .CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            this.startActivity(new Intent(Intent.ACTION_CALL,
                    Uri.parse("tel: " + "634998250")));
        } else {
            solicitarPermiso(Manifest.permission.CALL_PHONE, "Sin el permiso para llamar.", SOLICITUD_PERMISO_CALL_PHONE, this);
        }
    }

    //--------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------
    public static void solicitarPermiso(final String permiso, String
            justificacion, final int requestCode, final Activity actividad) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                permiso)){
            new AlertDialog.Builder(actividad)
                    .setTitle("Solicitud de permiso")
                    .setMessage(justificacion)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ActivityCompat.requestPermissions(actividad,
                                    new String[]{permiso}, requestCode);
                        }}).show();
        } else {
            ActivityCompat.requestPermissions(actividad,
                    new String[]{permiso}, requestCode);
        }
    }

    //--------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------
    public void crearUsuarioFirebase(String name){
        //variables de firebase/firestore
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String correo =  Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();
        //Crear usuario en database
        Map<String, Object> datos = new HashMap<>();
        datos.put("mail", correo);
        datos.put("nombre", name);
        db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).set(datos ,SetOptions.merge());
        //
        //Crear huerto en database
        // AQUI LEOPOLLO!!
        //
        Map<String, Object> documento = new HashMap<>();
        documento.put("temperatura", 27);
        documento.put("flujoAgua", true);
        documento.put("calidadAire", 11.3);
        documento.put("cantidadAgua", 78);
        documento.put("humedadAire", 27.4);
        db.collection("datosSensores").document(firebaseAuth.getCurrentUser().getUid()).set(documento ,SetOptions.merge());
        //Coleccion para notificaciones
        //-------------------------------------------------------------------
        Map<String, Object> documento2 = new HashMap<>();
        Map<String, Object> CalidadAire = new HashMap<>();
        Map<String, Object> CantidadAgua = new HashMap<>();
        Map<String, Object> datosFlujoAgua = new HashMap<>();
        Map<String, Object> datosHumedadAire = new HashMap<>();
        Map<String, Object> temperatura = new HashMap<>();
        documento2.put("temperatura", temperatura);
        documento2.put("flujoAgua", datosFlujoAgua);
        documento2.put("calidadAire", CalidadAire);
        documento2.put("cantidadAgua", CantidadAgua);
        documento2.put("humedadAire", datosHumedadAire);
        temperatura.put("texto", "27");
        temperatura.put("color","#ba0f0f");
        datosFlujoAgua.put("texto", "27");
        datosFlujoAgua.put("color", "#43b9d1");
        CalidadAire.put("texto", "27");
        CalidadAire.put("color", "#26ba0f");
        CantidadAgua.put("texto", "27");
        CantidadAgua.put("color", "#071fb8");
        datosHumedadAire.put("texto", "27");
        datosHumedadAire.put("color", "#c7d111");
        db.collection("datosSensores").document(firebaseAuth.getCurrentUser().getUid()).collection("datos").document("datosCalidadAire")
                .set(CalidadAire);
        db.collection("datosSensores").document(firebaseAuth.getCurrentUser().getUid()).collection("datos").document("datosCantidadAgua")
                .set(CantidadAgua);
        db.collection("datosSensores").document(firebaseAuth.getCurrentUser().getUid()).collection("datos").document("datosFlujoAgua")
                .set(datosFlujoAgua);
        db.collection("datosSensores").document(firebaseAuth.getCurrentUser().getUid()).collection("datos").document("datosHumedadAire")
                .set(datosHumedadAire);
        db.collection("datosSensores").document(firebaseAuth.getCurrentUser().getUid()).collection("datos").document("datosTemperatura")
                .set(temperatura);
    }

    //--------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        loadData();
        //noteAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //noteAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //noteAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data !=null){
            ArrayList<String> arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if(arrayList.get(0).toString().equals("ve a sensores") || arrayList.get(0).toString().equals("sensores") || arrayList.get(0).toString().equals("ir a sensores")
                    || arrayList.get(0).toString().equals("ve a sensores")){
                startActivity(new Intent(AplicacionActivity.this, SensoresActivity.class));
            }
            else if(arrayList.get(0).toString().equals("ve a mi cuenta") || arrayList.get(0).toString().equals("cuenta") || arrayList.get(0).toString().equals("mi cuenta")
                    || arrayList.get(0).toString().equals("ve a cuenta")){
                Intent senderIntent = new Intent(AplicacionActivity.this, EmptyActivity.class);
                senderIntent.putExtra("ID","Cuenta");
                startActivity(senderIntent);
            }
            else if(arrayList.get(0).toString().equals("ve a mis notas") || arrayList.get(0).toString().equals("notas") || arrayList.get(0).toString().equals("ir a mis notas")
                    || arrayList.get(0).toString().equals("quiero ver mis notas") || arrayList.get(0).toString().equals("mis notas")){
                Intent senderIntent = new Intent(AplicacionActivity.this, EmptyActivity.class);
                senderIntent.putExtra("ID","Notas");
                startActivity(senderIntent);
            }
            else if(arrayList.get(0).toString().equals("ve a contacto") || arrayList.get(0).toString().equals("contacto") || arrayList.get(0).toString().equals("ir a contacto")
                    || arrayList.get(0).toString().equals("quiero ver contactos") || arrayList.get(0).toString().equals("atención al cliente")){
                Intent senderIntent = new Intent(AplicacionActivity.this, EmptyActivity.class);
                senderIntent.putExtra("ID","Contacto");
                startActivity(senderIntent);
            }
            else if(arrayList.get(0).toString().equals("ve a mapa") || arrayList.get(0).toString().equals("mapa") || arrayList.get(0).toString().equals("ir a mapa")
                    || arrayList.get(0).toString().equals("quiero ver mapa") || arrayList.get(0).toString().equals("google maps")){
                startActivity(new Intent(AplicacionActivity.this, googleMapsActivity.class));
            }
            else if(arrayList.get(0).toString().equals("crear notas") || arrayList.get(0).toString().equals("quiero crear notas") || arrayList.get(0).toString().equals("ir a crear notas")
                    || arrayList.get(0).toString().equals("quiero crear una nota") || arrayList.get(0).toString().equals("hacer nota")){
                startActivity(new Intent(AplicacionActivity.this, EditarNotasActivity.class));
            }
            else if(arrayList.get(0).toString().equals("hola")){
                t1.speak("hola!",TextToSpeech.QUEUE_FLUSH,null);

            }
            else if(arrayList.get(0).toString().equals("chart") || arrayList.get(0).toString().equals("chart cantidad de agua")){
                Intent senderIntent = new Intent(AplicacionActivity.this, ColumnChartActivity.class);
                senderIntent.putExtra("ID","cantidadAgua");
                startActivity(senderIntent);
            }
            else if(arrayList.get(0).toString().equals("chart temperatura")){
                Intent senderIntent = new Intent(AplicacionActivity.this, ColumnChartActivity.class);
                senderIntent.putExtra("ID","temperatura");
                startActivity(senderIntent);
            }
            else if(arrayList.get(0).toString().equals("chart humedad aire")){
                Intent senderIntent = new Intent(AplicacionActivity.this, ColumnChartActivity.class);
                senderIntent.putExtra("ID","humedadAire");
                startActivity(senderIntent);
            }
            else if(arrayList.get(0).toString().equals("chart calidad Aire")){
                Intent senderIntent = new Intent(AplicacionActivity.this, ColumnChartActivity.class);
                senderIntent.putExtra("ID","calidadAire");
                startActivity(senderIntent);
            }
            else{
                t1.speak("no te he entendido puedes repetir?",TextToSpeech.QUEUE_FLUSH,null);
                Toast.makeText(AplicacionActivity.this,"intenta decirlo con otras palabras",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            t1.speak("no te he entendido puedes repetir?",TextToSpeech.QUEUE_FLUSH,null);

        }
    }



}