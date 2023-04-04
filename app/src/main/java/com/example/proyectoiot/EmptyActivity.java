package com.example.proyectoiot;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.proyectoiot.ui.gallery.GalleryFragment;

import java.util.Objects;

public class EmptyActivity extends AppCompatActivity {
    private static final int SOLICITUD_PERMISO_CALL_PHONE = 0;

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
    public void irCambiarFoto(View view) {
        startActivity(new Intent(EmptyActivity.this, UploadProfilePicActivity.class));
    }
    public void irMaps(View view) {
        startActivity(new Intent(EmptyActivity.this, googleMapsActivity.class));
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty);
        Intent intentReciever = getIntent();
        String idEmpty = intentReciever.getStringExtra("ID");
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        Log.w("ola","este es el intent " + idEmpty);

                if(Objects.equals(idEmpty, "Contacto")){
                    fragmentTransaction.replace(R.id.containerEmpty,new ContactoFragment()).commit();
                }
                else if(Objects.equals(idEmpty, "Notas")){
                     fragmentTransaction.replace(R.id.containerEmpty,new NotasFragment()).commit();
                }
                else if(Objects.equals(idEmpty, "Cuenta")){
                    fragmentTransaction.replace(R.id.containerEmpty,new GalleryFragment()).commit();
                }
            }
    }