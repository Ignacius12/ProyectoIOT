package com.example.proyectoiot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class googleMapsActivity extends FragmentActivity implements
        OnMapReadyCallback {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        // Obtenemos el mapa de forma asíncrona (notificará cuando esté listo)
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
    }

    private LatLng getCenterOfMarkers(List<Marker> markers) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        return bounds.getCenter();
    }

    @Override public void onMapReady(GoogleMap mapa) {
        LatLng EspacioVerde = new LatLng(38.9980651, -0.1851356); //Nos ubicamos en Espacio verde
        mapa.addMarker(new MarkerOptions().position(EspacioVerde).title("Espacio Verde"));

        LatLng Ecoterrazas = new LatLng(38.9711038, -0.1797656099241486);
        mapa.addMarker(new MarkerOptions().position(Ecoterrazas).title("Ecoterrazas"));

        LatLng LeroyMerlinGandia = new LatLng(38.9574528, -0.17248522921371356);
        mapa.addMarker(new MarkerOptions().position(LeroyMerlinGandia).title("Leroy Merlin Gandía"));




        List<Marker> markers = new ArrayList<Marker>();
        markers.add(mapa.addMarker(new MarkerOptions().position(EspacioVerde).title("Espacio Verde")));
        markers.add(mapa.addMarker(new MarkerOptions().position(Ecoterrazas).title("Ecoterrazas")));
        markers.add(mapa.addMarker(new MarkerOptions().position(LeroyMerlinGandia).title("Leroy Merlin Gandía")));


        LatLng center = getCenterOfMarkers(markers);


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(center) // Center Set
                .zoom(12.0f)                // Zoom
                .bearing(90)                // Orientation of the camera to east
                .tilt(30)                   // Tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mapa.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //mapa.moveCamera(CameraUpdateFactory.newLatLng(EspacioVerde));
    }


}
