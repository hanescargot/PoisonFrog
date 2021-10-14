package com.pyrion.game.poison_frog.trade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pyrion.game.poison_frog.R;
import com.pyrion.game.poison_frog.center.FragmentCenter;
import com.pyrion.game.poison_frog.data.Frog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ActivityCamera extends AppCompatActivity {
    ImageView ivFrog;
    View ivNoFrog;

    View alertNewFrogName;
    AlertDialog frogNameAlertDialog;
    EditText newFrogNameEditText;
    LayoutInflater inflater;
    int newFrogType;
    LocationManager locationManager;
    Gson gson;
    Float minDistance= null; //key_index , distance
    int minDistanceIndex= -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ivFrog = findViewById(R.id.iv);
        ivNoFrog = findViewById(R.id.iv_nofrog);

        locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        inflater = getLayoutInflater();
        gson = new GsonBuilder().setPrettyPrinting().create();
        ///todo 가까운 개구리가 있는지 체크
        if(isNearFrog()){
            //있으면 개구리 생성
            setRandomFrogSpecies();
        }else{
            //todo 없으면 근처에 개구리가 없네요라는 알림 표시
            ivFrog.setVisibility(View.INVISIBLE);
            ivNoFrog.setVisibility(View.VISIBLE);
        }


    }
    Type doubleType;
    double[] roadFrogLatLng = new double[2];
    public boolean isNearFrog(){
        //가장 가까운 개구리만 보여줌
        doubleType = new TypeToken<double[]>() {}.getType();

        Location userLocation = getCurrentUserLocation();
        for(int index =0; index<5; index++){
            roadFrogLatLng = gson.fromJson(getPref("locations" + index), doubleType);
            Toast.makeText(this, ""+getPref("locations"+index), Toast.LENGTH_SHORT).show();
            Location frogLocation = new Location("");

            if(roadFrogLatLng[0]!=-1) {
                frogLocation.setLatitude(roadFrogLatLng[0]);
                frogLocation.setLongitude(roadFrogLatLng[1]);

                if(minDistance==null || minDistance>userLocation.distanceTo(frogLocation)){
                    minDistanceIndex=index;
                    minDistance=userLocation.distanceTo(frogLocation);
                }
            }
        }
        if(minDistanceIndex != -1 && minDistance!=null && minDistance<10){
            //10미터 이내에 개구리가 있다.
            return true;
        }else{
            return false;
        }
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

    public void setRandomFrogSpecies() {
        newFrogType = Frog.getFrogSpecies(new Random().nextInt(6));//0~6
        Glide.with(this).load(newFrogType).into(ivFrog);

        ivFrog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityCamera.this, Frog.getStringSpecies(newFrogType)+" 포획 성공", Toast.LENGTH_SHORT).show();
                //이름짓기 얼럿
                showNewFrogNameAlert();
                ivFrog.setVisibility(View.GONE);
                //todo 데이터에서 개구리 없애기
                roadFrogLatLng[0] = (double)-1;
                roadFrogLatLng[1] = (double)-1;
                setPref("locations" + minDistanceIndex, gson.toJson(roadFrogLatLng));;
            }
        });
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



    void showNewFrogNameAlert(){
        AlertDialog.Builder frogNameBuilder = new AlertDialog.Builder(this);
        alertNewFrogName= inflater.inflate(R.layout.alert_set_new_frog_name, null);
        frogNameBuilder.setView(alertNewFrogName);

        ImageView newFrogNameConfirmButton = alertNewFrogName.findViewById(R.id.main_buy_button);
        newFrogNameEditText = alertNewFrogName.findViewById(R.id.new_frog_name_edit_text);

        frogNameAlertDialog = frogNameBuilder.create();
        frogNameAlertDialog.setCanceledOnTouchOutside(true);
        frogNameAlertDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#00000000")));
        frogNameAlertDialog.setCancelable(false);
        frogNameAlertDialog.show();

        newFrogNameConfirmButton.setOnClickListener(newFrogNameConfirmButtonOnClickListener);
    }


    View.OnClickListener newFrogNameConfirmButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            frogNameAlertDialog.cancel();

            String newFrogName = newFrogNameEditText.getText().toString();
            Toast.makeText(ActivityCamera.this, newFrogName + "생성", Toast.LENGTH_SHORT).show();

            // add 새 개구리 DB
            addNewFrogDB(newFrogName);

            finish();
        }
    };

    void addNewFrogDB(String newFrogName){
        SQLiteDatabase database_frog;
        database_frog = openOrCreateDatabase("frogsDB.db", MODE_PRIVATE, null);
        database_frog.execSQL("INSERT INTO frogs_data_set(house_type, creator_name, frog_name, frog_state, frog_species, frog_size, frog_power) VALUES('"
                + Frog.HOUSE_TYPE_LENT + "','"
                + FragmentCenter.getUserName() + "','"
                + newFrogName + "','"
                + Frog.STATE_ALIVE + "','"
                + newFrogType + "','"
                + Frog.SIZE_DEFAULT + "','"
                + Frog.POWER_DEFAULT + "')"
        );
    }
}