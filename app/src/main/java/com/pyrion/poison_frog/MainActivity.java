package com.pyrion.poison_frog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    EditText foodInput;
    TextView foodLog;
    String foodInputSting = "";
    ScrollView foodLogScroll;
    Stack<String> originFoodName = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        foodInput = findViewById(R.id.food_name_input);
        foodLog = findViewById(R.id.food_log);
        foodLogScroll = findViewById(R.id.log_scroll);

        foodInput.setOnEditorActionListener(foodInputActionListener);


    }


    TextView.OnEditorActionListener foodInputActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if(event == null){
                foodInputSting += v.getText().toString() + "\n";
                originFoodName.add(v.getText().toString());
                ////

                foodInputSting += "옴뇸뇸뇸 맛있는"+originFoodName.pop()+"네요." + "\n";

                ///
                foodLog.setText(foodInputSting);
                foodLogScroll.fullScroll(View.FOCUS_DOWN);
                v.setText("");

            }
            return false;
        }
    };
}