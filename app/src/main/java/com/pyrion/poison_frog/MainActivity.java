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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    Random random = new Random();
    EditText foodInputEditText;
    TextView foodLog;
    String foodLogString = "";
    ScrollView foodLogScroll;
    Stack<String> originFoodName = new Stack<>();
    int frogTochedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        foodInputEditText = findViewById(R.id.food_name_input);
        foodLog = findViewById(R.id.food_log);
        foodLogScroll = findViewById(R.id.log_scroll);

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
            if(event == null){
                String newFoodName = v.getText().toString();

                if(newFoodName.length()>0) {
                    originFoodName.add(newFoodName);
                    showToastString("개구리가 "+newFoodName+"을 먹었습니다.");
                    foodLogString += "\n"+getNewFoodLogString(newFoodName);
                    Log.i("tag",foodLogString);
                    foodLog.setText(foodLogString);
                }
                foodLogScroll.fullScroll(View.FOCUS_DOWN);
                v.setText("");
            }
            return false;
        }
    };

    String getNewFoodLogString(String newFoodName){
        String sumString="";
        sumString += "먹인음식: "+newFoodName+"\n";
        sumString += "개구리: 옴뇸뇸뇸"+"_맛있는"+ newFoodName+"입니다."+"\n"; //텍스트마이닝 후 단어 라벨 판단
        sumString += "_씹"+"는 중...\n"; //텍스트 마이닝
        sumString += "_건강해지는"+"맛이예요.\n"; //텍스트 마이닝
        sumString += "방금 먹은 음식과"+"_잘 안"+ "어울리네요.\n"; //이전 음식과 수 계산
        sumString += "또 먹고싶" +"_지 않아"+"요.\n"; //0과1랜덤
        sumString += "짜증나"+"요.\n"; //수 계산
        return sumString;
    }

    //TODO 개구리 터치 횟수 카운트 앤 무빙 리액션
    public void touchedFrog(View v) {
        frogTochedCount++;
        showToastString("개구리 찌름");
        foodLogString += "\n";
        switch (frogTochedCount){
            case 1: foodLogString += "왜요?"+"\n"; break;
            case 2: foodLogString += "잘 살아 있다구요."+"\n"; break;
            case 3: foodLogString += "아파요."+"\n"; break;
            case 4: foodLogString += "힘들어요. 그만 찌르세요"+"\n"; break;
            case 5: foodLogString += "죽을 것 같아요."+"\n"; break;
            default: {

                if(random.nextInt(2)==1){
                    foodLogString +="[개구리 죽음]"+"\n";
                    frogTochedCount = 0;
                }else{
                    foodLogString +="[개구리 상태가 이상하다.]"+"\n";
                    frogTochedCount = random.nextInt(6);
                }
            } break;
        }

        foodLog.setText(foodLogString);
        foodLogScroll.fullScroll(View.FOCUS_DOWN);
    }

    public void logEraser(View v){
        foodLogString="Ready...";
        foodLog.setText(foodLogString);
        foodLogScroll.fullScroll(View.FOCUS_DOWN);
    }

    public void showToastString(String text){
        Toast toast = Toast.makeText(
                MainActivity.this,
                text,
                Toast.LENGTH_SHORT
        );
        toast.setGravity(Gravity.CENTER_VERTICAL, 0 , 200);
        toast.show();

    }

}