package com.example.parking.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.parking.Activities.MainActivity;
import com.example.parking.Databaseconstant.Constants;
import com.example.parking.ParkingStation.ParkingStation;
import com.example.parking.R;
import com.example.parking.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private final int REQUEST_LOCATION_PERMISSION = 1;

    private SearchView searchView;

    ActivityMainBinding binding;

    // Define markerDocumentMap at the class level
    private HashMap<Marker, QueryDocumentSnapshot> markerDocumentMap = new HashMap<>();

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize SearchView
        MainActivity mainActivity = (MainActivity) getActivity();
        searchView = mainActivity.findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if (location == null || location.isEmpty()) {
                    showToast("Enter the search location first");
                } else {
                    Geocoder geocoder = new Geocoder(getContext());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addressList != null && !addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        // Clear existing markers
                        googleMap.clear();

                        // Add marker for searched location
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng)
                                .title("Search Location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        googleMap.addMarker(markerOptions);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f));

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference adminRef = db.collection(Constants.KEY_COLLECTION_ADMIN);

                        adminRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    String locationName = documentSnapshot.getString("location");
                                    Geocoder geocoder1 = new Geocoder(requireContext(), Locale.getDefault());

                                    try {
                                        List<Address> addresses = geocoder1.getFromLocationName(locationName, 1);
                                        if (!addresses.isEmpty()) {
                                            Address parkingAddress = addresses.get(0);
                                            LatLng parkingLatLng = new LatLng(parkingAddress.getLatitude(), parkingAddress.getLongitude());

                                            double distance = calculateDistance(latLng, parkingLatLng);
                                            // If parking station is within 100 km radius, display marker
                                            if (distance <= 100) {
                                                MarkerOptions parkingMarkerOptions = new MarkerOptions()
                                                        .position(parkingLatLng)
                                                        .title(documentSnapshot.getString("location"))
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                                Marker parkingMarker = googleMap.addMarker(parkingMarkerOptions);
                                                // Associate marker with document
                                                markerDocumentMap.put(parkingMarker, documentSnapshot);

                                                googleMap.setOnMarkerClickListener(marker -> {
                                                    QueryDocumentSnapshot clickedDocument = markerDocumentMap.get(marker);
                                                    if (clickedDocument != null) {
                                                        Intent intent = new Intent(getContext(), ParkingStation.class);
                                                        intent.putExtra("adminnumber", clickedDocument.getString("adminnumber"));
                                                        startActivity(intent);
                                                        return true;
                                                    }
                                                    return false;
                                                });
                                            }
                                        } else {
                                            showToast("Location not found for: " + locationName);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).addOnFailureListener(e -> showToast("Failed To Load Parking Station"));

                    } else {
                        showToast("Location not found");
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        mapFragment.getMapAsync(this);

        return view;
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        googleMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                CollectionReference adminRef = db.collection(Constants.KEY_COLLECTION_ADMIN);

                adminRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                String locationName = documentSnapshot.getString("location");

                                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

                                try {

                                    List<Address> addresses = geocoder.getFromLocationName(locationName, 1);

                                    if (!addresses.isEmpty()) {

                                        Address address = addresses.get(0);
                                        LatLng adminLatLng = new LatLng(address.getLatitude(), address.getLongitude());

                                        double distance = calculateDistance(latLng, adminLatLng);

                                        // If admin is within 100km radius, display marker
                                        if (distance <= 100) {
                                            MarkerOptions markerOptions = new MarkerOptions()
                                                    .position(adminLatLng)
                                                    .title(documentSnapshot.getString("location"))
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                                            Marker marker = googleMap.addMarker(markerOptions);
                                            // Associate marker with document
                                            markerDocumentMap.put(marker, documentSnapshot);
                                            googleMap.setOnMarkerClickListener(clickedmarker -> {
                                                QueryDocumentSnapshot clickedDocument = markerDocumentMap.get(clickedmarker);
                                                if (clickedDocument != null) {
                                                    Intent intent = new Intent(getContext(), ParkingStation.class);
                                                    intent.putExtra("adminnumber", clickedDocument.getString("adminnumber"));
                                                    startActivity(intent);
                                                    return true;
                                                }
                                                return false; // Allow other markers to be clickable
                                            });
                                        }

                                    } else {
                                        showToast("Location not found for: " + locationName);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Failed To Load Parking Station");
                    }
                });

                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Current Location");
                googleMap.addMarker(markerOptions);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
            } else {
                Toast.makeText(requireContext(), "Unable to fetch current location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            } else {

                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private double calculateDistance(LatLng latLng1, LatLng latLng2) {
        final int R = 6371;

        double latDistance = Math.toRadians(latLng2.latitude - latLng1.latitude);
        double lonDistance = Math.toRadians(latLng2.longitude - latLng1.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latLng1.latitude)) * Math.cos(Math.toRadians(latLng2.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Distance in kilometers

        return distance;
    }
}
