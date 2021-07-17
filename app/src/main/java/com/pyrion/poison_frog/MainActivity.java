package com.pyrion.poison_frog;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Stack;


public class MainActivity extends AppCompatActivity {
    Toast toast;
    Random random = new Random();
    EditText foodInputEditText;
    TextView foodLog, moneyString;
    ImageView mainFrog, chefHat, healthCare;
    View menuIcons, mainBackground;
    String foodLogString = "";
    ScrollView foodLogScroll;
    Stack<String> originFoodNameStack = new Stack<>();
    int frogTouchedCount = 0;
// 못외워서 일단 써둠                    Log.i("tag",foodLogString);

    Boolean isAlive = true;
    String[] deadFrogMsg = new String[]{
            "[개구리는 지금 시체일 뿐이야.]",
            "[죽은 개구리는 대답이없다.]",
            "[개구리 죽었다니까.]",
            "[미련가지지마. 개구리는 죽었어.]",
            "[너는 개구리를 죽게했어.]",
            "[그런다고 개구리가 살아나지는 않아.]",
            "[죽은 개구리는 찔러도 반응이없어.]",
            "[개구리를 다시 살리려면 치료 아이콘 클릭]"
    };
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        foodInputEditText = findViewById(R.id.food_name_input);
        foodLog = findViewById(R.id.food_log);
        foodLogScroll = findViewById(R.id.log_scroll);
        mainFrog = findViewById(R.id.main_frog);
        chefHat = findViewById(R.id.chef_hat);
        menuIcons = findViewById(R.id.menu_list);
        mainBackground = findViewById(R.id.back_ground);
        healthCare = findViewById(R.id.health);
        moneyString = findViewById(R.id.money_string);


        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        foodInputEditText.setOnEditorActionListener(foodInputActionListener);
    }

    EditText.OnEditorActionListener foodInputActionListener = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            //들어오는 글자 하나하나 알수있음
            //event.keypad현재 키패드에서 누른 글자 알수있음 한글인지 알아보기// actionID==EditorInfo.IME_ACTION_SEARCH
            //actionID 는 무조건 오른쪽 아래에
            if(event == null){
                //엔터 눌렀을 때
                String newFoodName = v.getText().toString();
                if(!isAlive){
                    addFoodLogString("[죽은 개구리는 "+newFoodName+" 먹지 못함.]");
                    showToastString("반응 없음");
                    hideFoodInputEditText();
                    return false;
                }

                if(newFoodName.length()>0) {
                    if(foodLogString.length()==0) {
                        foodLog.setText("");
                    }
                    originFoodNameStack.add(newFoodName);
                    showToastString("개구리가 "+newFoodName+" 먹음");/////error
                    showNewFoodLogSet(newFoodName);
                    v.setText("");
                }
                hideFoodInputEditText();
            }
            return false;
        }

    };

    void showNewFoodLogSet(String newFoodName){
        addFoodLogString("[먹인 음식: "+newFoodName+"]");
        addFoodLogString("[_씹"+"는 중...]");
        addFoodLogString("옴뇸뇸뇸"+"_맛있는 "+ newFoodName+"입니다.");
        addFoodLogString("_건강해지는"+"맛이예요.");
        addFoodLogString("방금 먹은 음식과"+"_잘 안"+ "어울리네요.");
        addFoodLogString("또 먹고싶" +"_지 않아"+"요.");
        addFoodLogString("짜증나"+"요.");
    }

    //TODO 개구리 터치 횟수 카운트 앤 무빙 리액션
    public void touchedFrog(View v) {
        if(!isAlive) {
            foodLogString += deadFrogMsg[random.nextInt(8)]+"\n\n";
            foodLog.setText(foodLogString);
            foodLogScroll.fullScroll(View.FOCUS_DOWN);
            return;
        }
        frogTouchedCount++;
        showToastString("개구리 찌름");
        if(foodLogString.length()==0){
            foodLog.setText("");
        }
        switch (frogTouchedCount){
            case 1: addFoodLogString("왜요?"); break;
            case 2: addFoodLogString("잘 살아 있다구요."); break;
            case 3: addFoodLogString("아파요."); break;
            case 4: addFoodLogString("힘들어요. 그만 찌르세요"); break;
            case 5: addFoodLogString("죽을 것 같아요."); break;
            default: {

                if(random.nextInt(2)==1){
                    frogTouchedCount = 0;
                    addFoodLogString("[개구리 죽음]");
                    showToastString("개구리 사망");
                    isAlive = false;
                    mainFrog.setImageResource(R.drawable.main_dead_frog);

                }else{
                    addFoodLogString("[개구리 상태가 이상하다.]");
                    frogTouchedCount = random.nextInt(6);
                }
            } break;
        }
    }

    public void logEraser(View v){
        foodLogString="";
        foodLog.setText("Ready...");
        foodLogScroll.fullScroll(View.FOCUS_DOWN);
    }

    public void showToastString(String text){
        if(toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(
                MainActivity.this,
                text,
                Toast.LENGTH_SHORT
        );
        toast.setGravity(Gravity.CENTER_VERTICAL, 0 , 200);
        toast.show();
    }

    public void chefHatClicked(View view) {
        if(menuIcons.getVisibility()==View.VISIBLE){
            menuIcons.setVisibility(View.GONE);
            foodInputEditText.setVisibility(View.VISIBLE);

            //keyboard popup
            foodInputEditText.requestFocus();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            return;
        }
        hideFoodInputEditText();
    }

    public void hideFoodInputEditText() {
        menuIcons.setVisibility(View.VISIBLE);
        foodInputEditText.setVisibility(View.GONE);
        foodInputEditText.setText("");
        imm.hideSoftInputFromWindow(foodInputEditText.getWindowToken(), 0);
    }


    public void healthCareClicked(View view) {
        //개구리 되살리기
        if(!isAlive){
            //죽어있으면 되살리기 살아있으면 회복
            resurrection();
        }
    }

    public void addFoodLogString(String string){
        foodLogString += string;
        foodLogString += "\n\n";
        foodLog.setText(foodLogString);
        foodLogScroll.fullScroll(View.FOCUS_DOWN);
    }

    void resurrection(){
        addFoodLogString("죽었다가 살아났습니다.");
        showToastString("개구리 부활");
        isAlive=true;
        mainFrog.setImageResource(R.drawable.main_frog_jelly);
        //체력 0
        //-100$ ask
        //
        int currentMoney = Integer.parseInt(moneyString.getText().toString());
        moneyString.setText((currentMoney-100)+"");
    }

    public void backgroundClicked(View view) {
        hideFoodInputEditText();
    }
}