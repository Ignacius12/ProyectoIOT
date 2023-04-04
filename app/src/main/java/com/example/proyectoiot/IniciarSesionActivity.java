package com.example.proyectoiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class IniciarSesionActivity extends AppCompatActivity {

    EditText contrasena;
    EditText correo;
    Button loginBtn;
    private FirebaseAuth mAuth;
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);
        correo = findViewById(R.id.correo);
        contrasena = findViewById(R.id.contrasena);
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(v->loginUser());
        getSupportActionBar().hide();
    }
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------
    public void loginAccountInFirebase(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(IniciarSesionActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(IniciarSesionActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("myTag", "login proqueado");
                } else if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                    IniciarSesionActivity.this.startActivity(new Intent(IniciarSesionActivity.this, AplicacionActivity.class));
                    IniciarSesionActivity.this.finish();
                } else {
                    Toast.makeText(IniciarSesionActivity.this, "El email no ha sido registrado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------
    public void loginUser() {
        String email = this.correo.getText().toString().trim();
        String password = this.contrasena.getText().toString();
        if (validateData(email, password)) {
            loginAccountInFirebase(email, password);
        }
    }
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------
    public boolean validateData(String email, String password) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.correo.setError("El email no es válido");
            return false;
        } else if (password.length() >= 6) {
            return true;
        } else {
            this.contrasena.setError("La contraseña no es válida");
            return false;
        }
    }
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------
    public void irRegistrarse(View view) {
        startActivity(new Intent(this, RegistrarseActivity.class));
    }
}

