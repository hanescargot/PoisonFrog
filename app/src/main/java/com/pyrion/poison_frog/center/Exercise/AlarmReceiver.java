package com.pyrion.poison_frog.center.Exercise;

import android.app.Activity;
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
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.pyrion.poison_frog.MainActivity;
import com.pyrion.poison_frog.R;
import com.pyrion.poison_frog.data.Frog;

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

    private static String CHANNEL_ID = "frog9708";
    private static String CHANNEL_NAME = "PoisonFrog";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.frogKey = intent.getIntExtra("currentFrogKey", 1);

        getExerciseDB();
        delExerciseDB();
        showNotification();

        updateFrogDB();

        if(getSelectedFrogKey() == frogKey) {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(i);
        }

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
        int exercisePoint =  (int)( (currentTime - startTime)/60000 )*itemEffect;
        Log.i("time", exercisePoint+"");

        int newFrogPower = currentFrogPower + exercisePoint;
        return newFrogPower;
    }


    public void showNotification() {
        //반영 안되면 앱 지웠다가 다시하기

        NotificationCompat.Builder builder =  null;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =  new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

            //사운드 설정 (검색하기 힘듬)
            Uri uri = Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.frog_sound_short);
            channel.setSound(uri, new AudioAttributes.Builder().build());

            notificationManager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        } else{
            builder = new NotificationCompat.Builder(context);
        }

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, frogKey, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder
                .setContentTitle("Poison Frog")
                .setContentText(frogName+" : 운동 끝났다 개굴")
                .setContentIntent(pendingIntent)
                .setSound(Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.frog_sound_short))
                .setAutoCancel(true);

        // 알림창 아이콘
         builder.setSmallIcon(R.drawable.main_frog_jelly);
         builder.setLargeIcon(BitmapFactory.decodeResource( context.getResources(), R.drawable.main_frog_jelly));

        // 알림창 실행
        Notification notification = builder.build();
        notificationManager.notify(9708,notification);
    }
}
