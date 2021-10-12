package com.pyrion.game.poison_frog.trade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pyrion.game.poison_frog.R;
import com.pyrion.game.poison_frog.data.Frog;

public class ActivityServerSend extends AppCompatActivity {

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_send);
        locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ImageView iv = findViewById(R.id.iv);

        Animation animation_logo = AnimationUtils.loadAnimation(this, R.anim.frog_finder);
        iv.startAnimation(animation_logo);


        Intent intent = getIntent();
        int frogSpecies = intent.getIntExtra("frog_src", Frog.SPECIES_BASIC);
        int currentFrogKey = intent.getIntExtra("frog_key", 0);
        iv.setImageResource(frogSpecies);

        Location currentUserLocation = getCurrentUserLocation();

        Toast.makeText(this, ""+currentUserLocation.getLatitude(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, ""+currentFrogKey, Toast.LENGTH_SHORT).show();

        //보내야 할 데이터 : oneFrogSet , currentUserData   :  종료할 때 공유중인 데이터 삭제하고 끝
        //답장 받으면 데이터 삭제하고 끝 // 내 개구리 DB에서 삭제하기.
        //TODO 서버에 DB만들기
        //1. Firebase Database에 nickName, profileUrl을 저장
        //firebase DB관리자 객체 소환
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        //'oneFrogSet'라는 이름의 자식 노드 참조 객체 얻어오기
        DatabaseReference profileRef= firebaseDatabase.getReference("oneFrogSet");

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
    public void finish() {

        //


        super.finish();
    }


    public void delFrogDB(int currentFrogKey){
        SQLiteDatabase database_exercise = openOrCreateDatabase("exerciseDB.db", MODE_PRIVATE, null); ;
        database_exercise.execSQL("DELETE FROM exercise_data_set " +
                "WHERE frog_key =" + "'" + currentFrogKey + "'");
    }
}