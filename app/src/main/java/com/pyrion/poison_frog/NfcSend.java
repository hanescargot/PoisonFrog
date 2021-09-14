package com.pyrion.poison_frog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;


import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.util.Log;
import java.io.IOException;
import java.nio.charset.Charset;
public class NfcSend extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntentNFC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_send);
        ImageView iv = findViewById(R.id.iv);

        Animation animation_logo = AnimationUtils.loadAnimation(this, R.anim.frog_finder);
        iv.startAnimation(animation_logo);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intentNFC = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntentNFC = PendingIntent.getActivity(this, 0, intentNFC, 0);


        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntentNFC, null, null);
        }
    }

    @Override
    protected void onPause() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        //write
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            byte[] tagId = tag.getId();
            Toast.makeText(this, "TagID: " + toHexString(tagId), Toast.LENGTH_SHORT).show();
            Log.i("NFC","TagID: " + toHexString(tagId) );
        }

        //끝
//        getIntent().putExtra("fragment_navigation", 2);

    }
    public static final String CHARS = "0123456789ABCDEF";//메시지 내용

    public static String toHexString(byte[] data) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < data.length; ++i) {

            sb.append(CHARS.charAt((data[i] >> 4) & 0x0F))

                    .append(CHARS.charAt(data[i] & 0x0F));

        }
        return sb.toString();
    }
}