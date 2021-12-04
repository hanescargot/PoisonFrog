package com.pyrion.game.poison_frog.trade;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder holder; // Surfac를 관리하는 공장장 객체

    //카메라 관리자 객체 참조 변수
    Camera camera;

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder= getHolder();//공장장 소환
        holder.addCallback(this);
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //카메라뷰가 화면에 그려낼 준비가 될때

        //카메라 객체 열기
        camera= Camera.open(0); //0: 후방 카메라, 1: 전방 카메라

        //카메라의 미리보기를 실행 전에 몇가지 설정
        try {
            camera.setPreviewDisplay(holder);

            //카메라는 무조건 가로방향임
            //프리뷰를 90도 회전
            camera.setDisplayOrientation(90);
        } catch (IOException e) { e.printStackTrace();}

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        //surfaceCreated()메소드가 종료된 후
        //자동으로 실행되는 메소드

        //마케라 미리보기 시작
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //미리보기 종료
        camera.stopPreview();

        //카메라 닫기
        camera.release();
        camera= null;
    }
}
