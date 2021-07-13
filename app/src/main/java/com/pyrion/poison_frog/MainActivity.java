package com.pyrion.poison_frog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText foodInput;
    TextView foodLog;
    String foodInputSting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        foodInput = findViewById(R.id.food_name_input);
        foodLog = findViewById(R.id.food_log);

        TextView.OnEditorActionListener foodInputActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                System.out.println(event);
                return false;
            }
        };

        foodInput.addTextChangedListener((TextWatcher) foodInputActionListener);



    }



}