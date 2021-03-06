package com.pyrion.game.poison_frog.center;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

//from temp
import android.content.Intent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.pyrion.game.poison_frog.Fight.ActivityMatch;
import com.pyrion.game.poison_frog.center.ItemStore.ActivityItemStore;
import com.pyrion.game.poison_frog.data.OneItemSet;
import com.pyrion.game.poison_frog.center.Exercise.AlarmReceiver;
import com.pyrion.game.poison_frog.center.fly_game.FlyGameActivity;
import com.pyrion.game.poison_frog.data.Frog;
import com.pyrion.game.poison_frog.data.Item;
import com.pyrion.game.poison_frog.data.OneFrogSet;
import com.pyrion.game.poison_frog.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class FragmentCenter extends Fragment {

    private static String userName = "null_userName";
    SQLiteDatabase database_user, database_frog, database_item, database_exercise;
    Cursor cursor_user, cursor_frog, cursor_item, cursor_exercise;

    ArrayList<OneItemSet> itemDataArrayList = new ArrayList<>();

    OneFrogSet currentFrogSet;

    //Main FragMents
    AlertDialog isBuyAlertDialog;
    LayoutInflater inflater;
    View alertNewFrogName;
    EditText newFrogNameEditText;
    AlertDialog frogNameAlertDialog;
    View alertIsBuyNewFrog;

    //clicked settings
    View mainMoneyIconSet;
    ImageView mainHouseIcon, mainFoodIcon, mainDumbbellIcon,  mainGamePlayIcon, mainSellIcon;
    ImageView mainEraserIcon;
    ImageView mainFrogImageView, chefHatIconView, healthCareIconView;

    //etc
    Toast toast;
    Random random = new Random();
    EditText foodInputEditText;
    TextView logTextView, moneyStringTextView;
    StringBuffer logStringBuffer = new StringBuffer();
    String logString = "";
    View menuListView, mainBackgroundView;
    ScrollView logScrollView;
    Stack<String> originFoodNameStack = new Stack<>();
    InputMethodManager imm;

    //current data
    int currentUserMoney = 10000000; //todo edit it to 1100
    private int selectedFrogKey = 1;
    int frogTouchedCount = 0;

    int exerciseTime;
    int sumPrice;

    String[] deadFrogMsgs;
    String[] soledFrogMsgs;
    String[] poisonFoods;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater= getActivity().getLayoutInflater();

        deadFrogMsgs = getResources().getStringArray(R.array.dead_frog_msg);
        soledFrogMsgs = getResources().getStringArray(R.array.soled_frog_msg);
        poisonFoods = getResources().getStringArray(R.array.poison_food);

        //Item data settings
        database_item = getActivity().openOrCreateDatabase("itemDB.db", getActivity().MODE_PRIVATE, null);
        database_item.execSQL("CREATE TABLE IF NOT EXISTS item_data_set("
                + "item_name VARCHAR(40),"
                + "item_explain TEXT,"
                + "current_item_price INTEGER,"
                + "current_level INTEGER,"
                + "max_level INTEGER,"
                + "upgrade_price_times DOUBLE,"
                + "item_case VARCHAR(40))"
        );

        //Frog data settings
        database_frog = getActivity().openOrCreateDatabase("frogsDB.db", getActivity().MODE_PRIVATE, null);
        database_frog.execSQL("CREATE TABLE IF NOT EXISTS frogs_data_set("
                + "frog_key INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "house_type INTEGER,"
                + "creator_name VARCHAR(40),"
                + "frog_name VARCHAR(40),"
                + "frog_state INTEGER,"
                + "frog_species INTEGER,"
                + "frog_size DOUBLE,"
                + "frog_power DOUBLE)"
        );

        //User data settings
        database_user = getActivity().openOrCreateDatabase("userDB.db", getActivity().MODE_PRIVATE, null);
        database_user.execSQL("CREATE TABLE IF NOT EXISTS user_data_set("
                + "user_name String,"
                + "selected_frog_key INTEGER,"
                + "user_money INTEGER)"
        );

        database_exercise = getActivity().openOrCreateDatabase("exerciseDB.db", getActivity().MODE_PRIVATE, null);
        database_exercise.execSQL("CREATE TABLE IF NOT EXISTS exercise_data_set("
                + "frog_key INTEGER,"
                + "frog_name VARCHAR(40),"
                + "current_frog_power INTEGER,"
                + "item_effect INTEGER,"
                + "start_time LONG)"
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_page, container, false);

        foodInputEditText = view.findViewById(R.id.food_name_input);
        logTextView = view.findViewById(R.id.food_log);
        logScrollView = view.findViewById(R.id.log_scroll);
        mainFrogImageView = view.findViewById(R.id.main_frog_icon);
        chefHatIconView = view.findViewById(R.id.chef_hat);
        menuListView = view.findViewById(R.id.menu_list);
        mainBackgroundView = view.findViewById(R.id.main_background_clickable);
        healthCareIconView = view.findViewById(R.id.health);
        moneyStringTextView = view.findViewById(R.id.money_string);

        ///icons only for click listener
        mainHouseIcon = view.findViewById(R.id.main_house_icon);
        mainFoodIcon = view.findViewById(R.id.main_food_icon);
        mainDumbbellIcon = view.findViewById(R.id.main_dumbbell_icon);
        mainMoneyIconSet = view.findViewById(R.id.main_money_icon);
        mainGamePlayIcon = view.findViewById(R.id.main_game_play_icon);
        mainSellIcon = view.findViewById(R.id.main_sell_icon);
        mainEraserIcon = view.findViewById(R.id.main_eraser_iocn);

        imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE); //getActivity(). ????????????.
        foodInputEditText.setOnEditorActionListener(foodInputActionListener);

//        updateSelectedFrogState(currentFrogSet.getFrogState());
        return view;

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i("Lifecycle:","Fragment onResume 1page");
        // set current data from DB
        getCurrentUserDB();
        getCurrentFrogDB();
        getCurrentItemDB();

        // set userDB from current and set money UI
        moneyStringTextView.setText((currentUserMoney)+"");
        updateSelectedFrogState(currentFrogSet.getFrogState()); //set frog UI
        getActivity().getIntent().putExtra("fragment_navigation", 1);
    }

    private void changeExerciseState( double currentTime) {
        //Frog DB
        updateSelectedFrogState(Frog.STATE_EXERCISE);

        //Exercise DB
        int exerciseEffect =  itemDataArrayList.get(Item.Name.EXERCISE_EFFECT).getCurrentLevel();//?????? ?????? *  ??????
        database_exercise.execSQL("INSERT INTO exercise_data_set(frog_key, frog_name, current_frog_power, item_effect, start_time) VALUES('"
                + currentFrogSet.getFrogKey() + "','"
                + currentFrogSet.getFrogName() + "','"
                + currentFrogSet.getFrogPower() + "','"
                + exerciseEffect + "','"
                + currentTime + "')"
        );

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //click listener settings
        chefHatIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(menuListView.getVisibility()==View.VISIBLE){
                    menuListView.setVisibility(View.GONE);
                    foodInputEditText.setVisibility(View.VISIBLE);

                    //keyboard popup
                    foodInputEditText.requestFocus();
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    return;
                }
                hideFoodInputEditText();
            }
        });

        healthCareIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //????????? ????????????
                if(currentFrogSet.getFrogState() == Frog.STATE_DEATH){
                    int resurrectionPrice = (int) (getCurrentFrogPrice()*0.7);
                    addLogString("[?????????: "+resurrectionPrice+"$]");
                    if(currentUserMoney >=resurrectionPrice){
                        changeCurrentMoney(-resurrectionPrice);
                        resurrection();
                        return;
                    }
                    showToastString("??? ??????");
                    addLogString("[?????? ???????????? ????????? ???????????????.]");

                }else if(currentFrogSet.getFrogState() == Frog.STATE_SOLD){
                    showToastString("????????? ???????????? ??????");
                    addLogString("[???????????? ?????? ????????? ?????????.]");
                }else{
                    showToastString("???????????? ?????? ?????????");
                }
            }
        });

        mainGamePlayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //game play
                if(currentFrogSet.getFrogState() == Frog.STATE_SOLD){
                    showToastString("?????? ?????????");
                    addLogString("????????? ????????? ???????????? ????????????.");
                }
                else if(currentFrogSet.getFrogState() == Frog.STATE_DEATH){
                    showToastString("?????? ?????????");
                    addLogString("?????? ???????????? ???????????? ??????.");
                }
                else if(currentFrogSet.getFrogState() == Frog.STATE_EXERCISE){
                    showToastString("?????? ?????????");
                    addLogString("???????????? ???????????? ????????? ???????????????. ");
                }
                else{
                    Intent intent = new Intent(getActivity(), ActivityMatch.class);
                    intent.putExtra("currentFrogKey", currentFrogSet.getFrogKey());
                    intent.putExtra("currentFrogSpecies", currentFrogSet.getFrogSpecies());
                    getActivity().startActivity(intent);
                }
            }
        });

        mainSellIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentFrogSet.getFrogSize()<40){
                    addLogString("[???????????? ?????? ????????? ?????? ????????????.]");
                    showToastString("?????? ??????");
                    return;

                }else if(currentFrogSet.getFrogState() == Frog.STATE_SOLD){
                    addLogString("[?????? ???????????? ????????? ????????????.]");
                    showToastString("?????? ?????????");
                    return;

                }else{
                    //??????
                    showHouseSellAlertDialog();

                }

            }
        });


        mainHouseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityItemStore.class);
                intent.putExtra("user_money", currentUserMoney);
                getActivity().startActivity(intent);
            }
        });

        mainFoodIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentFrogSet.getFrogState() == Frog.STATE_SOLD){
                    showToastString("???????????? ?????????");
                    addLogString("[????????? ???????????? ?????? ?????? ??????.]");
                    return;
                }
                if(currentFrogSet.getFrogState() == Frog.STATE_DEATH){
                    showToastString("???????????? ?????????");
                    addLogString("[?????? ???????????? ?????? ?????? ??????.]");
                    return;
                }
                if(currentFrogSet.getFrogState() == Frog.STATE_EXERCISE){
                    showToastString("???????????? ?????????");
                    addLogString("[???????????? ???????????? ?????? ?????? ??????.]");
                    addLogString("???...???...?????????...");
                    return;
                }
                Intent intent = new Intent(getActivity(), FlyGameActivity.class);
                intent.putExtra("currentFrogKey", currentFrogSet.getFrogKey());
                intent.putExtra("currentFrogSrc", currentFrogSet.getFrogSrc());
                intent.putExtra("currentFrogSize", currentFrogSet.getFrogSize());
                intent.putExtra("currentFrogName", currentFrogSet.getFrogName());
                intent.putExtra("currentFrogSpecies", currentFrogSet.getFrogSpecies());

                int foodItem = itemDataArrayList.get(Item.Name.FOOD_NUMBER).getCurrentLevel();
                int foodEffect = itemDataArrayList.get(Item.Name.FOOD_EFFECT).getCurrentLevel();

                intent.putExtra("food_item", foodItem);
                intent.putExtra("food_effect", foodEffect);
                getActivity().startActivity(intent);
            }
        });

        mainDumbbellIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentFrogSet.getFrogState() == Frog.STATE_SOLD){
                    showToastString("?????? ?????????");
                    addLogString("[????????? ???????????? ?????? ??????.]");
                    return;
                }
                if(currentFrogSet.getFrogState() == Frog.STATE_DEATH){
                    showToastString("?????? ?????????");
                    addLogString("[?????? ???????????? ?????? ??????.]");
                    return;
                }
                if(currentFrogSet.getFrogState() == Frog.STATE_EXERCISE){
                    cancelExercise();

                    return;
                }

                showTimerAlert();
            }
        });

        mainMoneyIconSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentFrogSet.getFrogState() == Frog.STATE_SOLD){
                showToastString("????????? ?????????");
                addLogString("[?????? ???????????? ??????.]");
                return;
            }
            if(currentFrogSet.getFrogState() == Frog.STATE_DEATH){
                showToastString("????????? ?????????");
                addLogString("[?????? ???????????? ?????? ??????.]");
                return;
            }
            if(currentFrogSet.getFrogState() == Frog.STATE_EXERCISE){
                showToastString("????????? ?????????");
                addLogString("[???????????? ???????????? ?????? ??????.]");
                addLogString("?????????...?????????...");
                return;
            }
            changeCurrentMoney(+1);
            showToastString("+1$");
            }
        });

        mainFrogImageView.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
                if (currentFrogSet.getFrogState() == Frog.STATE_SOLD) {
                    if (currentUserMoney < 100) {
                        showToastString("?????? ?????????");
                        addLogString("[?????? ????????????..]");
                        return;
                    }
                    alertDialogIsBuy();
                    return;
                }
                if (currentFrogSet.getFrogState() == Frog.STATE_DEATH) {
                    addLogString(deadFrogMsgs[random.nextInt(8)]);
                    return;
                }
                if (currentFrogSet.getFrogState() == Frog.STATE_EXERCISE) {
                    cancelExercise();
                    return;
                }
                frogTouchedCount++;
                showToastString("????????? ??????");
                if (logStringBuffer.length() == 0) {
                    logTextView.setText("");
                }
                switch (frogTouchedCount) {
                    case 1:
                        addLogString("???????");
                        break;
                    case 2:
                        addLogString("??? ?????? ????????????.");
                        break;
                    case 3:
                        addLogString("?????????.");
                        break;
                    case 4:
                        addLogString("????????????. ?????? ????????????");
                        break;
                    case 5:
                        addLogString("?????? ??? ?????????.");
                        break;
                    default: {
                       if (random.nextInt(2) == 1) {
                            updateSelectedFrogState(Frog.STATE_DEATH);
                            addLogString("[????????? ??????]");
                            showToastString("????????? ??????");
                        } else {
                            addLogString("[????????? ????????? ????????????.]");
                            frogTouchedCount = random.nextInt(6);
                        }
                    }
                    break;
                }
            }
        });

        mainFrogImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showFrogDataAlert();
                return false;
            }
        });

        mainEraserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logStringBuffer= new StringBuffer();
                logTextView.setText("Ready...");
                logScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        mainBackgroundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFoodInputEditText();
            }
        });

    }//onCreated

    private void showRefuseAlert() {
        AlertDialog.Builder refuseBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View alertRefuseView = inflater.inflate(R.layout.alert_not_enough_money, null);
        refuseBuilder.setView(alertRefuseView);
        AlertDialog refuseDialog = refuseBuilder.create();

        //?????????????????? ????????? ????????? ??????????????? ?????????????????? ???????????? ??????
        refuseDialog.setCanceledOnTouchOutside(true);
        //?????? ????????????
        refuseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        ImageView cancelButton = alertRefuseView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refuseDialog.cancel();
            }
        });

        alertRefuseView.findViewById(R.id.free_btn).setVisibility(View.INVISIBLE);
        TextView tv = alertRefuseView.findViewById(R.id.tv);
        tv.setTextSize(24);
        tv.setText("?????? ???????????? \n???????????? ??????????????????.");

        refuseDialog.show();
    }

    private void cancelExercise() {
        AlarmManager alarmManager;
        alarmManager= (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        showToastString("?????? ??????");
        updateSelectedFrogState(Frog.STATE_ALIVE);

        Intent intent= new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(getActivity(), selectedFrogKey, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

        //point
        powerPointUpdate();

    }

    void powerPointUpdate(){
        long currentTime = System.currentTimeMillis();

        database_exercise = getActivity().openOrCreateDatabase("exerciseDB.db", getActivity().MODE_PRIVATE, null);
        cursor_exercise = database_exercise.rawQuery("SELECT * FROM exercise_data_set WHERE frog_key = " + selectedFrogKey, null);
        cursor_exercise.moveToNext();//[?????????:row]??? ????????????
        long startTime = cursor_exercise.getLong(cursor_exercise.getColumnIndex("start_time"));
        int currentFrogPower = cursor_exercise.getInt(cursor_exercise.getColumnIndex("current_frog_power"));
        int itemEffect = cursor_exercise.getInt(cursor_exercise.getColumnIndex("item_effect"));

        int exercisePoint = (int)( (currentTime - startTime)/60000 )*itemEffect;
        Log.i("time", currentTime+"");
        Log.i("time", exercisePoint+"");
        int newFrogPower = currentFrogPower + exercisePoint;

        currentFrogSet.setFrogPower( newFrogPower);
        updateCurrentFrogDB();

        database_exercise.execSQL("DELETE FROM exercise_data_set " +
                "WHERE frog_key =" + "'" + selectedFrogKey + "'");

        addLogString("[+"+exercisePoint+" ??? ??????]");
    }


    void showHouseSellAlertDialog(){
        //sell house ?????? ???????????????
        AlertDialog isHouseSellAlertDialog;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        AlertDialog.Builder eggSaveOrSellBuilder = new AlertDialog.Builder(getActivity());
        View alertSaveOrSellView = inflater.inflate(R.layout.alert_is_sell_frog, null);
        eggSaveOrSellBuilder.setView(alertSaveOrSellView);
        isHouseSellAlertDialog = eggSaveOrSellBuilder.create();

        //?????????????????? ????????? ????????? ??????????????? ?????????????????? ???????????? ?????????
        isHouseSellAlertDialog.setCanceledOnTouchOutside(false);

        //button setting
        TextView frogName = alertSaveOrSellView.findViewById(R.id.frog_name);
        ImageView bigFrogSrc = alertSaveOrSellView.findViewById(R.id.iv);

        TextView tvSizePrice = alertSaveOrSellView.findViewById(R.id.tv_size);
        TextView tvPowerPrice= alertSaveOrSellView.findViewById(R.id.tv_power);

        TextView sellBtn = alertSaveOrSellView.findViewById(R.id.tv_sell);
        TextView tvSumPrice= alertSaveOrSellView.findViewById(R.id.sum);
        ImageView cancelBtn = alertSaveOrSellView.findViewById(R.id.cancel_button);

        bigFrogSrc.setImageResource(currentFrogSet.getFrogSrc());
        frogName.setText(currentFrogSet.getFrogName());

        sumPrice = 0;
        if(currentFrogSet.getFrogState() == Frog.STATE_ALIVE ||
                currentFrogSet.getFrogState() == Frog.STATE_EXERCISE){
            tvSizePrice.setText(currentFrogSet.getFrogSize()+"???");
            tvPowerPrice.setText(currentFrogSet.getFrogPower()+"???");

            sumPrice = currentFrogSet.getFrogSize()+currentFrogSet.getFrogPower();
        }
        if(currentFrogSet.getFrogState() == Frog.STATE_DEATH){
            int sizePrice = currentFrogSet.getFrogSize()/10;
            tvSizePrice.setText(sizePrice+"???");

            int powerPrice = currentFrogSet.getFrogPower()/10;
            tvPowerPrice.setText(powerPrice+"???");

            sumPrice = sizePrice+powerPrice;
        }
        tvSumPrice.setText(sumPrice+"???");

        sellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHouseSellAlertDialog.cancel();

                changeCurrentMoney(sumPrice);

                //Sell Message
                if(currentFrogSet.getFrogState() == Frog.STATE_DEATH) {
                    addLogString("[????????? ????????? ????????? ?????????...]");
                    showToastString("????????? ?????? ??????");
                }else{
                    showToastString("?????? ??????");
                    addLogString(soledFrogMsgs[random.nextInt(soledFrogMsgs.length)]);
                }
                addLogString("[???????????? ?????????????????????.]");

                //really sell
                updateSelectedFrogState(Frog.STATE_SOLD);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHouseSellAlertDialog.cancel();
            }
        });

        //?????????????????? ????????? ?????????
        isHouseSellAlertDialog.show();
    }

    void showFrogDataAlert() {
        inflater = LayoutInflater.from( getActivity() );
        AlertDialog.Builder frogDataDialogBuilder = new AlertDialog.Builder(getActivity());
        View viewFrogData = inflater.inflate(R.layout.alert_frog_data, null);

        ((TextView)viewFrogData.findViewById(R.id.frog_name)).setText(currentFrogSet.getFrogName());
        ((TextView)viewFrogData.findViewById(R.id.creator_name)).setText("?????????: "+currentFrogSet.getCreatorName());
        ((TextView)viewFrogData.findViewById(R.id.frog_property)).setText(
                "??????: "+Frog.getStringSpecies( currentFrogSet.getFrogSpecies() ));
        ((TextView)viewFrogData.findViewById(R.id.frog_size)).setText("??????: "+currentFrogSet.getFrogSize());
        ((TextView)viewFrogData.findViewById(R.id.frog_power)).setText("???: "+currentFrogSet.getFrogPower());


        frogDataDialogBuilder.setView(viewFrogData);

        AlertDialog frogDataAlertDialog = frogDataDialogBuilder.create();
        frogDataAlertDialog.setCanceledOnTouchOutside(true);
        frogDataAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        frogDataAlertDialog.show();
    }

    void showTimerAlert() {
        int exerciseTimeMax = itemDataArrayList.get(Item.Name.EXERCISE_TIME).getCurrentLevel();
        exerciseTime = exerciseTimeMax;

        inflater = LayoutInflater.from( getActivity() );
        AlertDialog.Builder timerAlertBuilder = new AlertDialog.Builder(getActivity());
        View viewTimerAlert = inflater.inflate(R.layout.alert_exercse_time_setting, null);

        TextView setTime =viewTimerAlert.findViewById(R.id.tv_time);
        setTime.setText(exerciseTime+"");

        viewTimerAlert.findViewById(R.id.up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                timerUpButton
                if(exerciseTime != exerciseTimeMax){
                    exerciseTime +=1;
                    setTime.setText(exerciseTime+"");
                }else{
                    showToastString("??????"+exerciseTimeMax+"??? ????????????");
                }
            }
        });
        viewTimerAlert.findViewById(R.id.down_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                timerDownButton
                if(exerciseTime >1){
                    exerciseTime -=1;
                    setTime.setText(exerciseTime+"");
                }else{
                    showToastString("?????? 1??? ?????? ??????");
                }
            }
        });
        timerAlertBuilder.setView(viewTimerAlert);
        AlertDialog frogDataAlertDialog = timerAlertBuilder.create();

        frogDataAlertDialog.setCanceledOnTouchOutside(true);
        frogDataAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        frogDataAlertDialog.show();


        viewTimerAlert.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frogDataAlertDialog.cancel();

                long currentTime = System.currentTimeMillis();
                showToastString("?????? ??????");
                changeExerciseState(currentTime);

                AlarmManager alarmManager;
                alarmManager= (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

                Intent intent= new Intent(getActivity(), AlarmReceiver.class);
                intent.putExtra("currentFrogKey", currentFrogSet.getFrogKey());
                intent.putExtra("currentFrogSrc", currentFrogSet.getFrogSpecies());
                PendingIntent pendingIntent= PendingIntent.getBroadcast(getActivity(), selectedFrogKey, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (currentTime+(exerciseTime*60000)), pendingIntent);
                }else{
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                        alarmManager.setExact( AlarmManager.RTC_WAKEUP, (currentTime+(exerciseTime*60000)), pendingIntent);
                    }else{
                        alarmManager.set( AlarmManager.RTC_WAKEUP, (currentTime+(exerciseTime*60000)), pendingIntent);
                    }
                }


            }
        });
    }


    EditText.OnEditorActionListener foodInputActionListener = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            //???????????? ?????? ???????????? ????????????
            //event.keypad?????? ??????????????? ?????? ?????? ???????????? ???????????? ????????????// actionID==EditorInfo.IME_ACTION_SEARCH
            //actionID ??? ????????? ????????? ?????????
            //?????? ?????? ????????? ??????
            if(event == null){
                //?????? ????????? ???
                String newFoodName = v.getText().toString();

                if(newFoodName.length()>0) {
                    if(logStringBuffer.length()==0) {
                        logTextView.setText("");
                    }
                    originFoodNameStack.add(newFoodName);

                    if(currentFrogSet.getFrogState() == Frog.STATE_DEATH){
                        addLogString("[?????? ???????????? "+newFoodName+" ?????? ??????.]");
                        showToastString("?????? ??????");
                        hideFoodInputEditText();
                        return false;
                    }
                    if(currentFrogSet.getFrogState() == Frog.STATE_SOLD){
                        addLogString("[????????? ???????????? "+newFoodName+" ?????? ??????.]");
                        showToastString("????????? ??????");
                        hideFoodInputEditText();
                        return false;
                    }
                    if(currentFrogSet.getFrogState() == Frog.STATE_EXERCISE){
                        addLogString("[???????????? ???????????? "+newFoodName+" ?????? ??????.]");
                        addLogString("??? ???????????? ?????????...?");
                        showToastString("?????? ??????");
                        hideFoodInputEditText();
                        return false;
                    }
                    showToastString("???????????? "+newFoodName+" ??????");
                    int randomFoodPoint = (random.nextInt(10)-1);//-1~9


                    for(String poisonFood: poisonFoods){
                        if(newFoodName.equals(poisonFood)){
                            addLogString("????????????... ??? ??????!!!");
                            if(random.nextBoolean()){
                                if(random.nextInt(10)==1){
                                    changeFrogSpecies(Frog.STATE_ALIVE);
                                }else{
                                    changeFrogSize(currentFrogSet.getFrogSize()/8+randomFoodPoint);
                                    addLogString("[???????????? ????????? ?????? ???]");
                                }

                            }else{

                                if(random.nextInt(10)==1){
                                    changeFrogSpecies(Frog.STATE_DEATH);
                                    addLogString("[????????? ??????]");
                                    addLogString("?????? ????????? ????????? ????????? ???????????? ???????????????...");
                                }else{
                                    updateSelectedFrogState(Frog.STATE_DEATH);
                                    addLogString("[????????? ??????]");
                                    showToastString("????????? ??????");
                                }
                            }

                            hideFoodInputEditText();
                            return false;
                        }
                    }

                    changeFrogSize(randomFoodPoint);
                    showNewFoodLogSet(newFoodName, randomFoodPoint);
                    v.setText("");
                }
                hideFoodInputEditText();
            }
            return false;
        }

    };

    void changeFrogSpecies(int frogState){
        addLogString("[????????? ?????? ??????]");
        int newFrogSpecies = Frog.getFrogSpecies( random.nextInt(Frog.FROG_SPECIES_COUNT) );
        while(newFrogSpecies == currentFrogSet.getFrogSpecies()){
            newFrogSpecies = Frog.getFrogSpecies( random.nextInt(Frog.FROG_SPECIES_COUNT) );
        }
        currentFrogSet.setFrogSpecies( newFrogSpecies );
        updateSelectedFrogState( frogState );
    }

    int getCurrentFrogPrice(){
        return currentFrogSet.getFrogSize()+currentFrogSet.getFrogPower();
    }


    void showNewFoodLogSet(String newFoodName, int like){
        addLogString("[?????? ??????: "+newFoodName+"]");
        addLogString("[?????? ???...]");
        if(like < 0){
            addLogString("??? ???????????? ?????????.");
            addLogString("[???????????? ???????????? ?????????]");
        }else{
            addLogString("???????????? ????????? "+newFoodName+"?????????.");
        }
//        addLogString("_???????????????"+"????????????.");
//        addLogString("?????? ?????? ?????????"+"_??? ???"+ "???????????????.");
//        addLogString("??? ?????????" +"_??? ??????"+"???.");
//        addLogString("?????????"+"???.");
    }

    public void showToastString(String text){
        if(toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(
                getActivity(),
                text,
                Toast.LENGTH_SHORT
        );
        toast.setGravity(Gravity.CENTER_VERTICAL, 0 , 200);
        toast.show();
    }


    public void hideFoodInputEditText() {
        menuListView.setVisibility(View.VISIBLE);
        foodInputEditText.setVisibility(View.GONE);
        foodInputEditText.setText("");
        imm.hideSoftInputFromWindow(foodInputEditText.getWindowToken(), 0);
    }

    public void addLogString(String string){
        logStringBuffer.append(string + "\n\n");
        logTextView.setText(logStringBuffer.toString());
        logScrollView.fullScroll(View.FOCUS_DOWN);
    }

    void resurrection(){
        addLogString("[???????????? ?????????????????????.]");
        if(random.nextBoolean()){
            addLogString("???????????? ??????????????????.");
        }else{
            addLogString("???????????? ????????? ??????.");
        }
        showToastString("????????? ??????");
        updateSelectedFrogState(Frog.STATE_ALIVE);
    }

    public void changeCurrentMoney(int diff){
        currentUserMoney += diff;
        updateUserDB();
    }

    public void changeFrogSize(int diff) {
        currentFrogSet.setFrogSize(currentFrogSet.getFrogSize() + diff);
        updateCurrentFrogDB();
        updateFrogLayout(mainFrogImageView,  currentFrogSet.getFrogSize(), false);
        if(diff>0) {
            showToastString("+" + diff + "??????");
        }else if(diff == 0) {
            showToastString("???????????? ??????");
        }else if(diff<0){
            showToastString(diff+"??????");
        }
    }

    public void changeFrogPower(int diff){
        currentFrogSet.setFrogPower(currentFrogSet.getFrogPower() + diff);
        updateCurrentFrogDB();
    }

    private void alertDialogIsBuy() {
        //TODO ?????? ?????? ????????? ?????? ????????? ????????????
        //AlertDialog
        AlertDialog.Builder isBuyBuilder = new AlertDialog.Builder(getActivity());
        alertIsBuyNewFrog= inflater.inflate(R.layout.alert_is_buy_new_frog, null);
        isBuyBuilder.setView(alertIsBuyNewFrog);

        //??????????????? ??? ????????? ???????????? AlertDialog ????????? ????????? ????????? ??????!
        isBuyAlertDialog= isBuyBuilder.create();

        //?????????????????? ????????? ????????? ??????????????? ?????????????????? ???????????? ?????????
        isBuyAlertDialog.setCanceledOnTouchOutside(false);

        //?????????????????? ????????? ?????????
        isBuyAlertDialog.show();
        isBuyAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        View payButton = alertIsBuyNewFrog.findViewById(R.id.pay_button);
        View cancelButton = alertIsBuyNewFrog.findViewById(R.id.cancel_button);

        payButton.setOnClickListener(payFrogButtonClickListener);
        cancelButton.setOnClickListener(cancelBuyFrogButton);

    }//TODO buy new frog  ?????? ????????????

    View.OnClickListener payFrogButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isBuyAlertDialog.cancel();

            showNewFrogNameAlert();
        }
    };

    View.OnClickListener cancelBuyFrogButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isBuyAlertDialog.cancel();
        }
    };

    View.OnClickListener newFrogNameConfirmButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            frogNameAlertDialog.cancel();

            String newFrogName = newFrogNameEditText.getText().toString();
            showToastString(newFrogName + "??????");
            addLogString("[???????????? ?????? ?????? ???????????????.]");
            changeCurrentMoney(-100);

            currentFrogDataReset();
            currentFrogSet.setFrogName(newFrogName);
            updateCurrentFrogCreatorDB(); //new userName
            updateSelectedFrogState(Frog.STATE_ALIVE);
        }
    };

    public void currentFrogDataReset(){
        currentFrogSet.setHouseType(Frog.HOUSE_TYPE_LENT);
        currentFrogSet.setCreatorName(userName);
        currentFrogSet.setFrogName(Frog.FROG_NAME_NULL);
        currentFrogSet.setFrogState(Frog.STATE_ALIVE);
        currentFrogSet.setFrogSpecies(Frog.SPECIES_BASIC);
        currentFrogSet.setFrogSize(Frog.SIZE_DEFAULT);
        currentFrogSet.setFrogPower(Frog.POWER_DEFAULT);

    }

    public void updateSelectedFrogState(int frogState){
        switch (frogState){
            case Frog.STATE_ALIVE:
                currentFrogSet.setFrogState(Frog.STATE_ALIVE);
                Glide.with(this).load( currentFrogSet.getFrogSpecies() ).into(mainFrogImageView);
                updateFrogLayout(mainFrogImageView, currentFrogSet.getFrogSize(), false);
                break;
            case Frog.STATE_SOLD:
                currentFrogSet.setFrogState(Frog.STATE_SOLD);
                mainFrogImageView.setImageResource(R.drawable.main_gift);
                updateFrogLayout(mainFrogImageView, 160, true);
                break;
            case Frog.STATE_DEATH:
                currentFrogSet.setFrogState(Frog.STATE_DEATH);
                mainFrogImageView.setImageResource(R.drawable.main_dead_frog);
                updateFrogLayout(mainFrogImageView, currentFrogSet.getFrogSize(), false);
                frogTouchedCount = 0;
                break;

            case Frog.STATE_EXERCISE:
                currentFrogSet.setFrogState(Frog.STATE_EXERCISE);
                Glide.with(getActivity()).load( R.drawable.dancing_pepe ).into(mainFrogImageView);
                updateFrogLayout(mainFrogImageView, currentFrogSet.getFrogSize(), false);
                frogTouchedCount = 0;
                break;
        }
        updateCurrentFrogDB();
        mainFrogImageView.requestLayout();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    void getCurrentFrogDB(){
        database_frog = getActivity().openOrCreateDatabase("frogsDB.db", getActivity().MODE_PRIVATE, null);
        cursor_frog = database_frog.rawQuery("SELECT * FROM frogs_data_set", null);//WHERE?????? ????????? ?????? ???????????? ?????????
        int countFrogDB = cursor_frog.getCount();
        if(countFrogDB != 0) {
            try {//when there is selected frog data
                cursor_frog = database_frog.rawQuery("SELECT * FROM frogs_data_set WHERE frog_key = " + selectedFrogKey, null);//WHERE?????? ????????? ?????? ???????????? ?????????
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("????????? ???????????? ??????").setPositiveButton("OK", null).show();
                getActivity().onBackPressed();
            }
        }
        else {
            //TODO ?????? movetToNext()???????????? ???????????? ????????? ???????????? UNBOXING MODE
            // ?????? ?????? ???????????? ????????? ?????? ?????? ?????? ???????????? ???????????? ???????????? ?????? ?????? ?????????
            int frog_key = selectedFrogKey;
            int house_type = Frog.HOUSE_TYPE_LENT;
            String creator_name = userName;
            String frog_name = Frog.FROG_NAME_NULL;
            int frog_state = Frog.STATE_ALIVE;
            int frog_species = Frog.SPECIES_BASIC;
            int frog_size = Frog.SIZE_DEFAULT;
            int frog_power = Frog.POWER_DEFAULT;
            database_frog.execSQL("INSERT INTO frogs_data_set(house_type, creator_name, frog_name, frog_state, frog_species, frog_size, frog_power) VALUES('"
                    + house_type + "','"
                    + creator_name + "','"
                    + frog_name + "','"
                    + frog_state + "','"
                    + frog_species + "','"
                    + frog_size + "','"
                    + frog_power + "')"
            );
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
        }
    }

    void updateCurrentFrogDB(){
        //only current Frog data
        database_frog.execSQL("UPDATE frogs_data_set SET"
                +" frog_name =" + "'"+ currentFrogSet.getFrogName() +"'"
                +", frog_state =" + currentFrogSet.getFrogState()
                +", frog_species =" + currentFrogSet.getFrogSpecies()
                +", frog_size =" + currentFrogSet.getFrogSize()
                +", frog_power =" + currentFrogSet.getFrogPower()

                +" WHERE frog_key =" + currentFrogSet.getFrogKey()
        );
    }

    void updateCurrentFrogCreatorDB(){
        ////new userName
        database_frog.execSQL("UPDATE frogs_data_set SET"
                +" creator_name =" + "'"+ currentFrogSet.getCreatorName() +"'"
                +" WHERE frog_key =" + currentFrogSet.getFrogKey()
        );
    }

    void getCurrentUserDB(){
        cursor_user = database_user.rawQuery("SELECT * FROM user_data_set", null);
        int countUserDB = cursor_user.getCount();
        if(countUserDB != 0) {
            //?????? ???????????? ????????? ???
            try {
                cursor_user.moveToNext();
                userName = cursor_user.getString(cursor_user.getColumnIndex("user_name"));
                selectedFrogKey = cursor_user.getInt(cursor_user.getColumnIndex("selected_frog_key"));
                currentUserMoney = cursor_user.getInt(cursor_user.getColumnIndex("user_money"));
            } catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("????????? ????????? ???????????? ??????").setPositiveButton("OK", null).show();
                getActivity().onBackPressed();
            }
        }
        if(countUserDB == 0){
            //unboxing;
            showNewUserNameAlert();
        }

        //TODO Adapter??? ?????? ??? ????????????
    }
    void showNewFrogNameAlert(){
        AlertDialog.Builder frogNameBuilder = new AlertDialog.Builder(getActivity());
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

    void showNewUserNameAlert(){
        AlertDialog.Builder userNameBuilder = new AlertDialog.Builder(getActivity());
        View alertNewUserName= inflater.inflate(R.layout.alert_set_new_frog_name, null);
        userNameBuilder.setView(alertNewUserName);

        TextView tv = alertNewUserName.findViewById(R.id.tv);
        tv.setText("????????? ????????? ????????? ?????????.");
        ImageView newUserNameConfirmButton = alertNewUserName.findViewById(R.id.main_buy_button);
        EditText newUserNameEditText = alertNewUserName.findViewById(R.id.new_frog_name_edit_text);
        newUserNameEditText.setHint("????????? ??????");

        AlertDialog newUserNameAlertDialog = userNameBuilder.create();
        newUserNameAlertDialog.setCanceledOnTouchOutside(true);
        newUserNameAlertDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#00000000")));

        newUserNameConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = newUserNameEditText.getText().toString();
                newUserNameAlertDialog.cancel();

                //default user data
                cursor_user = database_user.rawQuery("SELECT * FROM user_data_set", null);
                database_user.execSQL("INSERT INTO user_data_set(user_name, selected_frog_key, user_money) VALUES('"
                        + userName + "','"
                        + selectedFrogKey + "','"
                        + currentUserMoney + "')"
                );
                showNewFrogNameAlert();
            }
        });

        newUserNameAlertDialog.show();

    }


    void updateUserDB(){
        cursor_user = database_user.rawQuery("SELECT * FROM user_data_set", null);
        database_user.execSQL("UPDATE user_data_set SET"
                +" user_name =" + "'"+userName+"'"
                +", selected_frog_key = " + selectedFrogKey
                +", user_money = " + currentUserMoney
        );
        moneyStringTextView.setText((currentUserMoney)+"");
    }


    void getCurrentItemDB(){
        cursor_item = database_item.rawQuery("SELECT * FROM item_data_set", null);
        int countItemDB = cursor_item.getCount();
        itemDataArrayList.clear();
        if(countItemDB != 0) {
            //?????? ???????????? ????????? ???
            try {
                while (cursor_item.moveToNext()) {//[?????????:row]??? ????????????
                    String ItemName = cursor_item.getString(cursor_item.getColumnIndex("item_name"));
                    String ItemExplain = cursor_item.getString(cursor_item.getColumnIndex("item_explain"));
                    int CurrentItemPrice = cursor_item.getInt(cursor_item.getColumnIndex("current_item_price"));
                    int CurrentLevel = cursor_item.getInt(cursor_item.getColumnIndex("current_level"));
                    int MaxLevel = cursor_item.getInt(cursor_item.getColumnIndex("max_level"));
                    int UpgradePriceTimes = cursor_item.getInt(cursor_item.getColumnIndex("upgrade_price_times"));
                    String ItemCase = cursor_item.getString(cursor_item.getColumnIndex("item_case"));

                    itemDataArrayList.add(new OneItemSet(
                            ItemName,
                            ItemExplain,
                            CurrentItemPrice,
                            CurrentLevel,
                            MaxLevel,
                            UpgradePriceTimes,
                            ItemCase
                            )
                    );
                }
            } catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("????????? ???????????? ??????").setPositiveButton("OK", null).show();
                getActivity().onBackPressed();
            }
        }
        if(countItemDB == 0){

            // default item data xml ?????? ????????????
            TypedArray itemIDArrayList = getResources().obtainTypedArray(R.array.item_name_list);
            int itemIDArrayListLength = itemIDArrayList.length();
            for(int i = 0; i< itemIDArrayListLength; i++){
                int id = itemIDArrayList.getResourceId(i, R.array.food_number);
                String[] stringOneItemSet = getResources().getStringArray(id);
                OneItemSet currentItemSet = new OneItemSet(); //TODO  ????????? ????????? ?????? OneItemSet??? ?????? ????????? ??? ??????????????????.

                currentItemSet.setItemName(stringOneItemSet[Item.NAME]);
                currentItemSet.setItemExplain(stringOneItemSet[Item.EXPLAIN]);
                currentItemSet.setItemPrice(Integer.parseInt(stringOneItemSet[Item.PRICE]));
                currentItemSet.setCurrentLevel(Integer.parseInt(stringOneItemSet[Item.CURRENT_LEVEL]));
                currentItemSet.setMaxLevel(Integer.parseInt(stringOneItemSet[Item.MAX_LEVEL]));
                currentItemSet.setUpgradePriceTimes(Double.parseDouble(stringOneItemSet[Item.UPGRADE_PRICE]));
                currentItemSet.setItemCase(stringOneItemSet[Item.TYPE]);


                database_item.execSQL("INSERT INTO item_data_set(item_name, item_explain, current_item_price, current_level, max_level, upgrade_price_times, item_case) VALUES('"
                        + currentItemSet.getItemName() + "','"
                        + currentItemSet.getItemExplain() + "','"
                        + currentItemSet.getItemPrice() + "','"
                        + currentItemSet.getCurrentLevel() + "','"
                        + currentItemSet.getMaxLevel() + "','"
                        + currentItemSet.getUpgradePriceTimes() + "','"
                        + currentItemSet.getItemCase() + "')"
                );

                itemDataArrayList.add( currentItemSet );
            }
        }
    }


    void updateFrogLayout(View mainFrogImageView, int size, Boolean soldFrog){
        final int maxLayoutSize = 1200;
        final int minLayoutSize = 160;
        int viewSize = size/10;
        if(soldFrog || (viewSize < minLayoutSize)){
            mainFrogImageView.getLayoutParams().height = minLayoutSize;
            mainFrogImageView.getLayoutParams().width = minLayoutSize;
        }else if(viewSize < maxLayoutSize) {
            mainFrogImageView.getLayoutParams().height = viewSize;
            mainFrogImageView.getLayoutParams().width = viewSize;
        }else{
            mainFrogImageView.getLayoutParams().height = maxLayoutSize;
            mainFrogImageView.getLayoutParams().width = maxLayoutSize;
        }
        mainFrogImageView.requestLayout();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

   public static String getUserName(){
        return userName;
    }
    //TODO ????????? ???????????? ?????? ????????? ?????? ??????
}
