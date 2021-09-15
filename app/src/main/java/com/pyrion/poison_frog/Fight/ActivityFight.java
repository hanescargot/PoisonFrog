package com.pyrion.poison_frog.Fight;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pyrion.poison_frog.R;
import com.pyrion.poison_frog.data.Frog;
import com.pyrion.poison_frog.data.OneFrogSet;

import java.util.Random;

public class ActivityFight extends AppCompatActivity {

    Boolean userTurn = true;
    Random random = new Random();

    OneFrogSet userFrogSet;
    int userFrogHP = 0; //size
    int userFrogMP = 0; //power

    int enemyMaxHP = 0; //size
    int enemyMaxMP = 0; //power
    int enemyFrogHP = 0; //size
    int enemyFrogMP = 0; //power
    int enemyFrogSpecies = Frog.SPECIES_BASIC;

    TextView fightLog;
    ScrollView logScrollView;
    StringBuffer logStringBuffer = new StringBuffer();

    ProgressBar userProgressSize, userProgressPower,  enemyProgressSize, enemyProgressPower;
    TextView userSize, userPower,  enemySize, enemyPower;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);


        ImageView myFrogIv = findViewById(R.id.my_frog_iv);
        ImageView enemyFrogIv = findViewById(R.id.enemy_iv);
        fightLog = findViewById(R.id.fight_log);
        logScrollView = findViewById(R.id.log_scroll);

        userProgressSize = findViewById(R.id.my_progress_size);
        userProgressPower = findViewById(R.id.my_progress_power);
        enemyProgressSize= findViewById(R.id.enemy_progress_size);
        enemyProgressPower= findViewById(R.id.enemy_progress_power);

        userSize = findViewById(R.id.my_size);
        userPower = findViewById(R.id.my_power);
        enemySize= findViewById(R.id.enemy_size);
        enemyPower= findViewById(R.id.enemy_power);

//        Animation animation_logo = AnimationUtils.loadAnimation(this, R.anim.frog_finder);
//        myFrogIv.startAnimation(animation_logo);


        int userFrogKey = getIntent().getIntExtra("currentFrogKey", 1);
        userFrogSet = getUserFrogData(userFrogKey);
        userFrogHP = userFrogSet.getFrogSize();
        userFrogMP = userFrogSet.getFrogPower();


        //기존 개구리와 비례하는 랜덤 개구리 생성
        int userSumMpHp = userFrogHP + userFrogMP;
        int extraHP= random.nextInt(10)+1;
        int extraMP= random.nextInt(10)+1;
        int rateHP= random.nextInt(100)+1;
        int rateMP= 100 - rateHP;
        enemyFrogSpecies = Frog.getFrogSpecies(random.nextInt(Frog.FROG_SPECIES_COUNT)); //0~6(7)
        enemyFrogHP = (int)(  ((userSumMpHp/100.0) *rateHP ) + (userFrogHP/100.0*extraHP)  );
        enemyFrogMP = (int)(  ((userSumMpHp/100.0) *rateMP ) + (userFrogMP/100.0*extraMP)  );
        if(random.nextInt(30)==0){
            enemyFrogHP = (int)(  (userSumMpHp/100.0 *rateHP ) - userFrogHP/100.0*extraHP  );
            enemyFrogMP = (int)(  (userSumMpHp/100.0 *rateMP ) - userFrogMP/100.0*extraMP  );
        }

        //Minimum HP MP
        enemyFrogHP = Math.max(enemyFrogHP,1);
        enemyFrogMP = Math.max(enemyFrogMP,1);
        //set Max HP MP
        enemyMaxHP = enemyFrogHP;
        enemyMaxMP = enemyFrogMP;

        userProgressSize.setMax(userFrogHP);
        userProgressPower.setMax(userFrogMP);
        enemyProgressSize.setMax(enemyFrogHP);
        enemyProgressPower.setMax(enemyFrogMP);

        TextView userFrogName = findViewById(R.id.my_frog_name);
        TextView enemyFrogName = findViewById(R.id.enemy_name);
        userFrogName.setText(userFrogSet.getFrogName());
        enemyFrogName.setText(Frog.getStringSpecies(enemyFrogSpecies));

        Glide.with(this).load(userFrogSet.getFrogSrc()).into(myFrogIv);
        Glide.with(this).load(enemyFrogSpecies).into(enemyFrogIv);


        setStatuses();
    }

    @Override
    public void onBackPressed() {
       //뒤로 나가면 안됨.
    }

    @Override
    protected void onDestroy() {
        //상대 개구리의 체력이 0이 아니라면 무조건 내 개구리는 죽는다.
        
        if(enemyFrogHP != 0){
            changeFrogDB("frog_state", Frog.STATE_DEATH);
        }

        super.onDestroy();
    }

    private void changeFrogDB(String dataName , int data) {
        //frog_size, frog_power
         SQLiteDatabase database_frog = this.openOrCreateDatabase("frogsDB.db", this.MODE_PRIVATE, null);
         database_frog.execSQL("UPDATE frogs_data_set SET "
                +dataName + "=" + data
                +" WHERE frog_key =" + userFrogSet.getFrogKey()
        );
    }

    void changeUserMoney(int diff){
        SQLiteDatabase database_user = this.openOrCreateDatabase("userDB.db", this.MODE_PRIVATE, null);
        Cursor cursor_user = database_user.rawQuery("SELECT * FROM user_data_set", null);
        cursor_user = database_user.rawQuery("SELECT * FROM user_data_set", null);
        cursor_user.moveToNext();
        int currentUserMoney = cursor_user.getInt(cursor_user.getColumnIndex("user_money"));
        
        database_user.execSQL("UPDATE user_data_set SET"
                +" user_money = " + (currentUserMoney + diff)
        );
        
    }

    public OneFrogSet getUserFrogData(int frogKey) {
        SQLiteDatabase database_frog = this.openOrCreateDatabase("frogsDB.db", this.MODE_PRIVATE, null);
        Cursor cursor_frog = database_frog.rawQuery("SELECT * FROM frogs_data_set", null);//WHERE절이 없기에 모든 레코드가 검색됨
        OneFrogSet currentFrogSet = new OneFrogSet();
        try {//when there is selected frog data
            cursor_frog = database_frog.rawQuery("SELECT * FROM frogs_data_set WHERE frog_key = " + frogKey, null);//WHERE절이 없기에 모든 레코드가 검색됨
            cursor_frog.moveToNext();//[레코드:row]로 커서이동

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
            builder.setMessage("개구리 불러오기 오류").setPositiveButton("OK", null).show();
            this.onBackPressed();
        }
        return currentFrogSet;
    }


    public void onSpecialSkill(View view) {
        if(!userTurn)return;
        if(userFrogMP==0){
            addLog("MP가 부족하여 일반공격 합니다.");
            onNormalSkill(null);
            return;
        }

        if (random.nextInt(10)==0){
            addLog("특수 공격에 실패하였다.");
        }
        else{
            int damage = (enemyMaxHP / 50) * (random.nextInt(17)+1);
            damage = Math.max(damage,1);
            enemyFrogHP -= damage;
            userFrogMP -= damage;
            addLog("[데미지 -" + damage + "]");
            addLog("특수 공격에 성공하였다.");
            if(!setStatuses()){
                //죽음
                return;
            }
        }

        userTurn = false;
        startEnemySkill();
    }

    private Boolean setStatuses() {
        //my frog check
        userFrogHP = Math.max(userFrogHP, 0);
        userFrogMP = Math.max(userFrogMP, 0);

        //enemy frog check
        enemyFrogHP = Math.max(enemyFrogHP, 0);
        enemyFrogMP = Math.max(enemyFrogMP, 0);

        //set UI
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M ) {
            userProgressSize.setProgress(userFrogHP, true);
            userProgressPower.setProgress(userFrogMP, true);
            enemyProgressSize.setProgress(enemyFrogHP, true);
            enemyProgressPower.setProgress(enemyFrogMP, true);
        }else{
            userProgressSize.setProgress(userFrogHP);
            userProgressPower.setProgress(userFrogMP);
            enemyProgressSize.setProgress(enemyFrogHP);
            enemyProgressPower.setProgress(enemyFrogMP);
        }

        enemySize.setText(enemyFrogHP+"/"+enemyMaxHP);
        enemyPower.setText(enemyFrogMP+"/"+enemyMaxMP);
        userSize.setText(userFrogHP+"/"+userFrogSet.getFrogSize());
        userPower.setText(userFrogMP+"/"+userFrogSet.getFrogPower());

        //check death
        if(userFrogHP == 0){
            //패배
            addLog(userFrogSet.getFrogName()+"가 죽었습니다.");
            fightFinish(false, false);
            return false;
        }
        if(enemyFrogHP == 0){
            //승리
            addLog("["+userFrogSet.getFrogName()+" 승리]");
            fightFinish(false, true);
            return false;
        }
        return true;
    }

    public void onNormalSkill(View view) {
        if(!userTurn)return;
        if (random.nextInt(10)==0){
            addLog("일반 공격에 실패하였다.");
        }
        else{

            int damage = (enemyMaxHP / 50) * (random.nextInt(10)+1);
            damage = Math.max(damage,1);
            enemyFrogHP -= damage;
            addLog("[데미지 -" + damage + "]");
            addLog("일반 공격에 성공하였다.");
        }
        if(!setStatuses()){
            //죽음
            return;
        }
        userTurn = false;
        startEnemySkill();
    }

    public void onRunSkill(View view) {
        if(!userTurn)return;
        if (random.nextInt(3)==0){
            //도망 성공
            fightFinish( true, false);
            return;
        };
        //실패
        addLog("[도망 실패]");
        userTurn = false;
        startEnemySkill();
    }

    public void startEnemySkill() {
        if (random.nextInt(15)==0){
            addLog("상대의 공격이 실패하였다.");
            userTurn = true;
            return;
        }
        Boolean specialSkill = enemyFrogMP>0 ? !(random.nextInt(20)==0) : false;
        if(specialSkill){
            int damage =  userFrogSet.getFrogSize() / 50 * (random.nextInt(20)+1);
            damage = Math.max(damage,1);
            userFrogHP -= damage;
            enemyFrogMP -= damage;

            addLog("[데미지 -" + damage + "]");
            addLog("상대의 특수 공격에 상처입었습니다.");
        }else{

            if(random.nextInt(30)==0){
                //도망가기
                addLog("상대가 도망쳤습니다.");
                fightFinish(true, true);
                return;
            }else{
                //일반공격
                int damage = userFrogSet.getFrogSize() / 50 * (random.nextInt(10)+1);
                damage = Math.max(damage,1);
                userFrogHP -= damage;

                addLog("[데미지 -" + damage + "]");
                addLog("상대의 일반 공격에 상처입었습니다.");
            }


        }
        userTurn = true;
        if(!setStatuses()){
            //죽음
            return;
        }
    }

    public void fightFinish(boolean isRun, boolean isWin){
        userTurn = false;
        
        //상대가 도망간 경우
        if(isRun && isWin){
            enemyFrogHP = 0;
            int diffMoney = (enemyMaxHP + enemyMaxMP)/50 * (random.nextInt(10)+1);
            changeUserMoney(diffMoney);
            showFightNoticeAlert(ENEMY_RUN, diffMoney);
        }

        //내가 도망간 경우
        if(isRun && !isWin){
            enemyFrogHP = 0;
            showFightNoticeAlert(USER_RUN, 0);
        }
        //상대가 이긴 경우
        if(!isRun && !isWin){
            changeFrogDB("frog_state", Frog.STATE_DEATH);
            showFightNoticeAlert(USER_DEATH, 0);
        }
        //내가 이긴 경우
        if(!isRun && isWin){
            int diffSize = (enemyMaxHP/50) * (random.nextInt(20)+10);
            int diffPower = (enemyMaxMP/50) * (random.nextInt(20)+10);
            diffSize = Math.max(diffSize, 1);
            diffPower = Math.max(diffPower, 1);

            changeFrogDB("frog_size", (userFrogSet.getFrogSize() + diffSize) );
            changeFrogDB("frog_power", (userFrogSet.getFrogPower() + diffPower) );

            showResultAlertDialog(diffSize, diffPower);
        }

    }

    public void addLog(String msg){
        logStringBuffer.append(msg + "\n\n");
        fightLog.setText(logStringBuffer.toString());
        logScrollView.fullScroll(View.FOCUS_DOWN);
    }

    void showResultAlertDialog(int diffSize, int diffPower) {

        AlertDialog fightResultAlertDialog;
        LayoutInflater inflater = LayoutInflater.from(this);
        AlertDialog.Builder frogSellBuilder = new AlertDialog.Builder(this);
        View fightResultView = inflater.inflate(R.layout.alert_firg_result, null);
        frogSellBuilder.setView(fightResultView);
        fightResultAlertDialog = frogSellBuilder.create();


        //다이얼로그의 바깥쪽 영역을 터치했을때 다이얼로그가 사라지않음
        fightResultAlertDialog.setCanceledOnTouchOutside(false);
        //뒤로가기 버튼 이후
        fightResultAlertDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    fightResultAlertDialog.dismiss();
                    finish();
                }
                return true;
            }
        });

        //button setting
        TextView frogName = fightResultView.findViewById(R.id.frog_name);
        ImageView bigFrogSrc = fightResultView.findViewById(R.id.iv);

        TextView tvSizePrice = fightResultView.findViewById(R.id.tv_size);
        TextView tvPowerPrice = fightResultView.findViewById(R.id.tv_power);

        TextView okBtn = fightResultView.findViewById(R.id.tv_sell);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //게임 실제로 끝나는 부분
                fightResultAlertDialog.dismiss();
                finish();
            }
        });

        bigFrogSrc.setImageResource(userFrogSet.getFrogSrc());
        frogName.setText(userFrogSet.getFrogName());


        tvSizePrice.setText("+"+diffSize + "Size");
        tvPowerPrice.setText("+"+diffPower + "Power");

        fightResultAlertDialog.show();
    }

    final int USER_DEATH = 0;
    final int USER_RUN = 1;
    final int ENEMY_RUN = 2;
    void showFightNoticeAlert(int result, int diffMoney){

        AlertDialog fightNoticeAlertDialog;
        LayoutInflater inflater = LayoutInflater.from(this);
        AlertDialog.Builder frogSellBuilder = new AlertDialog.Builder(this);
        View fightNoticeView = inflater.inflate(R.layout.alert_fight_notice, null);
        frogSellBuilder.setView(fightNoticeView);
        fightNoticeAlertDialog = frogSellBuilder.create();

        //배경 투명하게
        fightNoticeAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        //다이얼로그의 바깥쪽 영역을 터치했을때 다이얼로그가 사라짐
//        fightNoticeAlertDialog.setCanceledOnTouchOutside(true);
        fightNoticeAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                fightNoticeAlertDialog.dismiss();
                finish();
            }
        });
        fightNoticeAlertDialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    fightNoticeAlertDialog.dismiss();
                    finish();
                }
                return true;
            }
        });

        //button setting
        TextView tv = fightNoticeView.findViewById(R.id.tv);
        ImageView iv = fightNoticeView.findViewById(R.id.iv);
        LinearLayout payButton = fightNoticeView.findViewById(R.id.pay_button);
        TextView price = fightNoticeView.findViewById(R.id.price);

        iv.setImageResource(R.drawable.main_paper_money);//TODo
        price.setText("+"+diffMoney);
        if(result == USER_RUN){
            iv.setImageResource(userFrogSet.getFrogSrc());
            tv.setText("도망치기에 성공했습니다.");
            payButton.setVisibility(View.INVISIBLE);
        }
        if(result == USER_DEATH){
            iv.setImageResource(userFrogSet.getFrogSrc());
            tv.setText(userFrogSet.getFrogName()+"가 죽었습니다.");
            payButton.setVisibility(View.INVISIBLE);
        }
        fightNoticeAlertDialog.show();
    }
}