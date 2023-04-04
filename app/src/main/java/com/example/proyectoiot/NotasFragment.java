package com.example.proyectoiot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.proyectoiot.databinding.FragmentSlideshowBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class NotasFragment extends Fragment{

    TextView addNoteBtn;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    AdaptadorNotas noteAdapter;
    private FirebaseAuth mAuth;
    private FragmentSlideshowBinding binding; //si no está
    public static AdaptadorNotas adaptador;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSlideshowBinding.inflate(getLayoutInflater());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseFirestore.getInstance().collection("notes").document(currentUser.getUid()).collection("my_notes");
        FirestoreRecyclerOptions<Note> opciones = new FirestoreRecyclerOptions
                .Builder<Note>().setQuery(query, Note.class).build();
        adaptador = new AdaptadorNotas(opciones, getContext());
        System.out.println(getContext());
        binding.recylerView.setAdapter(adaptador);
        binding.recylerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Inflate the layout for this fragment
        adaptador.startListening();
        return binding.getRoot();
    }
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------
    @Override
    public void onStart() {
        super.onStart();
        //noteAdapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        //noteAdapter.stopListening();
    }
    @Override
    public void onResume() {
        super.onResume();
        //noteAdapter.notifyDataSetChanged();
    }
}