package com.pyrion.poison_frog.center;

import android.app.AlertDialog;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
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

import androidx.fragment.app.Fragment;

import com.pyrion.poison_frog.center.fly_game.FlyGameActivity;
import com.pyrion.poison_frog.center.ItemStore.ActivityItemStore;
import com.pyrion.poison_frog.data.Frog;
import com.pyrion.poison_frog.data.Item;
import com.pyrion.poison_frog.data.OneFrogSet;
import com.pyrion.poison_frog.R;
import com.pyrion.poison_frog.data.OneItemSet;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class FragmentCenter extends Fragment {

    SQLiteDatabase database_user, database_frog, database_item;
    Cursor cursor_user, cursor_frog, cursor_item;

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
    String userName = "Anonymous";
    int currentUserMoney = 10000000; //todo edit it to 100
    private int selectedFrogKey = 1;
    int frogTouchedCount = 0;

    String[] deadFrogMsgs;
    String[] soledFrogMsgs;
    String[] poisonFoods;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deadFrogMsgs = getResources().getStringArray(R.array.dead_frog_msg);
        soledFrogMsgs = getResources().getStringArray(R.array.soled_frog_msg);
        poisonFoods = getResources().getStringArray(R.array.poison_food);

        //Item data settings
        database_item = getActivity().openOrCreateDatabase("itemDB.db", getActivity().MODE_PRIVATE, null);
        database_item.execSQL("CREATE TABLE IF NOT EXISTS item_data_set("
                + "item_name VARCHAR(40),"
                + "item_explain STRING,"
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

        getCurrentUserDB(); // set current data from DB
        getCurrentFrogDB();
        getCurrentItemDB();

        updateSelectedFrogState(currentFrogSet.getFrogState());
        return view;

    }


    @Override
    public void onResume() {
        super.onResume();

        // set current data from DB
        getCurrentUserDB();
        getCurrentFrogDB();
        getCurrentItemDB();

        // set userDB from current and set money UI
        moneyStringTextView.setText((currentUserMoney)+"");
        updateSelectedFrogState(currentFrogSet.getFrogState()); //set frog UI
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
                if(currentFrogSet.getFrogState() == Frog.STATE_DEATH){
                    int resurrectionPrice = (int) (getCurrentFrogPrice()*0.7);
                    addLogString("[치료비: "+resurrectionPrice+"$]");
                    if(currentUserMoney >=resurrectionPrice){
                        changeCurrentMoney(-resurrectionPrice);
                        resurrection();
                        return;
                    }
                    showToastString("돈 부족");
                    addLogString("[돈이 부족해서 치료를 못했습니다.]");

                }else if(currentFrogSet.getFrogState() == Frog.STATE_SOLD){
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
                if(currentFrogSet.getFrogSize()<40){
                    addLogString("[개구리가 너무 작아서 팔수 없습니다.]");
                    showToastString("판매 실패");
                    return;

                }else if(currentFrogSet.getFrogState() == Frog.STATE_SOLD){
                    addLogString("[이미 개구리가 팔리고 없습니다.]");
                    showToastString("판매 불가능");
                    return;

                }else if(currentFrogSet.getFrogState() == Frog.STATE_DEATH) {
                    addLogString("[개구리 시체는 쓸모가 없는데...]");
                    showToastString("헐값에 판매 완료");
                    changeCurrentMoney(    getCurrentFrogPrice()/10 );

                }else{
                    changeCurrentMoney(getCurrentFrogPrice());
                    showToastString("판매 완료");
                }

                addLogString(soledFrogMsgs[random.nextInt(soledFrogMsgs.length)]);
                addLogString("[개구리가 판매되었습니다.]");
                updateSelectedFrogState(Frog.STATE_SOLD);
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
                    showToastString("밥먹이기 불가능");
                    addLogString("[팔려간 개구리는 밥을 먹지 못함.]");
                    return;
                }
                if(currentFrogSet.getFrogState() == Frog.STATE_DEATH){
                    showToastString("밥먹이기 불가능");
                    addLogString("[죽은 개구리는 밥을 법지 못함.]");
                    return;
                }
                Intent intent = new Intent(getActivity(), FlyGameActivity.class);
                intent.putExtra("currentFrogKey", currentFrogSet.getFrogKey());
                intent.putExtra("currentFrogSrc", currentFrogSet.getFrogSrc());
                intent.putExtra("currentFrogSize", currentFrogSet.getFrogSize());
                intent.putExtra("currentFrogName", currentFrogSet.getFrogName());
                intent.putExtra("currentFrogSpecies", currentFrogSet.getFrogSpecies());
                int foodItem = itemDataArrayList.get(Item.Name.FOOD).getCurrentLevel();
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
                    showToastString("운동 불가능");
                    addLogString("[팔려간 개구리는 운동 못함.]");
                    return;
                }
                if(currentFrogSet.getFrogState() == Frog.STATE_DEATH){
                    showToastString("운동 불가능");
                    addLogString("[죽은 개구리는 운동 못함.]");
                    return;
                }
                changeFrogPower(1);
                showToastString("힘+1");
                updateCurrentFrogDB();
            }
        });

        mainMoneyIconSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentFrogSet.getFrogState() == Frog.STATE_SOLD){
                showToastString("돈벌기 불가능");
                addLogString("[일할 개구리가 없음.]");
                return;
            }
            if(currentFrogSet.getFrogState() == Frog.STATE_DEATH){
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
                if (currentFrogSet.getFrogState() == Frog.STATE_SOLD) {
                    if (currentUserMoney < 100) {
                        showToastString("구매 불가능");
                        addLogString("[돈이 부족하네..]");
                        return;
                    }
                    alertDialogIsBuy();
                    return;
                }
                if (currentFrogSet.getFrogState() == Frog.STATE_DEATH) {
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
                            updateSelectedFrogState(Frog.STATE_DEATH);
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

                    if(currentFrogSet.getFrogState() == Frog.STATE_DEATH){
                        addLogString("[죽은 개구리는 "+newFoodName+" 먹지 못함.]");
                        showToastString("반응 없음");
                        hideFoodInputEditText();
                        return false;
                    }
                    if(currentFrogSet.getFrogState() == Frog.STATE_SOLD){
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
                                updateSelectedFrogState(Frog.STATE_DEATH);
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
        return currentFrogSet.getFrogSize()+currentFrogSet.getFrogPower();
    }


    void showNewFoodLogSet(String newFoodName, int like){
        addLogString("[먹인 음식: "+newFoodName+"]");
        addLogString("[씹는 중...]");
        if(like < 1){
            addLogString("또 먹고싶지 않아요.");
            addLogString("[맛없어서 개구리가 작아짐]");
        }else{
            addLogString("옴뇸뇸뇸 맛있는 "+newFoodName+"입니다.");
        }
//        addLogString("_건강해지는"+"맛이예요.");
//        addLogString("방금 먹은 음식과"+"_잘 안"+ "어울리네요.");
//        addLogString("또 먹고싶" +"_지 않아"+"요.");
//        addLogString("짜증나"+"요.");
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
        addLogString("[개구리가 치료되었습니다.]");
        if(random.nextBoolean()){
            addLogString("죽었다가 살아났습니다.");
        }else{
            addLogString("살려줘서 고맙다 개굴.");
        }
        showToastString("개구리 부활");
        updateSelectedFrogState(Frog.STATE_ALIVE);
        mainFrogImageView.setImageResource(R.drawable.main_frog_jelly);
    }

    public void changeCurrentMoney(int diff){
        currentUserMoney += diff;
        updateUserDB();
    }

    public void changeFrogSize(int diff) {
        currentFrogSet.setFrogSize(currentFrogSet.getFrogSize() + diff);
        updateCurrentFrogDB();
        updateFrogLayout(mainFrogImageView,  currentFrogSet.getFrogSize(), false);

        showToastString("크기+"+diff);
    }

    public void changeFrogPower(int diff){
        currentFrogSet.setFrogPower(currentFrogSet.getFrogPower() + diff);
        updateCurrentFrogDB();
    }

    private void alertDialogIsBuy() {
        //TODO 새로 사는 개구리 창고 데이터 업데이트
        //AlertDialog
        AlertDialog.Builder isBuyBuilder = new AlertDialog.Builder(getActivity());
        alertIsBuyNewFrog= inflater.inflate(R.layout.alert_is_buy_new_frog, null);
        isBuyBuilder.setView(alertIsBuyNewFrog);

        //건축가에게 위 설정할 모양으로 AlertDialog 객체를 만들어 달라고 요청!
        isBuyAlertDialog= isBuyBuilder.create();

        //다이얼로그의 바깥쪽 영역을 터치했을때 다이얼로그가 사라지지 않도록
        isBuyAlertDialog.setCanceledOnTouchOutside(false);

        //다이얼로그를 화면에 보이기
        isBuyAlertDialog.show();
        isBuyAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        View payButton = alertIsBuyNewFrog.findViewById(R.id.pay_button);
        View cancelButton = alertIsBuyNewFrog.findViewById(R.id.cancel_button);

        payButton.setOnClickListener(payFrogButtonClickListener);
        cancelButton.setOnClickListener(cancelBuyFrogButton);

    }//TODO buy new frog  코드 정리하기

    View.OnClickListener payFrogButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isBuyAlertDialog.cancel();

            AlertDialog.Builder frogNameBuilder = new AlertDialog.Builder(getActivity());
            alertNewFrogName= inflater.inflate(R.layout.alert_set_new_frog_name, null);
            frogNameBuilder.setView(alertNewFrogName);

            ImageView newFrogNameConfirmButton = alertNewFrogName.findViewById(R.id.main_buy_button);
            newFrogNameEditText = alertNewFrogName.findViewById(R.id.new_frog_name_edit_text);

            frogNameAlertDialog = frogNameBuilder.create();
            frogNameAlertDialog.setCanceledOnTouchOutside(true);
            frogNameAlertDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#00000000")));
            frogNameAlertDialog.show();

            newFrogNameConfirmButton.setOnClickListener(newFrogNameConfirmButtonOnClickListener);
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
            String newFrogName = newFrogNameEditText.getText().toString();
            showToastString(newFrogName + "생성");
            addLogString("[개구리를 새로 구매 하셨습니다.]");
            changeCurrentMoney(-100);

            frogDataReset();
            currentFrogSet.setFrogName(newFrogName);
            updateSelectedFrogState(Frog.STATE_ALIVE);

            frogNameAlertDialog.cancel();

        }
    };

    public void frogDataReset(){
        currentFrogSet.setHouseType(Frog.HOUSE_TYPE_LENT);
        currentFrogSet.setCreatorName(userName);
        currentFrogSet.setFrogName(Frog.FROG_NAME_NULL);
        currentFrogSet.setFrogState(Frog.STATE_ALIVE);
        currentFrogSet.setFrogSpecies(Frog.SPECIES_BASIC);
        currentFrogSet.setFrogSize(Frog.SIZE_DEFAULT);
        currentFrogSet.setFrogPower(Frog.POWER_DEFAULT);

        updateSelectedFrogState(Frog.STATE_ALIVE);
    }

    public void updateSelectedFrogState(int frogState){
        switch (frogState){
            case Frog.STATE_ALIVE:
                currentFrogSet.setFrogState(Frog.STATE_ALIVE);
                mainFrogImageView.setImageResource(R.drawable.main_frog_jelly);
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
        }
        updateCurrentFrogDB();
        mainFrogImageView.requestLayout();
    }

    void getCurrentFrogDB(){
        database_frog = getActivity().openOrCreateDatabase("frogsDB.db", getActivity().MODE_PRIVATE, null);
        cursor_frog = database_frog.rawQuery("SELECT * FROM frogs_data_set", null);//WHERE절이 없기에 모든 레코드가 검색됨
        int countFrogDB = cursor_frog.getCount();
        if(countFrogDB != 0) {
            try {//when there is selected frog data
                cursor_frog = database_frog.rawQuery("SELECT * FROM frogs_data_set WHERE frog_key = " + selectedFrogKey, null);//WHERE절이 없기에 모든 레코드가 검색됨
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("개구리 불러오기 오류").setPositiveButton("OK", null).show();
                getActivity().onBackPressed();
            }
        }
        else {
            //TODO 만약 movetToNext()한후에도 데이터가 하나도 없었다면 UNBOXING MODE
            // 유저 이름 입력받고 개구리 이름 입력 받는 페이지로 넘어가는 전역변수 모드 순자 바꾸기
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
        database_frog.execSQL("UPDATE frogs_data_set SET"
                +" frog_name =" + "'"+ currentFrogSet.getFrogName() +"'"
                +", frog_state =" + currentFrogSet.getFrogState()
                +", frog_species =" + currentFrogSet.getFrogSpecies()
                +", frog_size =" + currentFrogSet.getFrogSize()
                +", frog_power =" + currentFrogSet.getFrogPower()

                +" WHERE frog_key =" + currentFrogSet.getFrogKey()
        );
    }

    void getCurrentUserDB(){
        cursor_user = database_user.rawQuery("SELECT * FROM user_data_set", null);
        int countUserDB = cursor_user.getCount();
        if(countUserDB != 0) {
            //기존 데이터가 존재할 때
            try {
                cursor_user.moveToNext();
                userName = cursor_user.getString(cursor_user.getColumnIndex("user_name"));
                selectedFrogKey = cursor_user.getInt(cursor_user.getColumnIndex("selected_frog_key"));
                currentUserMoney = cursor_user.getInt(cursor_user.getColumnIndex("user_money"));
            } catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("사용자 데이터 불러오기 오류").setPositiveButton("OK", null).show();
                getActivity().onBackPressed();
            }
        }
        if(countUserDB == 0){
            database_user.execSQL("INSERT INTO user_data_set(user_name, selected_frog_key, user_money) VALUES('"
                    + userName + "','"
                    + selectedFrogKey + "','"
                    + currentUserMoney + "')"
            );
        }

        //TODO Adapter가 닫힐 때 닫아주기
    }


    void updateUserDB(){
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
            //기존 데이터가 존재할 때
            try {
                while (cursor_item.moveToNext()) {//[레코드:row]로 커서이동
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
                builder.setMessage("아이템 불러오기 오류").setPositiveButton("OK", null).show();
                getActivity().onBackPressed();
            }
        }
        if(countItemDB == 0){

            // default item data xml 에서 받아오기
            TypedArray itemIDArrayList = getResources().obtainTypedArray(R.array.item_name_list);
            int itemIDArrayListLength = itemIDArrayList.length();
            for(int i = 0; i< itemIDArrayListLength; i++){
                int id = itemIDArrayList.getResourceId(i, R.array.food);
                String[] stringOneItemSet = getResources().getStringArray(id);
                OneItemSet currentItemSet = new OneItemSet(); //TODO  여기서 원래는 받은 OneItemSet을 바로 넣어줄 수 있도록해야함.

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
        final int maxLayoutSize = 12000;
        final int minLayoutSize = 160;
        int viewSize = size/10;
        if(soldFrog || (size/10 < minLayoutSize)){
            mainFrogImageView.getLayoutParams().height = minLayoutSize;
            mainFrogImageView.getLayoutParams().width = minLayoutSize;
        }else if(size < maxLayoutSize) {
            mainFrogImageView.getLayoutParams().height = size/10;
            mainFrogImageView.getLayoutParams().width = size/10;
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

    //TODO 개구리 롱프래스 하면 개구리 상태 보기
}
