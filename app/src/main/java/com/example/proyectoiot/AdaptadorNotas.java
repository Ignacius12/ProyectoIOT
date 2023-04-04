package com.example.proyectoiot;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.proyectoiot.databinding.ElementoNotaBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdaptadorNotas extends
        FirestoreRecyclerAdapter<Note, AdaptadorNotas.ViewHolder> {
    protected View.OnClickListener onClickListener;
    protected Context context;
    protected  static CollectionReference notas;

    public AdaptadorNotas(
            @NonNull FirestoreRecyclerOptions<Note> options, Context context){
        super(options);
        this.context = context;
    }
    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView TituloNota, ContenidoNota;
        private Task<DocumentSnapshot> aviso123;

        public ViewHolder(ElementoNotaBinding itemView) {
            super(itemView.getRoot());
            TituloNota = itemView.TituloNota;
            ContenidoNota = itemView.ContenidoNota;
        }
        // Personalizamos un ViewHolder a partir de un lugar
        public void personaliza(Note aviso) {
            //FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            //FirebaseFirestore db = FirebaseFirestore.getInstance();
            //notas = db.collection("notes").document(currentUser.getUid()).collection("my_notes");
            //notas.document().get();
            TituloNota.setText(aviso.getTitle());
            ContenidoNota.setText(aviso.getContent());


        }
    }

    @Override
    public AdaptadorNotas.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        ElementoNotaBinding v = ElementoNotaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        v.getRoot().setOnClickListener(onClickListener);
        return new AdaptadorNotas.ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdaptadorNotas
            .ViewHolder holder, int position, @NonNull Note note) {
        holder.personaliza(note);

        holder.itemView.setOnClickListener((v)->{
            Intent intent = new Intent(context,EditarNotasActivity.class);
            intent.putExtra("title",note.title);
            intent.putExtra("content",note.content);

            String docId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId",docId);
            context.startActivity(intent);
        });
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }

    public String getKey(int pos) {
        return super.getSnapshots().getSnapshot(pos).getId();
    }
    /*
    public int getPos(String id) {
        int pos = 0;
        while (pos < getItemCount()) {
            if (getKey(pos).equals(id)) return pos;
            pos++;
        }
        return -1;
    }*/

}