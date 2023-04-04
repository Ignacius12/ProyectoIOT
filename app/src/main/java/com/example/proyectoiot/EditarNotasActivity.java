package com.example.proyectoiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditarNotasActivity extends AppCompatActivity {

    EditText titleEditText, contentEditText;
    ImageButton saveNotebtn;
    TextView pageTitleTextView;
    String title,content,docId;
    ImageView deleteNoteTextViewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_notas);

        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        pageTitleTextView = findViewById(R.id.page_tittle);
        saveNotebtn = findViewById(R.id.save_note_btn);
        deleteNoteTextViewBtn = findViewById(R.id.delete_note_image_view_btn);

        saveNotebtn.setOnClickListener((v)->saveNote());

        //Recibir datos
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        titleEditText.setText(title);
        contentEditText.setText(content);

        deleteNoteTextViewBtn.setOnClickListener((v)->deleteNoteFromFirebase());

    }
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------
    void saveNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        if(noteTitle==null || noteTitle.isEmpty()){
            titleEditText.setError("la nota requiere un título");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);

        saveNoteToFirebase(note);
    }
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------
    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        documentReference = getCollectionReferenceForNotes().document(docId);
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(EditarNotasActivity.this,"Nota Editada",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(EditarNotasActivity.this,"Error al guardar la nota",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------
    static CollectionReference getCollectionReferenceForNotes(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance().collection("notes").document(currentUser.getUid()).collection("my_notes");
    }
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------
    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
        documentReference = getCollectionReferenceForNotes().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(EditarNotasActivity.this,"Nota borrada",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(EditarNotasActivity.this,"Error al borrar la nota",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}