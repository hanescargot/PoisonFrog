package com.pyrion.game.poison_frog.trade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pyrion.game.poison_frog.R;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import android.location.Location;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;

public class ActivityMap extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent(); // todo page navigation


// Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
//        비동기 방식(동시에 라는 뜻)으로 별도 스레드로 지도 데이터 읽어오도록 요척
        mapFragment.getMapAsync(this);


        //        내 위치 사용에 대한 동적 퍼미션
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int checkResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if(checkResult == PackageManager.PERMISSION_DENIED){
                String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(permissions, 0);
            }
        }
    }

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;


        LatLng userPosition = new LatLng(37.560797, 127.034571);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 20));
//        벡터 이미지 안됨
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.560797, 127.034571))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_frog))
                .title("User Frog Name")
        );
//        높을수록 확대:20 default
        googleMap.setMinZoomPreference(10.0f);

        UiSettings settings = googleMap.getUiSettings();
        settings.setMyLocationButtonEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);



        //다이얼로그 선택하면 발동하는 메소드
//         허용안하면 샤용할 수 없도록 하기 앱 설치할 때 부터
//        onRequestPermissionsResult( new);

//        googleMap.setOnMyLocationButtonClickListener(this);
//        googleMap.setOnMyLocationClickListener(this);
//        enableMyLocation();

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull @NotNull Location location) {

    }
//
//    private void enableMyLocation() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            if (googleMap != null) {
//                googleMap.setMyLocationEnabled(true);
//            }
//        } else {
//            // Permission to access the location is missing. Show rationale and request permission
//            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
//                    Manifest.permission.ACCESS_FINE_LOCATION, true);
//        }
//    }
//
//    @Override
//    public boolean onMyLocationButtonClick() {
//        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
//        // Return false so that we don't consume the event and the default behavior still occurs
//        // (the camera animates to the user's current position).
//        return false;
//    }
//
//    @Override
//    public void onMyLocationClick(@NonNull Location location) {
//        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
//            return;
//        }
//
//        if (ActivityCompat.requestPermissions(this, LOCATION_PERMISSION_REQUEST_CODE,
//                Manifest.permission.ACCESS_FINE_LOCATION, true, C)) {
//            // Enable the my location layer if the permission has been granted.
//            enableMyLocation();
//        } else {
//            // Permission was denied. Display an error message
//            // Display the missing permission error dialog when the fragments resume.
//            permissionDenied = true;
//        }
//    }
//    @Override
//    protected void onResumeFragments() {
//        super.onResumeFragments();
//        if (permissionDenied) {
//            // Permission was not granted, display error dialog.
//            showMissingPermissionError();
//            permissionDenied = false;
//        }
//    }
//
//    /**
//     * Displays a dialog with error message explaining that the location permission is missing.
//     */
//    private void showMissingPermissionError() {
//        PermissionUtils.PermissionDeniedDialog
//                .newInstance(true).show(getSupportFragmentManager(), "dialog");
//    }
}