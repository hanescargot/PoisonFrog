package com.pyrion.game.poison_frog.trade;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pyrion.game.poison_frog.R;
import com.pyrion.game.poison_frog.center.FragmentCenter;
import com.pyrion.game.poison_frog.data.Frog;
import com.pyrion.game.poison_frog.data.OneFrogSet;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ActivityCamera extends AppCompatActivity {
    ImageView ivFrog;
    View ivNoFrog;

    int MAX_DISTANCE = 100; //100m 이내의 개구리만 카메라에 보여진다.

    View alertNewFrogName;
    AlertDialog frogNameAlertDialog;
    EditText newFrogNameEditText;
    LayoutInflater inflater;
    int newFrogType;
    LocationManager locationManager;
    Gson gson;
    Float minDistance= null; //key_index , distance
    int minDistanceIndex= -1;

    FirebaseFirestore firebaseFirestore;
    boolean isFromServer = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ivFrog = findViewById(R.id.iv);
        ivNoFrog = findViewById(R.id.iv_nofrog);


        locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        inflater = getLayoutInflater();
        gson = new GsonBuilder().setPrettyPrinting().create();
        firebaseFirestore = FirebaseFirestore.getInstance();
        setFirebaseDB();





    }
    Type doubleType;
    double[] roadFrogLatLng = new double[2];
    public boolean isNearFrog(){
        //가장 가까운 개구리만 보여줌
        doubleType = new TypeToken<double[]>() {}.getType();

        Location userLocation = getCurrentUserLocation();
        for(int index =0; index<5; index++){
            roadFrogLatLng = gson.fromJson(getPref("locations" + index), doubleType);
            Location frogLocation = new Location("");


            try {
                if(roadFrogLatLng[0]!=-1) {
                    frogLocation.setLatitude(roadFrogLatLng[0]);
                    frogLocation.setLongitude(roadFrogLatLng[1]);

                    if(minDistance==null || minDistance>userLocation.distanceTo(frogLocation)){
                        minDistanceIndex=index;
                        minDistance=userLocation.distanceTo(frogLocation);
                    }
                }
            }catch (Exception e){
                //New frog
                Toast.makeText(this, "개구리가 모두 숨어있습니다. 지도를 한번 봐주세요", Toast.LENGTH_SHORT).show();
            }

        }
        if(minDistanceIndex != -1 && minDistance!=null && minDistance<MAX_DISTANCE){
            //100미터 이내에 개구리가 있다.
            return true;
        }else{
            return false;
        }
    }

    OneFrogSet nearOneFrogSet = new OneFrogSet();
    public boolean isNearServerFrog(){
        //가장 가까운 개구리만 보여줌
        Location userLocation = getCurrentUserLocation();
        for(int index = 0; index< serverRoadFrogLocations.size(); index++){
            Location serverFrogLocation = new Location("");
            serverFrogLocation.setLatitude(serverRoadFrogsLatLng[0]);
            serverFrogLocation.setLongitude(serverRoadFrogsLatLng[1]);

            if(minDistance==null || minDistance>userLocation.distanceTo(serverFrogLocation)){
                minDistanceIndex=index;
                minDistance=userLocation.distanceTo(serverFrogLocation);
            }

        }

        if(minDistanceIndex != -1 && minDistance!=null && minDistance<10){
            //10미터 이내에 개구리가 있다.
            nearOneFrogSet = serverRoadOneFrogSets.get(minDistanceIndex);
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

            }
        });
    }

    @Override
    public void finish() {
        if(isFromServer){
            //todo 서버로부터 지우기
            firebaseFirestore.collection("road_frogs").document(serverKeyList.get(minDistanceIndex))
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("jjj", "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("jjj", "Error deleting document", e);
                        }
                    });
        }else{
            //todd shared preference 데이터에서 개구리 없애기
//        잡던 안잡던 사라져야함 .
            roadFrogLatLng[0] = (double)-1;
            roadFrogLatLng[1] = (double)-1;
            setPref("locations" + minDistanceIndex, gson.toJson(roadFrogLatLng));;
        }

        super.finish();
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

    void addNewServerFrogDB(){
        SQLiteDatabase database_frog;
        database_frog = openOrCreateDatabase("frogsDB.db", MODE_PRIVATE, null);
        database_frog.execSQL("INSERT INTO frogs_data_set(" +
                "house_type," +
                " creator_name," +
                " frog_name," +
                " frog_state," +
                " frog_species," +
                " frog_size," +
                " frog_power) VALUES('"
                + Frog.HOUSE_TYPE_LENT + "','"
                + nearOneFrogSet.getCreatorName() + "','"
                + nearOneFrogSet.getFrogName() + "','"
                + nearOneFrogSet.getFrogState() + "','"
                + nearOneFrogSet.getFrogSpecies() + "','"
                + nearOneFrogSet.getFrogSize() + "','"
                + nearOneFrogSet.getFrogPower() + "')"
        ); }

    ArrayList<Location> serverRoadFrogLocations = new ArrayList<>();
    ArrayList<String> serverKeyList = new ArrayList<>();
    double[] serverRoadFrogsLatLng = new double[2];

    ArrayList<OneFrogSet> serverRoadOneFrogSets = new ArrayList<>();
    public void setFirebaseDB(){
        //todo 서버에서 사용자들의 개구리 가져오기

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
//                                location[0]   oneFrogSet[1] 여긴 지도여서 location 만 필요함

                                serverKeyList.add( document.getId() );
                                String locationString = (String)user.get("location");
                                serverRoadFrogsLatLng =  gson.fromJson(locationString, double[].class);
                                location.setLatitude(serverRoadFrogsLatLng[0]);
                                location.setLongitude(serverRoadFrogsLatLng[1]);
                                serverRoadFrogLocations.add(location);
                                Log.i("!!import", locationString);

                                String frogSetString = (String)user.get("frog_set");
                                serverRoadOneFrogSets.add(gson.fromJson(frogSetString, OneFrogSet.class));

                            }


                            ///서버에가까운 개구리가 있는지 체크
                            if(isNearServerFrog()){
                                ivFrog.setImageResource(nearOneFrogSet.getFrogSrc());
                                isFromServer = true;

                                ivFrog.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(ActivityCamera.this, nearOneFrogSet.getFrogSpecies()+" 포획 성공", Toast.LENGTH_SHORT).show();
                                        //서버 정보대로 DB 추가
                                        addNewServerFrogDB();
                                        finish();
                                    }
                                });
                            }else if(isNearFrog()){
                                //있으면 개구리 생성
                                setRandomFrogSpecies();
                            }else{
                                //todo 없으면 근처에 개구리가 없네요라는 알림 표시
                                ivFrog.setVisibility(View.INVISIBLE);
                                ivNoFrog.setVisibility(View.VISIBLE);
                            }
                        } else {
                        }
                    }
                });


    }
}