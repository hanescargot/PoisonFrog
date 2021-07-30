package com.pyrion.poison_frog;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import java.util.Random;
import java.util.Stack;

import androidx.fragment.app.Fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class MainFragmentCenter extends Fragment {
    SQLiteDatabase database_frog;
    SQLiteDatabase database_user;


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
    String userName = "Anonymous";
    int currentUserMoney = 100;
    int frogTouchedCount = 0;

// 못외워서 일단 써둠                    Log.i("tag",foodLogString);


    String[] deadFrogMsgs = new String[]{
            "[개구리는 지금 시체일 뿐이야.]",
            "[죽은 개구리는 대답이없다.]",
            "[개구리 죽었다니까.]",
            "[미련가지지마. 개구리는 죽었어.]",
            "[너는 개구리를 죽게했어.]",
            "[그런다고 개구리가 살아나지는 않아.]",
            "[죽은 개구리는 찔러도 반응이없어.]",
            "[개구리를 다시 살리려면 치료 아이콘 클릭]"
    };
    String[] soledFrogMsgs = new String[]{
            "안녕 주인아...고마웠어",
            "키워줘서 고마웠어.",
            "이제 자유가 되는구나...",
            "나 먼저 간다... 행복해라...",
            "개굴개굴...고기가 되는건가..",
            "안돼...가기싫어...",
            "끄악...개굴!!!",
            "개구리 살려!!!"
    };
    String[] poisonFoods = new String[]{
            "독",
            "모기",
            "소금",
            "해삼",
            "고추",
            "거미",
            "복어",
            "뱀",
            "전갈"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //User data settings
        database_user = getActivity().openOrCreateDatabase("userDB.db", getActivity().MODE_PRIVATE, null);;
        database_user.execSQL("CREATE TABLE IF NOT EXISTS user_data_set("
                        + "user_name String,"
                        + "user_money INTEGER)");

        Cursor cursor_user = database_user.rawQuery("SELECT * FROM user_data_set", null);
        if (cursor_user != null) {
            while (cursor_user.moveToNext()) {
                if(cursor_user.getString(cursor_user.getColumnIndex("user_name")) != null){
                        //기존 데이터가 존재할 때
                        userName = cursor_user.getString(0);
                        currentUserMoney = cursor_user.getInt(1);

                }else {
                    userName = "Anonymous";
                    currentUserMoney = 100;

                    database_user.execSQL("INSERT INTO user_data_set(user_name, user_money) VALUES('"
                            + userName + "','"
                            + currentUserMoney + "')"
                    );
                }
            }
        }


        //Frog data settings
        database_frog = getActivity().openOrCreateDatabase("frogsDB.db", getActivity().MODE_PRIVATE, null);
        database_frog.execSQL("CREATE TABLE IF NOT EXISTS frogs_data_set("
                + "num_key INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "house_type INTEGER,"
                + "creator_name VARCHAR(40),"
                + "frog_name VARCHAR(40),"
                + "frog_state INTEGER,"
                + "frog_species INTEGER,"
                + "frog_size DOUBLE,"
                + "frog_power DOUBLE)");

        Cursor cursor_frog = database_frog.rawQuery("SELECT * FROM frogs_data_set", null);//WHERE절이 없기에 모든 레코드가 검색됨

        currentFrogSet = new OneFrogSet();

        if (cursor_frog != null) {
            while (cursor_frog.moveToNext()) { //[레코드:row]로 커서이동
                if (cursor_frog.getString(cursor_frog.getColumnIndex("creator_name")) != null) {

                    //columnIndex: 0 is origin number
                    //창고 상태가 selected 인것 찾기
                    currentFrogSet.houseType = cursor_frog.getInt(1);
                    currentFrogSet.creatorName = cursor_frog.getString(2);
                    currentFrogSet.frogName = cursor_frog.getString(3);
                    currentFrogSet.frogState = cursor_frog.getInt(4);
                    currentFrogSet.frogSpecies = cursor_frog.getInt(5);
                    currentFrogSet.frogSize = cursor_frog.getInt(6);
                    currentFrogSet.frogPower = cursor_frog.getInt(7);

                }else{
                    //TODO 만약 movetToNext()한후에도 데이터가 하나도 없었다면 UNBOXING MODE
                    // 유저 이름 입력받고 개구리 이름 입력 받는 페이지로 넘어가는 전역변수 모드 순자 바꾸기
                    currentFrogSet.houseType = Frog.HOUSE_TYPE_LENT;
                    currentFrogSet.creatorName = "hyunju";
                    currentFrogSet.frogName = "default";
                    currentFrogSet.frogState = Frog.STATE_ALIVE;
                    currentFrogSet.frogSpecies = Frog.SPECIES_BASIC;
                    currentFrogSet.frogSize = Frog.SIZE_DEFAULT;
                    currentFrogSet.frogPower = Frog.POWER_DEFAULT;
                    // TODO DATA INPUT;
                    database_frog.execSQL("INSERT INTO frogs_data_set(house_type, creator_name, frog_name, frog_state, frog_species, frog_size, frog_power) VALUES('"
                            + currentFrogSet.houseType + "','"
                            + currentFrogSet.creatorName + "','"
                            + currentFrogSet.frogName + "','"
                            + currentFrogSet.frogState + "','"
                            + currentFrogSet.frogSpecies + "','"
                            + currentFrogSet.frogSize + "','"
                            + currentFrogSet.frogPower + "')"
                    );
                }
            }
        }


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

        imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE); //getActivity(). 해줘야함.
        foodInputEditText.setOnEditorActionListener(foodInputActionListener);

        setStart();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflater= getLayoutInflater();

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
                //개구리 되살리기
                if(currentFrogSet.frogState == Frog.STATE_DEATH){
                    int resurrectionPrice = (int) (getCurrentFrogPrice()*0.7);
                    addLogString("[치료비: "+resurrectionPrice+"$]");
                    if(currentUserMoney >=resurrectionPrice){
                        changeCurrentMoney(-resurrectionPrice);
                        resurrection();
                        return;
                    }
                    showToastString("돈 부족");
                    addLogString("[돈이 부족해서 치료를 못했습니다.]");

                }else if(currentFrogSet.frogState == Frog.STATE_SOLD){
                    showToastString("치료할 개구리가 없음");
                    addLogString("[개구리를 새로 구매해 주세요.]");
                }else{
                    showToastString("건강해서 치료 불필요");
                }
            }
        });

        mainGamePlayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //game play
            }
        });

        mainSellIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentFrogSet.frogSize<40){
                    addLogString("[개구리가 너무 작아서 팔수 없습니다.]");
                    showToastString("판매 실패");
                    return;

                }else if(currentFrogSet.frogState == Frog.STATE_SOLD){
                    addLogString("[이미 개구리가 팔리고 없습니다.]");
                    showToastString("판매 불가능");
                    return;

                }else if(currentFrogSet.frogState == Frog.STATE_DEATH) {
                    addLogString("[개구리 시체는 쓸모가 없는데...]");
                    showToastString("헐값에 판매 완료");
                    changeCurrentMoney(    getCurrentFrogPrice()/10 );

                }else{
                    changeCurrentMoney(getCurrentFrogPrice());
                    showToastString("판매 완료");
                }

                addLogString(soledFrogMsgs[random.nextInt(soledFrogMsgs.length)]);
                addLogString("[개구리가 판매되었습니다.]");
                updateFrogState(Frog.STATE_SOLD);
            }
        });

        mainHouseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivityFrogHouse.class);

                intent.putExtra("name", "Frog Name");
                intent.putExtra("property", Frog.SPECIES_BASIC);
                intent.putExtra("size",currentFrogSet.frogSize);
                intent.putExtra("power",currentFrogSet.frogSize);

                //결과 값도 돌려 받을 거임
                startActivityForResult(intent, 999);
            }
        });

        mainFoodIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentFrogSet.frogState == Frog.STATE_SOLD){
                    showToastString("밥먹이기 불가능");
                    addLogString("[팔려간 개구리는 밥을 먹지 못함.]");
                    return;
                }
                if(currentFrogSet.frogState == Frog.STATE_DEATH){
                    showToastString("밥먹이기 불가능");
                    addLogString("[죽은 개구리는 밥을 법지 못함.]");
                    return;
                }
                changeFrogSize(+1);
                showToastString("크기+1");

                frogDBUpdate();
            }
        });

        mainDumbbellIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentFrogSet.frogState == Frog.STATE_SOLD){
                    showToastString("운동 불가능");
                    addLogString("[팔려간 개구리는 운동 못함.]");
                    return;
                }
                if(currentFrogSet.frogState == Frog.STATE_DEATH){
                    showToastString("운동 불가능");
                    addLogString("[죽은 개구리는 운동 못함.]");
                    return;
                }
                currentFrogSet.frogPower +=1;
                showToastString("힘+1");

                frogDBUpdate();
            }
        });

        mainMoneyIconSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentFrogSet.frogState == Frog.STATE_SOLD){
                showToastString("돈벌기 불가능");
                addLogString("[일할 개구리가 없음.]");
                return;
            }
            if(currentFrogSet.frogState == Frog.STATE_DEATH){
                showToastString("돈벌기 불가능");
                addLogString("[죽은 개구리는 일을 못함.]");
                return;
            }
            changeCurrentMoney(+1);
            showToastString("+1$");
            }
        });

        mainFrogImageView.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
                if (currentFrogSet.frogState == Frog.STATE_SOLD) {
                    if (currentUserMoney < 100) {
                        showToastString("구매 불가능");
                        addLogString("[돈이 부족하네..]");
                        return;
                    }
                    buyNewFrog();
                    return;
                }
                if (currentFrogSet.frogState == Frog.STATE_DEATH) {
                    addLogString(deadFrogMsgs[random.nextInt(8)]);
                    return;
                }
                frogTouchedCount++;
                showToastString("개구리 찌름");
                if (logStringBuffer.length() == 0) {
                    logTextView.setText("");
                }
                switch (frogTouchedCount) {
                    case 1:
                        addLogString("왜요?");
                        break;
                    case 2:
                        addLogString("잘 살아 있다구요.");
                        break;
                    case 3:
                        addLogString("아파요.");
                        break;
                    case 4:
                        addLogString("힘들어요. 그만 찌르세요");
                        break;
                    case 5:
                        addLogString("죽을 것 같아요.");
                        break;
                    default: {

                        if (random.nextInt(2) == 1) {
                            updateFrogState(Frog.STATE_DEATH);
                            addLogString("[개구리 죽음]");
                            showToastString("개구리 사망");
                        } else {
                            addLogString("[개구리 상태가 이상하다.]");
                            frogTouchedCount = random.nextInt(6);
                        }
                    }
                    break;
                }
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

    EditText.OnEditorActionListener foodInputActionListener = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            //들어오는 글자 하나하나 알수있음
            //event.keypad현재 키패드에서 누른 글자 알수있음 한글인지 알아보기// actionID==EditorInfo.IME_ACTION_SEARCH
            //actionID 는 무조건 오른쪽 아래에
            if(event == null){
                //엔터 눌렀을 때
                String newFoodName = v.getText().toString();

                if(newFoodName.length()>0) {
                    if(logStringBuffer.length()==0) {
                        logTextView.setText("");
                    }
                    originFoodNameStack.add(newFoodName);

                    if(currentFrogSet.frogState == Frog.STATE_DEATH){
                        addLogString("[죽은 개구리는 "+newFoodName+" 먹지 못함.]");
                        showToastString("반응 없음");
                        hideFoodInputEditText();
                        return false;
                    }
                    if(currentFrogSet.frogState == Frog.STATE_SOLD){
                        addLogString("[팔려간 개구리는 "+newFoodName+" 먹지 못함.]");
                        showToastString("개구리 없음");
                        hideFoodInputEditText();
                        return false;
                    }
                    showToastString("개구리가 "+newFoodName+" 먹음");
                    int randomFoodPoint = random.nextInt(10)-1;//-1~9


                    for(String poisonFood: poisonFoods){
                        if(newFoodName.equals(poisonFood)){
                            addLogString("커어어억... 이 맛은!!!");
                            if(random.nextBoolean()){
                                changeFrogSize(randomFoodPoint *30);
                                addLogString("[개구리가 급격히 성장 함]");
                            }else{
                                updateFrogState(Frog.STATE_DEATH);
                                addLogString("[개구리 죽음]");
                                showToastString("개구리 사망");
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

    int getCurrentFrogPrice(){
        return currentFrogSet.frogSize+currentFrogSet.frogPower;
    }


    void showNewFoodLogSet(String newFoodName, int like){
        if(like < 1){
            addLogString("[맛없어서 개구리가 작아짐]");
        }
        addLogString("[먹인 음식: "+newFoodName+"]");
        addLogString("[_씹"+"는 중...]");
        addLogString("옴뇸뇸뇸"+"_맛있는 "+ newFoodName+"입니다.");
        addLogString("_건강해지는"+"맛이예요.");
        addLogString("방금 먹은 음식과"+"_잘 안"+ "어울리네요.");
        addLogString("또 먹고싶" +"_지 않아"+"요.");
        addLogString("짜증나"+"요.");
    }

    public void setStart() {
        updateFrogState(currentFrogSet.frogState);
        moneyStringTextView.setText((currentUserMoney)+"");
    }

    OneFrogSet currentFrogSet;
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
        addLogString("[개구리가 치료되었습니다.]");
        if(random.nextBoolean()){
            addLogString("죽었다가 살아났습니다.");
        }else{
            addLogString("살려줘서 고맙다 개굴.");
        }
        showToastString("개구리 부활");
        updateFrogState(Frog.STATE_ALIVE);
        mainFrogImageView.setImageResource(R.drawable.main_frog_jelly);
    }

    public void changeCurrentMoney(int diff){
        currentUserMoney += diff;
        moneyStringTextView.setText((currentUserMoney)+"");

        database_user.execSQL("UPDATE user_data_set set user_money = "
                +currentUserMoney);
    }

    public void changeFrogSize(int diff) {
        currentFrogSet.frogSize += diff;
        database_frog.execSQL("UPDATE frogs_data_set SET frog_size = " +currentFrogSet.frogSize);

        mainFrogImageView.getLayoutParams().height=currentFrogSet.frogSize;
        mainFrogImageView.getLayoutParams().width=currentFrogSet.frogSize;
        mainFrogImageView.requestLayout();
    }

    private void buyNewFrog() {
        //TODO 새로 사는 개구리 창고 데이터 업데이트
        //AlertDialog
        AlertDialog.Builder isBuyBuilder= new AlertDialog.Builder(getActivity());
        alertIsBuyNewFrog= inflater.inflate(R.layout.alert_is_buy_new_frog, null);
        isBuyBuilder.setView(alertIsBuyNewFrog);


        View isBuyButton = alertIsBuyNewFrog.findViewById(R.id.is_buy_button);


        //건축가에게 위 설정할 모양으로 AlertDialog 객체를 만들어 달라고 요청!
        isBuyAlertDialog= isBuyBuilder.create();

        //다이얼로그의 바깥쪽 영역을 터치했을때 다이얼로그가 사라지지 않도록
        isBuyAlertDialog.setCanceledOnTouchOutside(false);

        //다이얼로그를 화면에 보이기
        isBuyAlertDialog.show();
        isBuyAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        isBuyButton.setOnClickListener(isBuyButtonClickListener);

    }//TODO buy new frog  코드 정리하기

    View.OnClickListener isBuyButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder frogNameBuilder= new AlertDialog.Builder(getActivity());//R.style.Theme_TransparentBackground
            //                frogNameBuilder.setTitle("새 개구리 이름 정하기");
            alertNewFrogName= inflater.inflate(R.layout.alert_set_new_frog_name, null);
            frogNameBuilder.setView(alertNewFrogName);


            ImageView newFrogNameButton = alertNewFrogName.findViewById(R.id.main_buy_button);
            newFrogNameEditText= alertNewFrogName.findViewById(R.id.new_frog_name_edit_text);

            frogNameAlertDialog= frogNameBuilder.create();
            frogNameAlertDialog.setCanceledOnTouchOutside(true);

            frogNameAlertDialog.show();
            frogNameAlertDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#00000000")));


            isBuyAlertDialog.cancel();
            newFrogNameButton.setOnClickListener(newFrogNameButtonOnClickListener);

        }
    };

    View.OnClickListener newFrogNameButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String newFrogName = newFrogNameEditText.getText().toString();
            showToastString(newFrogName + "생성");
            addLogString("[개구리를 새로 구매 하셨습니다.]");
            changeCurrentMoney(-100);

            currentFrogSet.frogSize = 80;
            currentFrogSet.frogSize = 80;
            database_frog.execSQL("INSERT INTO frogs_data_set(house_type, creator_name, frog_name, frog_state, frog_species, frog_size, frog_power) VALUES('"
                    + Frog.HOUSE_TYPE_LENT + "','"
                    + userName + "','"
                    + newFrogName + "','"
                    + Frog.STATE_ALIVE + "','"
                    + Frog.SPECIES_BASIC + "','"
                    + Frog.SIZE_DEFAULT + "','"
                    + Frog.POWER_DEFAULT + "')"
            );

            //Current frog data setting
            currentFrogSet.houseType = Frog.HOUSE_TYPE_LENT;
            currentFrogSet.creatorName = "hyunju";
            currentFrogSet.frogName = "default";
            currentFrogSet.frogState = Frog.STATE_ALIVE;
            currentFrogSet.frogSpecies = Frog.SPECIES_BASIC;
            currentFrogSet.frogSize = Frog.SIZE_DEFAULT;
            currentFrogSet.frogPower = Frog.POWER_DEFAULT;



            updateFrogState(Frog.STATE_ALIVE);
            frogNameAlertDialog.cancel();

        }
    };

    public void updateFrogState(int frogState){
        switch (frogState){
            case Frog.STATE_ALIVE:
                currentFrogSet.frogState = Frog.STATE_ALIVE;
                mainFrogImageView.setImageResource(R.drawable.main_frog_jelly);
                mainFrogImageView.getLayoutParams().height=currentFrogSet.frogSize;
                mainFrogImageView.getLayoutParams().width=currentFrogSet.frogSize;
                break;
            case Frog.STATE_SOLD:
                currentFrogSet.frogState = Frog.STATE_SOLD;
                mainFrogImageView.setImageResource(R.drawable.main_gift);
                mainFrogImageView.getLayoutParams().height=160;
                mainFrogImageView.getLayoutParams().width=160;
                break;
            case Frog.STATE_DEATH:
                currentFrogSet.frogState = Frog.STATE_DEATH;
                mainFrogImageView.setImageResource(R.drawable.main_dead_frog);
                mainFrogImageView.getLayoutParams().height=currentFrogSet.frogSize;
                mainFrogImageView.getLayoutParams().width=currentFrogSet.frogSize;
                frogTouchedCount = 0;
                break;
        }

        frogDBUpdate();
        mainFrogImageView.requestLayout();
    }

    void frogDBUpdate(){
        //                TODO DB 업데이트 하는 코드 추가 WHERE 문이 왜 안되지? PRIVATE여서 인듯

        database_frog.execSQL("UPDATE frogs_data_set SET"
                +" house_type = " +currentFrogSet.houseType
                +", frog_state =" + currentFrogSet.frogState
                +", frog_species =" + currentFrogSet.frogSpecies
                +", frog_size =" + currentFrogSet.frogSize
                +", frog_power =" + currentFrogSet.frogPower
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == 999){
            showToastString("yes");
            //Fragment바꾸기

        }

    }

    //TODO 개구리 롱프래스 하면 개구리 상태 보기, 지금 상대와 거래하기
}
