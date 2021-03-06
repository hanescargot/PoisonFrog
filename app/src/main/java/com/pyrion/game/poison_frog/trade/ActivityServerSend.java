package com.pyrion.game.poison_frog.trade;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pyrion.game.poison_frog.R;
import com.pyrion.game.poison_frog.data.Frog;
import com.pyrion.game.poison_frog.data.OneFrogSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ActivityServerSend extends AppCompatActivity {

    LocationManager locationManager;
    FirebaseFirestore firebaseFirestore;
    DocumentReference docRef;
    String sharedFrogLatLngString;
    String sharedFrogSetString;
    String firebaseKey;
    int nextFrogKey;
    int currentFrogKey;
    EventListener<DocumentSnapshot> eventListener;

    ListenerRegistration listenerRegistration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_send);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ImageView iv = findViewById(R.id.iv);

        Animation animation_logo = AnimationUtils.loadAnimation(this, R.anim.frog_finder);
        iv.startAnimation(animation_logo);

        Intent intent = getIntent();
        int frogSpecies = intent.getIntExtra("frog_src", Frog.SPECIES_BASIC);
        currentFrogKey = intent.getIntExtra("frog_key", 0);
        nextFrogKey = intent.getIntExtra("next_frog_key", 0);

        iv.setImageResource(frogSpecies);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        double[] sharedFrogLatLng = new double[2];
        Location currentUserLocation = getCurrentUserLocation();
        sharedFrogLatLng[0] = currentUserLocation.getLatitude();
        sharedFrogLatLng[1] = currentUserLocation.getLongitude();
        sharedFrogLatLngString = gson.toJson(sharedFrogLatLng);
        sharedFrogSetString = gson.toJson(getUserFrogData(currentFrogKey));
//        //????????? ??? ????????? : oneFrogSet , currentUserData   :  ????????? ??? ???????????? ????????? ???????????? ???
//        //?????? ????????? ????????? ???????????? ??? // ??? ????????? DB?????? ????????????.
//        //firebase DB????????? ?????? ??????
//        //DB
        Random random = new Random();
        Map<String, Object> sendData = new HashMap<>();
        sendData.put("frog_set", sharedFrogSetString);
        sendData.put("location", sharedFrogLatLngString);
        firebaseKey = sharedFrogLatLngString+currentFrogKey+random.nextInt(1000);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("road_frogs").document(firebaseKey)
                .set(sendData);

//        todo ?????? ????????? ???????????? (????????? ???????????? ???????????? ?????? ????????? )????????? DB ???????????? finish()
       // ???????????? ?????? ??????

        eventListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //listen fail
                    Log.i("gg", (snapshot.getData()+"error!!!"+e));
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
//                    Toast.makeText(ActivityServerSend.this, ""+snapshot.getData(), Toast.LENGTH_SHORT).show();
                    Log.i("gggg", "????????? ??????");
                }

                else {
//                  Log.d(TAG, "Current data: null");
                    Toast.makeText(ActivityServerSend.this, "????????? ?????? ??????", Toast.LENGTH_SHORT).show();
                    Log.i("gggg", "?????????");
                    //????????? ???????????? ????????? db??????
                    delFrogDB(currentFrogKey);
                    noFrog = true;
                    finish();
                }
            }
        };
        docRef = firebaseFirestore.collection("road_frogs").document(firebaseKey);
        listenerRegistration = docRef.addSnapshotListener(eventListener);
    }


    boolean noFrog = false;
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
    public void finish( ) {
        listenerRegistration.remove();
        if(!noFrog) {
            //??????????????? ?????? DB ???????????? ?????? ??????????????? ???????????? ??? ????????? ???????????? ?????? ??????
            Log.i("gg", "?????????");
            firebaseFirestore.collection("road_frogs").document(firebaseKey)
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

        }

        super.finish();
    }

    public void delFrogDB(int frogKey){
        SQLiteDatabase database_frog = openOrCreateDatabase("frogsDB.db", MODE_PRIVATE, null);
        database_frog.execSQL("DELETE FROM frogs_data_set "+
                "WHERE frog_key =" +"'"+ frogKey +"'");

        SQLiteDatabase database_user = openOrCreateDatabase("userDB.db", MODE_PRIVATE, null);
        Cursor cursor_user = database_user.rawQuery("SELECT * FROM user_data_set", null);
        cursor_user.moveToNext();
        database_user.execSQL("UPDATE user_data_set SET"
                +" selected_frog_key = " + nextFrogKey
        );
    }


    public OneFrogSet getUserFrogData(int frogKey) {
        SQLiteDatabase database_frog = this.openOrCreateDatabase("frogsDB.db", this.MODE_PRIVATE, null);
        Cursor cursor_frog = database_frog.rawQuery("SELECT * FROM frogs_data_set", null);//WHERE?????? ????????? ?????? ???????????? ?????????
        OneFrogSet currentFrogSet = new OneFrogSet();
        try {//when there is selected frog data
            cursor_frog = database_frog.rawQuery("SELECT * FROM frogs_data_set WHERE frog_key = " + frogKey, null);//WHERE?????? ????????? ?????? ???????????? ?????????
            cursor_frog.moveToNext();//[?????????:row]??? ????????????

            int frog_key = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_key"));
            int house_type = cursor_frog.getInt(cursor_frog.getColumnIndex("house_type"));
            String creator_name = cursor_frog.getString(cursor_frog.getColumnIndex("creator_name"));
            String frog_name = cursor_frog.getString(cursor_frog.getColumnIndex("frog_name"));
            int frog_state = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_state"));
            int frog_species = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_species"));
            int frog_size = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_size"));
            int frog_power = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_power"));

            currentFrogSet = new OneFrogSet(
                    frog_key,
                    house_type,
                    creator_name,
                    frog_name,
                    frog_state,
                    frog_species,
                    frog_size,
                    frog_power
            );
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("????????? ???????????? ??????").setPositiveButton("OK", null).show();
            this.onBackPressed();
        }
        return currentFrogSet;
    }
}