package com.pyrion.poison_frog.trade;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.pyrion.poison_frog.Beam;
import com.pyrion.poison_frog.MainActivity;
import com.pyrion.poison_frog.NfcSend;
import com.pyrion.poison_frog.R;
import com.pyrion.poison_frog.data.Frog;
import com.pyrion.poison_frog.data.OneFrogSet;

import java.util.ArrayList;

import static android.nfc.NdefRecord.createMime;

public class FragmentTrade extends Fragment {
    AdapterRecyclerViewTrade adapter;

    View view;
    RecyclerView tradeFrogRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_trade_page, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        tradeFrogRecyclerView = view.findViewById(R.id.trade_frog_recyclerview);
        adapter = new AdapterRecyclerViewTrade(getActivity(), view);
        tradeFrogRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}
