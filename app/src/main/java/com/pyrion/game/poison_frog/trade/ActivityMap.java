package com.pyrion.game.poison_frog.trade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pyrion.game.poison_frog.R;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.Inflater;

public class ActivityMap extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;

    private GoogleMap googleMap;
    LocationManager locationManager;
    Criteria criteria;
    Location userLocation;
    ArrayList<Location> roadFrogs = new ArrayList<>();
    ArrayList<Location> serverRoadFrogs = new ArrayList<>();
    String bestProvider;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

// Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
//        ????????? ??????(????????? ?????? ???)?????? ?????? ???????????? ?????? ????????? ??????????????? ??????
        mapFragment.getMapAsync(this);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setCostAllowed(true); //???????????? ???????????? best location provider
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//?????????
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);// ????????? ?????? ????????????
        criteria.setAltitudeRequired(false); // ?????? ?????? ??????
        bestProvider = locationManager.getBestProvider(criteria, true);
//        locationManager.requestLocationUpdates(bestProvider,1000, 1,locationListener);

        gson = new GsonBuilder().setPrettyPrinting().create();

////        ????????? ?????? ????????????
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        if (locationManager.isProviderEnabled("gps")) {
//            locationManager.requestLocationUpdates("gps", 1000, 1, locationListener);
//        }else if(locationManager.isProviderEnabled("network")){
//            locationManager.requestLocationUpdates("network", 1000, 1, locationListener);
//        }

        setRoadFrogList();

    }//create

    double[] roadFrogLatLng = new double[2];
    double[] serverRoadFrogsLatLng = new double[2];

    private void setRoadFrogList() {
//    ?????? ?????? ?????? ???????????? ????????? ?????? ?????? ?????????

        //????????? ??? ??????
        long currentTime = System.currentTimeMillis();
        if (getPref("time").equals("null")) {
            //?????? DB??? ??????
            pref();
            Log.i("newFrog", "t?????? ?????????");
        } else {
            //???????????? ????????? ????????? ????????? ??????
            long diffHours = (currentTime - Long.parseLong(getPref("time"))) / (1000 * 60);
            if (diffHours > 24) {
                pref();
            }
            Log.i("newFrog", "t?????? ?????????");
            //?????? DB??? ????????? ??????
            Type doubleType = new TypeToken<double[]>() {
            }.getType();
            for (int index = 0; index < 5; index++) {
                roadFrogLatLng = gson.fromJson(getPref("locations" + index), doubleType);
                Location location = new Location("");

                if(roadFrogLatLng[0]!=-1) {
                    location.setLatitude(roadFrogLatLng[0]);
                    location.setLongitude(roadFrogLatLng[1]);
                    roadFrogs.add(location);
                }
            }

            //????????? ?????? ????????? ????????? serverRoadFrogs ?????????
            setFirebaseDB();
        }

    }

    public void setFirebaseDB(){
        //todo ???????????? ??????????????? ????????? ????????????
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("road_frogs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> user = new HashMap<>();
                            Location location = new Location("");

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user = document.getData();
//                                location[0]   oneFrogSet[1] ?????? ???????????? location ??? ?????????
                                String locationString = (String)user.get("location");
                                serverRoadFrogsLatLng =  gson.fromJson(locationString, double[].class);
                                location.setLatitude(serverRoadFrogsLatLng[0]);
                                location.setLongitude(serverRoadFrogsLatLng[1]);
                                serverRoadFrogs.add(location);
                                Log.i("!!import", locationString);

                                //todo ???????????? ????????? ????????? ??????
                                for (Location frogLocation : serverRoadFrogs) {
                                    googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(frogLocation.getLatitude(), frogLocation.getLongitude()))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_frog))
                                            .title(userLocation.distanceTo(frogLocation) + "m")
                                    );
                                    Log.i("loda", frogLocation.getLatitude() + "\n" + frogLocation.getLongitude());
                                }
                            }
                        } else {
                        }
                    }
                });


    }

    public void pref() {
        //??? ????????? DB ?????????
//        userLocation = getCurrentUserLocation();
        requestMyLocation("makeNewFrogDB");
    }

    public void makeNewFrogDB(){
        Log.i("newFrog", "make New FRog");
        for (int index = 0; index < 5; index++) {
            Location randomLocation = getRandomLatLng(userLocation, 100);
            roadFrogs.add(randomLocation);
            roadFrogLatLng[0] = randomLocation.getLatitude();
            roadFrogLatLng[1] = randomLocation.getLongitude();
            setPref("locations" + index, gson.toJson(roadFrogLatLng));
        }
        long currentTime = System.currentTimeMillis();
        setPref("time", currentTime + "");

    }

    public void setPref(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("test", MODE_PRIVATE);    // test ????????? ???????????? ??????
        SharedPreferences.Editor editor = sharedPreferences.edit(); //sharedPreferences??? ????????? editor??? ??????
        editor.putString(key, value); // key,value ???????????? ??????
        editor.commit();    //?????? ??????. ????????? ?????? ????????? ??????.
    }

    public String getPref(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("test", MODE_PRIVATE);    // test ????????? ???????????? ??????, ?????? test key?????? ????????? ?????? ?????? ?????????.
        String value = sharedPreferences.getString(key, "null");
        return value;
    }

    public Location getRandomLatLng(Location location, int radius) {
        double x0 = location.getLatitude();
        double y0 = location.getLongitude();

        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111300f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(y0);

        double foundLatitude = new_x + x0;
        double foundLongitude = y + y0;
        LatLng randomLatLng = new LatLng(foundLatitude, foundLongitude);
        Location l1 = new Location("");
        l1.setLatitude(randomLatLng.latitude);
        l1.setLongitude(randomLatLng.longitude);

        return l1;
    }


    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
//        userLocation = getCurrentUserLocation();
        requestMyLocation("setMap");
        Log.i("hhh", "??? ?????????");






    }
    public void setGoogleMap(){
        //        37.560797, 127.034571
        //?????? ????????? ????????? ??????
        for (Location frogLocation : roadFrogs) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(frogLocation.getLatitude(), frogLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_frog))
                    .title(userLocation.distanceTo(frogLocation) + "m")
            );
            Log.i("loda", frogLocation.getLatitude() + "\n" + frogLocation.getLongitude());
        }



//        ???????????? ??????:15 default
        googleMap.setMinZoomPreference(10.0f);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 15));

        UiSettings settings = googleMap.getUiSettings();
        settings.setMyLocationButtonEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
    }

    public Location getCurrentUserLocation() {
        Location currentLocation = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "?????? ????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (locationManager.isProviderEnabled("gps")) {
            Log.i("hhh", "???????????? ??????");
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if(currentLocation == null && locationManager.isProviderEnabled("network")){
            Log.i("hhh", "????????? ??????");
            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (currentLocation == null){
            Toast.makeText(this, "?????? ????????? ?????? ??? ????????????.", Toast.LENGTH_SHORT).show();
            Log.i("hhh", "???????????? ?????????");
            finish();
        }else{
            Log.i("hhh", "???????????? ??????");
            return currentLocation;
        }
    return null;
    }


    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }


    @Override
    public void onMyLocationClick(@NonNull @NotNull Location location) {

    }


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
//      1m ????????? ????????? ????????? ??????????????? ??????
        }
    };



    /////?????????????????? ?????? ?????? ????????? ??? ?????? ?????? ??????
//    ?????? ??? ????????? Location Manager??? ??????????????? ?????? ????????? ?????? ??????????????? ?????? ??????

    Location myLocation;
    FusedLocationProviderClient locationProviderClient;
    void requestMyLocation(String key){
        //Google Map?????? ???????????? ?????? ??????????????? API ??????????????? ?????? Fused Location API : play-service-location
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // ????????? ???????????? ?????? ??????
        LocationRequest request = LocationRequest.create();
        request.setInterval(1000); //1ch
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {return;}


        if (key.equals("setMap")){
            Log.i("newFrog", "setMap");
            locationProviderClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
        }
        if(key.equals("makeNewFrogDB")) {
            Log.i("newFrog", "make New FRog1");
            locationProviderClient.requestLocationUpdates(request, locationCallback2, Looper.getMainLooper());
        }
    }


    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            myLocation = locationResult.getLastLocation();
            locationProviderClient.removeLocationUpdates(locationCallback);

            //???????????? ?????? ??????
            userLocation = myLocation;
            setGoogleMap();
            Log.i("newFrog", "ddd");
        }
    };

    LocationCallback locationCallback2 = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            myLocation = locationResult.getLastLocation();
            locationProviderClient.removeLocationUpdates(locationCallback2);

            //???????????? ?????? ??????
            userLocation = myLocation;
            makeNewFrogDB();
        }
    };

}