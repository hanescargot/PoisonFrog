package com.pyrion.game.poison_frog.trade;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pyrion.game.poison_frog.R;
import com.pyrion.game.poison_frog.center.FragmentCenter;
import com.pyrion.game.poison_frog.data.Frog;

import java.util.Random;

public class ActivityCamera extends AppCompatActivity {
    ImageView ivFrog;

    View alertNewFrogName;
    AlertDialog frogNameAlertDialog;
    EditText newFrogNameEditText;
    LayoutInflater inflater;
    int newFrogType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        inflater = getLayoutInflater();

        ///todo 가까운 개구리가 있는지 체크
        if(isNearFrog()){
            //있으면 개구리 생성
            setRandomFrogSpecies();
        }else{
            //todo 없으면 근처에 개구리가 없네요라는 알림 표시

        }


    }

    public boolean isNearFrog(){
        //가장 가까운 개구리만 보여줌
        return true;
    }

    public void setRandomFrogSpecies() {
        newFrogType = Frog.getFrogSpecies(new Random().nextInt(6));//0~6
        ivFrog = findViewById(R.id.iv);
        Glide.with(this).load(newFrogType).into(ivFrog);

        ivFrog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityCamera.this, Frog.getStringSpecies(newFrogType)+"포획 성공", Toast.LENGTH_SHORT).show();
                //이름짓기 얼럿
                showNewFrogNameAlert();
                ivFrog.setVisibility(View.GONE);
                //todo 데이터에서 개구리 없애기
            }
        });
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