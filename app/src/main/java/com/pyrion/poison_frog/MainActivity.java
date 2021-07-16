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
    TextView foodLog;
    ImageView mainFrog, chefHat;
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
        mainBackground =findViewById(R.id.back_ground);

        foodInputEditText.setOnEditorActionListener(foodInputActionListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if( focusView != null){
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    TextView.OnEditorActionListener foodInputActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            //event.keypad현재 키패드에서 누른 글자 알수있음 한글인지 알아보기// actionID==EditorInfo.IME_ACTION_SEARCH
            //actionID 는 무조건 오른쪽 아래에
            if(event == null){
                String newFoodName = v.getText().toString();
                if(!isAlive){
                    foodLogString += "[죽은 개구리는 "+newFoodName+" 못 먹음.]\n\n";
                    foodLog.setText(foodLogString);
                    foodLogScroll.fullScroll(View.FOCUS_DOWN);
                    v.setText("");
                    return false;
                }

                if(newFoodName.length()>0) {
                    if(foodLogString.length()==0) {
                        foodLog.setText("");
                    }
                    originFoodNameStack.add(newFoodName);
                    showToastString("개구리가 "+newFoodName+" 먹음");/////error
                    foodLogString += getNewFoodLogString(newFoodName);
                    foodLogString += "\n";
                    foodLog.setText(foodLogString);
                    foodLogScroll.fullScroll(View.FOCUS_DOWN);
                    v.setText("");
                }
            }
            return false;
        }

    };

    String getNewFoodLogString(String newFoodName){
        String sumString="";
        sumString += "[먹인음식: "+newFoodName+"]\n";
        sumString += "[_씹"+"는 중...]"+"\n"; //텍스트 마이닝
        sumString += "옴뇸뇸뇸"+"_맛있는"+ newFoodName+"입니다."+"\n"; //텍스트마이닝 후 단어 라벨 판단
        sumString += "_건강해지는"+"맛이예요."+"\n"; //텍스트 마이닝
        sumString += "방금 먹은 음식과"+"_잘 안"+ "어울리네요."+"\n"; //이전 음식과 수 계산
        sumString += "또 먹고싶" +"_지 않아"+"요."+"\n"; //0과1랜덤
        sumString += "짜증나"+"요."+"\n"; //수 계산
        return sumString;
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
            case 1: foodLogString += "왜요?"; break;
            case 2: foodLogString += "잘 살아 있다구요."; break;
            case 3: foodLogString += "아파요."; break;
            case 4: foodLogString += "힘들어요. 그만 찌르세요"; break;
            case 5: foodLogString += "죽을 것 같아요."; break;
            default: {

                if(random.nextInt(2)==1){
                    frogTouchedCount = 0;
                    foodLogString +="[개구리 죽음]";
                    showToastString("개구리 사망");
                    isAlive = false;
                    mainFrog.setImageResource(R.drawable.main_dead_frog);

                }else{
                    foodLogString +="[개구리 상태가 이상하다.]";
                    frogTouchedCount = random.nextInt(6);
                }
            } break;
        }
        foodLogString += "\n\n";
        foodLog.setText(foodLogString);
        foodLogScroll.fullScroll(View.FOCUS_DOWN);
    }

    public void logEraser(View v){
        foodLogString="";
        foodLog.setText("Ready...");
        foodLogScroll.fullScroll(View.FOCUS_DOWN);
    }

    public void showToastString(String text){
        if(toast == null) {
            toast = Toast.makeText(
                    MainActivity.this,
                    text,
                    Toast.LENGTH_SHORT
            );
        } else {
            toast.setText(text);
        }
        toast.setGravity(Gravity.CENTER_VERTICAL, 0 , 200);
        toast.show();
    }

    public void chefHatClicked(View view) {
        if(menuIcons.getVisibility()==View.VISIBLE){
            menuIcons.setVisibility(View.GONE);
            foodInputEditText.setVisibility(View.VISIBLE);
            return;
        }
        backGroundClicked(view);
    }

    public void backGroundClicked(View view) {
        menuIcons.setVisibility(View.VISIBLE);
        foodInputEditText.setVisibility(View.GONE);
    }
}