package com.example.proyectoiot;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Variables para el RecyclerView y el adapter
    private List<String> imageUrls = new ArrayList<>();
    private ImageAdapter imageAdapter;
    private RecyclerView recyclerView;

    // Referencia a la colección "images" de Firestore
    private CollectionReference imagesRef;

    public CommunityFragment() {
        // Required empty public constructor
    }

/**
 * Use this factory method to create a new instance of
 * this fragment using the provided parameters.
 *
 * @param param1 Parameter 1.
 * @
 **/
// TODO: Rename and change types and number of parameters
public static CommunityFragment newInstance(String param1, String param2) {
    CommunityFragment fragment = new CommunityFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Inicializa la referencia a la colección "images" de Firestore
        imagesRef = FirebaseFirestore.getInstance().collection("images");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        // Inicializa el RecyclerView y el adapter
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        imageAdapter = new ImageAdapter(getContext(), imageUrls);
        recyclerView.setAdapter(imageAdapter);

        // Escucha los cambios en la colección "images" de Firestore
        imagesRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                // Limpia la lista de URLs de imágenes y añade los nuevos
                imageUrls.clear();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    String imageUrl = document.getString("url");
                    imageUrls.add(imageUrl);
                }

                // Notifica al adapter sobre los cambios en la lista de URLs de imágenes
                imageAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

}