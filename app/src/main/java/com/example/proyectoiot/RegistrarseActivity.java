package com.example.proyectoiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegistrarseActivity extends AppCompatActivity {
    private EditText contrasena;
    private EditText contrasenaConfirmacion;
    private EditText correo;
    private EditText nombre;
    private FirebaseAuth mAuth;
    TextView registrarseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        correo = findViewById(R.id.correo);
        contrasena = findViewById(R.id.contrasena);
        contrasenaConfirmacion = findViewById(R.id.contrasenaConfirmacion);
        registrarseBtn = findViewById(R.id.loginBtn);
        nombre = findViewById(R.id.nombre);

        registrarseBtn.setOnClickListener(v->registrarUsuario());
        getSupportActionBar().hide();

    }
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------
    public void registrarUsuario() {
        String email = this.correo.getText().toString().trim();
        String password = this.contrasena.getText().toString();
        if (validateData(email, password, this.contrasenaConfirmacion.getText().toString())) {
            createAccountInFirebase(email, password);
        }
    }
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------

    public void createAccountInFirebase(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegistrarseActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegistrarseActivity.this,"La cuenta ha sido creada correctamente, verifique su correo electrónico",Toast.LENGTH_SHORT).show();
                    firebaseAuth.getCurrentUser().sendEmailVerification();

                    //Crear usuario en la database ----------------------------
                    AplicacionActivity funciones = new AplicacionActivity();
                    String nombre1 = nombre.getText().toString();
                    funciones.crearUsuarioFirebase(nombre1);
                    //----------------------------------------------------------

                    firebaseAuth.signOut();
                    finish();
                }
                else{
                    Toast.makeText(RegistrarseActivity.this,"Ha ocurrido algun problema",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------
    public boolean validateData(String email, String password, String confirmPassword) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.correo.setError("El email no es valido");
            return false;
        } else if (password.length() < 6) {
            this.contrasena.setError("La contraseña es muy corta");
            return false;
        } else if (password.equals(confirmPassword)) {
            return true;
        } else {
            this.contrasenaConfirmacion.setError("Las contraseñas no coinciden");
            return false;
        }
    }




}