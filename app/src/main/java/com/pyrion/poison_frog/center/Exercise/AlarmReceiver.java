package com.pyrion.poison_frog.center.Exercise;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.pyrion.poison_frog.MainActivity;
import com.pyrion.poison_frog.R;
import com.pyrion.poison_frog.data.Frog;
import com.pyrion.poison_frog.data.Item;
import com.pyrion.poison_frog.data.OneFrogSet;

import java.util.Date;

//리시버는 4대컴포넌트 - 매니페스트에 등록
public class AlarmReceiver extends BroadcastReceiver {
    Context context;

    SQLiteDatabase database_frog, database_exercise;
    Cursor cursor_exercise;

    int frogKey;
    String frogName;
    int currentFrogPower;
    int itemEffect;
    Long startTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.frogKey = intent.getIntExtra("currentFrogKey", 1);

        getExerciseDB();
        delExerciseDB();
        showNotification();

        updateFrogDB();

        if(getSelectedFrogKey() == frogKey) {
         //화면에 보여지고 있는 상태임으로 화면 업데이트
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        Toast.makeText(context, "운동 완료", Toast.LENGTH_SHORT).show();
    }

    void getExerciseDB() {
        database_exercise = context.openOrCreateDatabase("exerciseDB.db", context.MODE_PRIVATE, null);
        cursor_exercise = database_exercise.rawQuery("SELECT * FROM exercise_data_set WHERE frog_key = " + frogKey, null);
        cursor_exercise.moveToNext();//[레코드:row]로 커서이동
        int countFrogDB = cursor_exercise.getCount();
        try {//when there is selected frog data
            frogName = cursor_exercise.getString(cursor_exercise.getColumnIndex("frog_name"));
            currentFrogPower = cursor_exercise.getInt(cursor_exercise.getColumnIndex("current_frog_power"));
            itemEffect = cursor_exercise.getInt(cursor_exercise.getColumnIndex("item_effect"));
            startTime = cursor_exercise.getLong(cursor_exercise.getColumnIndex("start_time"));

        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("운동 데이터 오류").setPositiveButton("OK", null).show();
        }
    }

    void delExerciseDB() {
        database_exercise = context.openOrCreateDatabase("exerciseDB.db", context.MODE_PRIVATE, null);
        database_exercise.execSQL("DELETE FROM exercise_data_set " +
                "WHERE frog_key =" + "'" + frogKey + "'");
    }

    void updateFrogDB() {
        database_frog = context.openOrCreateDatabase("frogsDB.db", context.MODE_PRIVATE, null);
        database_frog.execSQL("UPDATE frogs_data_set SET"
                + " frog_state =" + Frog.STATE_ALIVE
                + ", frog_power =" + newFrogPower()

                + " WHERE frog_key =" + frogKey
        );
    }

    int getSelectedFrogKey(){
        SQLiteDatabase database_user;
        database_user = context.openOrCreateDatabase("userDB.db", context.MODE_PRIVATE, null);
        Cursor cursor_user = database_user.rawQuery("SELECT * FROM user_data_set", null);

        cursor_user.moveToNext();
        int selectedFrogKey = cursor_user.getInt(cursor_user.getColumnIndex("selected_frog_key"));
        return selectedFrogKey;

    }


    private int newFrogPower() {
        long currentTime = System.currentTimeMillis();
        int exercisePoint =  (int)( (currentTime - startTime)/60000 );
        Log.i("time", exercisePoint+"");

        int newFrogPower = currentFrogPower + exercisePoint;
        return newFrogPower;
    }

    public void showNotification() {
        NotificationManager manager;
        NotificationCompat.Builder builder;

        builder = null;
        manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                    new NotificationChannel("9708", "Poison Frog", NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder = new NotificationCompat.Builder(context, "9708");
             }else{
            builder = new NotificationCompat.Builder(context);
        }
        //알림창 제목
         builder.setContentTitle("Poison Frog");

        // 알림창 메시지
         builder.setContentText(frogName+" 운동을 끝내었습니다.");

        // 알림창 아이콘
         builder.setSmallIcon(R.drawable.main_frog_jelly);
         Notification notification = builder.build();

        // 알림창 실행
         manager.notify(9708,notification);
    }
}
