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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    LatLng userLatLng;
    ArrayList<Location> roadFrogs = new ArrayList<>();
    String bestProvider;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

// Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
//        비동기 방식(동시에 라는 뜻)으로 별도 스레드로 지도 데이터 읽어오도록 요척
        mapFragment.getMapAsync(this);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setCostAllowed(true); //비용지불 감수하고 best location provider
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//정확도
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);// 배터리 소모 신경안씀
        criteria.setAltitudeRequired(false); // 고도 고려 안함
        bestProvider = locationManager.getBestProvider(criteria, true);
//        locationManager.requestLocationUpdates(bestProvider,1000, 1,locationListener);

        gson = new GsonBuilder().setPrettyPrinting().create();

////        실시간 위치 업데이트
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

    private void setRoadFrogList() {
//    todo 시간 보고 시간 지났으면 개구리 위치 전부 바꾸기

        //만들기 및 체크
        long currentTime = System.currentTimeMillis();
        if (getPref("time").equals("null")) {
            //새로 DB에 추가
            pref();
        } else {
            //데이터는 있지만 시간은 초과한 경우
            long diffHours = (currentTime - Long.parseLong(getPref("time"))) / (1000 * 60);
            if (diffHours > 24) {
                pref();
            }

            //todo 기존 DB의 개구리 사용
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
        }

    }

    public void pref() {
        //새 개구리 DB 만들기
        userLocation = getCurrentUserLocation();
        Log.i("hhh", "값 가져옴");
        for (int index = 0; index < 5; index++) {
            Location randomLocation = getRandomLatLng(userLocation, 100);
            roadFrogs.add(randomLocation);
            roadFrogLatLng[0] = randomLocation.getLatitude();
            roadFrogLatLng[1] = randomLocation.getLongitude();
            setPref("locations" + index, gson.toJson(roadFrogLatLng));
        }
        ;
        long currentTime = System.currentTimeMillis();
        setPref("time", currentTime + "");
    }

    public void setPref(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("test", MODE_PRIVATE);    // test 이름의 기본모드 설정
        SharedPreferences.Editor editor = sharedPreferences.edit(); //sharedPreferences를 제어할 editor를 선언
        editor.putString(key, value); // key,value 형식으로 저장
        editor.commit();    //최종 커밋. 커밋을 해야 저장이 된다.
    }

    public String getPref(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("test", MODE_PRIVATE);    // test 이름의 기본모드 설정, 만약 test key값이 있다면 해당 값을 불러옴.
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
        userLocation = getCurrentUserLocation();
        Log.i("hhh", "값 가져옴");
        //todo 서버에서 사용자들의 개구리 가져오기
//        37.560797, 127.034571
        //랜덤 생성된 개구리 표시
        for (Location frogLocation : roadFrogs) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(frogLocation.getLatitude(), frogLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_frog))
                    .title(userLocation.distanceTo(frogLocation) + "m")
            );
            Log.i("loda", frogLocation.getLatitude() + "\n" + frogLocation.getLongitude());
        }
//        높을수록 확대:15 default
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
            Toast.makeText(this, "위치 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (locationManager.isProviderEnabled("gps")) {
            Log.i("hhh", "지피에스 시도");
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if(currentLocation == null && locationManager.isProviderEnabled("network")){
            Log.i("hhh", "인터넷 시도");
            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (currentLocation == null){
            Toast.makeText(this, "위치 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            Log.i("hhh", "위치정보 못찾음");
            finish();
        }else{
            Log.i("hhh", "위치정보 찾음");
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
//      1m 이내에 개구리 있으면 애니메이션 주기
        }
    };

}