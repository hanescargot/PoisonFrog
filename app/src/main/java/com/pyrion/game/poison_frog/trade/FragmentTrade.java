package com.pyrion.game.poison_frog.trade;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.pyrion.game.poison_frog.Fight.ActivityMatch;
import com.pyrion.game.poison_frog.R;

import org.jetbrains.annotations.NotNull;

public class FragmentTrade extends Fragment {
    AdapterRecyclerViewTrade adapter;

    View view;
    RecyclerView tradeFrogRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Lifecycle:","Fragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("Lifecycle:","Fragment onCreateView");
        view = inflater.inflate(R.layout.fragment_trade_page, container, false);
        tradeFrogRecyclerView = view.findViewById(R.id.trade_frog_recyclerview);
        adapter = new AdapterRecyclerViewTrade(getActivity(), view);
        tradeFrogRecyclerView.setAdapter(adapter);


        ImageView map = view.findViewById(R.id.map);
        ImageView camera = view.findViewById(R.id.camera);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Map
                //지도 배경에 근처 개구리 랜덤으로 보여주기

                //        내 위치 사용에 대한 동적 퍼미션 todo 밖으로 빼기
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int checkResult = getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                    if (checkResult == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                        requestPermissions(permissions, 0);
                    }else{
                        // 위치 정보 제공에 동의한 경우
                        getActivity().getIntent().putExtra("fragment_navigation", 2);
                        Intent intent = new Intent(getActivity(), ActivityMap.class);
                        getActivity().startActivity(intent);
                    }
                }


            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Camera
                //1미터 이내면 개구리 보여주고 터치하면 잡을 수 있기
                //동적퍼미션 작업
                Log.i("camera", "clicked");
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    int permissionResult= getActivity().checkSelfPermission(Manifest.permission.CAMERA);
                    if(permissionResult == PackageManager.PERMISSION_DENIED){
                        String[] permissions= new String[]{Manifest.permission.CAMERA};
                        Toast.makeText(getActivity(), "ask", Toast.LENGTH_SHORT).show();
                        requestPermissions(permissions,10);
                    }else{
                        getActivity().getIntent().putExtra("fragment_navigation", PageNumber);
                        Intent intent = new Intent(getActivity(), ActivityCamera.class);
                        getActivity().startActivity(intent);
                    }
                }

            }
        });


        return view;
    }
    int PageNumber = 2;
    //다이얼로그 선택하면 발동하는 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//         허용안하면 샤용할 수 없도록 하기 앱 설치할 때 부터
        switch (requestCode){
            case 0:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // 위치 정보 제공에 동의한 경우
                    getActivity().getIntent().putExtra("fragment_navigation", 2);
                    Intent intent = new Intent(getActivity(), ActivityMap.class);
                    getActivity().startActivity(intent);
                }else{
                    Toast.makeText(getActivity(), "위치 권한을 허용해 주세요.", Toast.LENGTH_SHORT).show();
                }
                break;

            case 10:
                Toast.makeText(getActivity(), "10", Toast.LENGTH_SHORT).show();
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    getActivity().getIntent().putExtra("fragment_navigation", 2);
                    Intent intent = new Intent(getActivity(), ActivityCamera.class);
                    getActivity().startActivity(intent);
                }else{
                    Toast.makeText(getActivity(), "카메라 권한을 허용해 주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        Log.i("Lifecycle:","Fragment onResume 2page");
        getActivity().getIntent().putExtra("fragment_navigation", 2);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("Lifecycle:","Fragment onViewCreated");
    }

}
