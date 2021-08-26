package com.pyrion.poison_frog.egg;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pyrion.poison_frog.MainActivity;
import com.pyrion.poison_frog.R;
import com.pyrion.poison_frog.center.FragmentCenter;
import com.pyrion.poison_frog.data.Egg;
import com.pyrion.poison_frog.data.Frog;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

public class AdapterRecyclerViewEgg extends RecyclerView.Adapter {

    Context context;
    ArrayList eggItemArrayList;
    int userMoney;
    TextView tvUserMoney;
    ImageView eggSrc;

    int selectedItemPrice;
    int newFrogType = 0;

    AlertDialog frogNameAlertDialog;
    EditText newFrogNameEditText;
    String newFrogName;

    LayoutInflater inflater;
    AlertDialog eggSaveOrSellAlertDialog;
    Toast toast;

    //result
    TextView tv ;
    ImageView iv ;


    public AdapterRecyclerViewEgg(Context context, ArrayList eggItemArrayList, int userMoney, TextView tvUserMoney, ImageView eggSrc){
        this.context = context;
        this.eggItemArrayList = eggItemArrayList;
        this.userMoney = userMoney;
        this.tvUserMoney = tvUserMoney;
        this.eggSrc = eggSrc;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.listview_egg_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        String[] oneEggItem = (String[])eggItemArrayList.get(position);

        //0:name  1:price
        viewHolder.tvTitle.setText(oneEggItem[Egg.NAME]);
        viewHolder.ivItem.setImageResource(getEggSrc(oneEggItem[Egg.NAME]));

        viewHolder.btnPrice.setText(oneEggItem[Egg.PRICE]);

        viewHolder.btnPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 돈 빼고 랜덤 개구리 추가하기 / 구매하시겠습니가 yes 취소 간단 얼럿 띄우기 없음 바로 광고 ㄱㄱ
                //TODO 돈 빼고 랜덤 개구리 추가하기 / 구매하시겠습니가 yes 취소 간단 얼럿 띄우기

                String price = oneEggItem[1];
                if(price.equals("Free")) {
                    selectedItemPrice = 0; // no payment
                    getNewRandomFrog("red_box");
                }else{
                    selectedItemPrice = Integer.parseInt(price);
                    if(userMoney<selectedItemPrice) {
                        //돈 부족

                        AlertDialog.Builder refuseBuilder = new AlertDialog.Builder(context);
                        View alertRefuseView = inflater.inflate(R.layout.alert_not_enough_money, null);
                        refuseBuilder.setView(alertRefuseView);

                        AlertDialog refuseDialog = refuseBuilder.create();

                        //다이얼로그의 바깥쪽 영역을 터치했을때 다이얼로그가 사라지지 도록
                        refuseDialog.setCanceledOnTouchOutside(true);
                        //배경 투명하게
                        refuseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

                        ImageView cancelButton = alertRefuseView.findViewById(R.id.cancel_button);
                        cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                refuseDialog.cancel();
                            }
                        });

                        TextView freeBuyButton = alertRefuseView.findViewById(R.id.free_btn);
                        freeBuyButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //buy free Frog
                                getNewRandomFrog("red_box");
                                refuseDialog.cancel();
                            }
                        });

                        refuseDialog.show();
                        return;

                    }else{
                        //정상적으로 구매하기.
                        payment(selectedItemPrice);
                        getNewRandomFrog(oneEggItem[Egg.NAME]);
                    }
                }
            }
        });
    }


    private int getEggSrc(String title) {
        switch (title) {
            case "red_box":
                return (R.drawable.free_dice);
            case "blue_box":
                return (R.drawable.free_dice);
            case "gold_box":
                return (R.drawable.free_dice);
        }
        return (R.drawable.free_dice);
    }

    @Override
    public int getItemCount() {
        return eggItemArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        ImageView ivItem;
        Button btnPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            ivItem = itemView.findViewById(R.id.iv_item);
            btnPrice = itemView.findViewById(R.id.btn_price);
        }
    }

    void payment(int itemPrice){
        userMoney -= itemPrice;

        SQLiteDatabase database_user;
        database_user = context.openOrCreateDatabase("userDB.db", context.MODE_PRIVATE, null);
        database_user.execSQL("UPDATE user_data_set SET"
                +" user_money = " + (userMoney)
        );

        tvUserMoney.setText(userMoney+"");
    }

    void getNewRandomFrog(String eggType){
        newFrogType = 0; // basic frog
        Random random = new Random();
        int randomInt = random.nextInt(100); //0~99

        switch (eggType){
//            default is 0
            case "red_box":
                //확률 10%
                if(0<randomInt && randomInt<11){
                    newFrogType = random.nextInt(3)+2; //개구리 타입 2,3,4
                }
                break;
            case "blue_box":
                //확률 15%
                if(0<randomInt && randomInt<16){
                    newFrogType = random.nextInt(3)+5; //5~7
                }
                break;
            case "gold_box":
                //확률 5%
                if(0<randomInt && randomInt<6){
                    newFrogType = random.nextInt(3)+8; //8~10
                }
                break;

        }

        //TODO 알 깨지는 애니메이션으로 바꾸기
        Glide.with(context).load(R.drawable.dancing_pepe).into(eggSrc);
        eggSrc.animate().alpha(1f).setDuration(2000).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Glide.with(context).load(R.drawable.ic_egg).into(eggSrc);
                //니메이션 끝난 뒤

                AlertDialog.Builder eggSaveOrSellBuilder = new AlertDialog.Builder(context);
                View alertSaveOrSellView = inflater.inflate(R.layout.alert_sell_save_new_egg, null);
                eggSaveOrSellBuilder.setView(alertSaveOrSellView);
                eggSaveOrSellAlertDialog = eggSaveOrSellBuilder.create();

                //다이얼로그의 바깥쪽 영역을 터치했을때 다이얼로그가 사라지지 않도록
                eggSaveOrSellAlertDialog.setCanceledOnTouchOutside(false);
                eggSaveOrSellAlertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if( keyCode == KeyEvent.KEYCODE_BACK){
                            eggSaveOrSellAlertDialog.cancel();

                            showToastString("개구리 창고로 옮겨짐");

                            newFrogName = "아무개구리";
                            showToastString(newFrogName + "생성");
                            addNewFrogDB(newFrogName, newFrogType);
                        }
                        return true;
                    }
                });

                TextView saveButton = alertSaveOrSellView.findViewById(R.id.tv_save);
                TextView sellButton = alertSaveOrSellView.findViewById(R.id.tv_sell);

                saveButton.setOnClickListener(saveEggClickListener);
                sellButton.setOnClickListener(sellEggClickListener);

                tv = alertSaveOrSellView.findViewById(R.id.frog_species);
                iv = alertSaveOrSellView.findViewById(R.id.iv);

                int frogSpecies = Frog.getFrogSpecies(newFrogType);
                tv.setText( Frog.getStringSpecies(frogSpecies) );
                iv.setImageResource( frogSpecies );

                //다이얼로그를 화면에 보이기
                eggSaveOrSellAlertDialog.show();
            }
        });

    }



    View.OnClickListener saveEggClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            eggSaveOrSellAlertDialog.cancel();
            //TODO 개구리 이름 짓기
            AlertDialog.Builder frogNameBuilder = new AlertDialog.Builder(context);
            View alertNewFrogName= inflater.inflate(R.layout.alert_set_new_frog_name, null);
            frogNameBuilder.setView(alertNewFrogName);

            ImageView newFrogNameConfirmButton = alertNewFrogName.findViewById(R.id.main_buy_button);
            newFrogNameEditText = alertNewFrogName.findViewById(R.id.new_frog_name_edit_text);

            frogNameAlertDialog = frogNameBuilder.create();
            frogNameAlertDialog.setCanceledOnTouchOutside(true);
            frogNameAlertDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#00000000")));
            frogNameAlertDialog.setCancelable(false);
            frogNameAlertDialog.show();

            newFrogNameConfirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //buy
                    newFrogName = newFrogNameEditText.getText().toString();
                    showToastString(newFrogName + "생성");
                    addNewFrogDB(newFrogName, newFrogType);

                    frogNameAlertDialog.cancel();
                }
            });


        }
    };



    View.OnClickListener sellEggClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            eggSaveOrSellAlertDialog.cancel();
            payment(-100);
            showToastString("100원");
        }
    };

    public void showToastString(String text){
        if(toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(
                context,
                text,
                Toast.LENGTH_SHORT
        );
        toast.setGravity(Gravity.CENTER_VERTICAL, 0 , 200);
        toast.show();
    }

    void addNewFrogDB(String newFrogName, int newFrogType){
        SQLiteDatabase database_frog;
        database_frog = context.openOrCreateDatabase("frogsDB.db", context.MODE_PRIVATE, null);
        database_frog.execSQL("INSERT INTO frogs_data_set(house_type, creator_name, frog_name, frog_state, frog_species, frog_size, frog_power) VALUES('"
                + Frog.HOUSE_TYPE_LENT + "','"
                + FragmentCenter.getUserName() + "','"
                + newFrogName + "','"
                + Frog.STATE_ALIVE + "','"
                + Frog.getFrogSpecies(newFrogType) + "','"
                + Frog.SIZE_DEFAULT + "','"
                + Frog.POWER_DEFAULT + "')"
        );
    }

}


