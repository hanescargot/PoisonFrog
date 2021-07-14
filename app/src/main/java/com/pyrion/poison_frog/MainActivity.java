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

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    EditText foodInputEditText;
    TextView foodLog;
    String foodLogString = "";
    ScrollView foodLogScroll;
    Stack<String> originFoodName = new Stack<>();
    //생성자만, 매소드 호출이 안됨
    Toast ateNotice;

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
                    ateNotice = Toast.makeText(
                            MainActivity.this,
                            ("개구리가 "+newFoodName+"을 먹었습니다."),
                            Toast.LENGTH_SHORT
                    );
                    ateNotice.show();
                    ateNotice.setGravity(Gravity.CENTER_VERTICAL, 0 , 200);

                    if(foodLogString.length()>0){
                        foodLogString += "\n--------------------------------\n\n";
                    }
                    foodLogString += getNewFoodLogString(newFoodName);
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
    public void hideKeyboard(View v) {
    }


}