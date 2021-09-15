package com.pyrion.poison_frog.trade;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pyrion.poison_frog.Beam;
import com.pyrion.poison_frog.MainActivity;
import com.pyrion.poison_frog.NfcSend;
import com.pyrion.poison_frog.R;
import com.pyrion.poison_frog.center.Exercise.AlarmReceiver;
import com.pyrion.poison_frog.center.FragmentCenter;
import com.pyrion.poison_frog.data.Frog;
import com.pyrion.poison_frog.data.OneFrogSet;
import com.pyrion.poison_frog.kotlin_nfc_read;
import com.pyrion.poison_frog.kotlin_nfc_write;

import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class AdapterRecyclerViewTrade extends RecyclerView.Adapter {
    Context context;
    ArrayList<OneFrogSet> oneFrogSetList = new ArrayList<>();
    Cursor cursor_frog, cursor_user;
    SQLiteDatabase database_frog, database_user;
    Toast toast;

    ImageView tradeCenterFrog;
    TextView woodNoticeText;
    RecyclerView tradeFrogRecyclerView;

    public AdapterRecyclerViewTrade(Context context, View view){
        this.context = context;
        setOneFrogSetList();
        tradeCenterFrog = view.findViewById(R.id.trade_frog_center_src);
        tradeFrogRecyclerView = view.findViewById(R.id.trade_frog_recyclerview);
        woodNoticeText = view.findViewById(R.id.notice_text);

        tradeCenterFrog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)context).getIntent().putExtra("fragment_navigation", 2);
                Intent nfcIntent;
                OneFrogSet currentFrogSet = oneFrogSetList.get(0);
                if(currentFrogSet.getFrogState() == Frog.STATE_SOLD){
                    //read
                    //NFC 어디냐
                    nfcIntent = new Intent(context, kotlin_nfc_read.class);
                }else{
                    if(currentFrogSet.getFrogState() == Frog.STATE_EXERCISE){
                        //운동중이던 개구리 그냥 운동 캔슬해버리기
                        cancelExercise(currentFrogSet);
                    }
                    //write
                    nfcIntent = new Intent(context, kotlin_nfc_write.class);
                }
                nfcIntent.putExtra("frog_src", currentFrogSet.getFrogSrc()+"");
                context.startActivity(nfcIntent);
            }
        });

        updateListView();
    }

    private void cancelExercise(OneFrogSet selectedFrogSet) {
        AlarmManager alarmManager;
        alarmManager= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        showToastString("운동 취소");
        updateSelectedFrogDB("frog_state", Frog.STATE_ALIVE, selectedFrogSet.getFrogKey());

        Intent intent= new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(context, selectedFrogSet.getFrogKey(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }


    @Override
    public int getItemViewType(int position) {
        OneFrogSet selected_frog = oneFrogSetList.get(position);
        if(selected_frog.getHouseType() == Frog.HOUSE_TYPE_BUY_NEW ){
            return 0;
        }else if(selected_frog.getFrogState() == Frog.STATE_SOLD){
            return 1;
        }
        else{
            return 2;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView;
        if(viewType == 0){
            //Buy new house
            itemView = inflater.inflate(R.layout.listview_house_event_item, parent, false);
            return new ViewHolderEvent(itemView);
        }
        if( viewType == 1){
            //Empty house
            itemView = inflater.inflate(R.layout.listview_house_event_item, parent, false);
            return new ViewHolderEvent(itemView);
        }
        //Not empty house
        itemView = inflater.inflate(R.layout.listview_house_frog_item, parent, false);
        return new ViewHolderFrog(itemView);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OneFrogSet selectedFrogSet = oneFrogSetList.get(position);

        if(selectedFrogSet.getHouseType() == Frog.HOUSE_TYPE_BUY_NEW ){
            //buy new house
            ViewHolderEvent viewHolderEvent = (ViewHolderEvent) holder;
            viewHolderEvent.sellHouseIcon.setVisibility(View.GONE);
            viewHolderEvent.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showIsBuyHouseAlert(selectedFrogSet);
                }
            });

            return;
        }

        if(selectedFrogSet.getFrogState()==Frog.STATE_SOLD) {
            //sold
            ViewHolderEvent viewHolderEvent = (ViewHolderEvent) holder;

            viewHolderEvent.backGround.setBackgroundColor(R.color.black);
            viewHolderEvent.mainSrc.setImageResource(R.drawable.main_gift);
            viewHolderEvent.mainSrc.setPadding(40, 40, 40, 40);
            viewHolderEvent.subSrc.setVisibility(View.GONE);

            viewHolderEvent.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newFrogKey = selectedFrogSet.getFrogKey();
                    upDateUserDB("selected_frog_key", newFrogKey);
                    updateListView();
                }
            });


            if(position == 0){
                //selected event box
                viewHolderEvent.backGround.setBackgroundColor(R.color.purple_200);
            }
            return;
        }


        String stringSpeciesName = Frog.getStringSpecies( selectedFrogSet.getFrogSpecies() );
        //Normal Frog item
        ViewHolderFrog viewHolderFrog = (ViewHolderFrog) holder;

        viewHolderFrog.backGround.setBackgroundColor(R.color.black);
        viewHolderFrog.frogSrc.setImageResource( selectedFrogSet.getFrogSrc());
        Glide.with(context).load( selectedFrogSet.getFrogSrc() ).into(viewHolderFrog.frogSrc);
        viewHolderFrog.frogName.setText( selectedFrogSet.getFrogName() );
        viewHolderFrog.creatorName.setText("제작자: " + selectedFrogSet.getCreatorName());
        viewHolderFrog.frogProperty.setText( "품종: " + stringSpeciesName);
        viewHolderFrog.frogSize.setText( "크기: "+(selectedFrogSet.getFrogSize() ));
        viewHolderFrog.frogPower.setText( "힘: "+(selectedFrogSet.getFrogPower() ));

        if(position == 0){
            //selected frog background
            viewHolderFrog.backGround.setBackgroundColor(R.color.purple_200);
        }
        return;
    }

    @Override
    public int getItemCount() {
        return oneFrogSetList.size();
    }

    class ViewHolderFrog extends RecyclerView.ViewHolder{
        View backGround;
        ImageView frogSrc, sellHouseIcon;
        TextView creatorName, frogName, frogProperty, frogSize, frogPower;
        public ViewHolderFrog(@NonNull View itemView) {
            super(itemView);
            backGround = itemView.findViewById(R.id.click_background);
            frogSrc= itemView.findViewById(R.id.iv_power);
            creatorName= itemView.findViewById(R.id.creator_name);
            frogName= itemView.findViewById(R.id.frog_name);
            frogProperty= itemView.findViewById(R.id.frog_property);
            frogSize= itemView.findViewById(R.id.frog_size);
            frogPower= itemView.findViewById(R.id.frog_power);
            sellHouseIcon = itemView.findViewById(R.id.sell_house_icon);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //select frog
                    OneFrogSet selectedFrogSet = oneFrogSetList.get(getLayoutPosition());
                    int newFrogKey = selectedFrogSet.getFrogKey();
                    upDateUserDB("selected_frog_key", newFrogKey);

                    updateListView();

                }
            });

            sellHouseIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OneFrogSet selectedFrogSet = oneFrogSetList.get(getLayoutPosition());

                    if(oneFrogSetList.size()<3){
                        //Only one house left
                        //can not sell the house
                        //add 다이어로그 집을 팔수 없습니다.
                        showRefuseDialogDialog("마지막 집 판매 불가능");
                    }else{
                        showHouseSellAlertDialog( getLayoutPosition() );
                    }
                }
            });

        }
    }
    private void updateSelectedFrogDB(String dataName ,int data, int frogKey) {
        database_frog = context.openOrCreateDatabase("frogsDB.db", context.MODE_PRIVATE, null);
        database_frog.execSQL("UPDATE frogs_data_set SET "
                +dataName+"=" + data
                + " WHERE frog_key =" + frogKey
        );


    }

    class ViewHolderEvent extends RecyclerView.ViewHolder{
        View backGround;
        ImageView subSrc, mainSrc, sellHouseIcon;

        public ViewHolderEvent(@NonNull View itemView) {
            super(itemView);
            backGround = itemView.findViewById(R.id.click_back_ground);
            mainSrc = itemView.findViewById(R.id.main_house_icon);
            subSrc = itemView.findViewById(R.id.main_buy_icon);
            sellHouseIcon = itemView.findViewById(R.id.sell_house_icon);

            sellHouseIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(oneFrogSetList.size()<3){
                        //Only one house left
                        //can not sell the house
                        showRefuseDialogDialog("마지막 집 판매 불가능");
                    }else{
                        showHouseSellAlertDialog( getLayoutPosition() );
                    }
                }
            });
        }

    }



    void houseSell(int layoutPosition){
        //add summit alert dialog

        int currentFrogKey = oneFrogSetList.get(layoutPosition).getFrogKey();
        database_frog = context.openOrCreateDatabase("frogsDB.db", context.MODE_PRIVATE, null);
        database_frog.execSQL("DELETE FROM frogs_data_set "+
                "WHERE frog_key =" +"'"+currentFrogKey+"'");

        if(layoutPosition == 0){
            //oneFrogSetList 요소는 삭제할 필요없이 mainActivity에서 DB업데이트 할때 적용 됨
            upDateUserDB("selected_frog_key" , (oneFrogSetList.get(1)).getFrogKey());
        }
        showToastString("판매 완료");
        updateListView();
    }


    synchronized void updateListView(){
        setOneFrogSetList();
        setSelectedViews();
        notifyDataSetChanged();
        //
    }

    int getUserDB( String columnName){
        database_user = context.openOrCreateDatabase("userDB.db", context.MODE_PRIVATE, null);
        cursor_user = database_user.rawQuery("SELECT * FROM user_data_set", null);
        cursor_user.moveToNext();

        if(columnName.equals("selected_frog_key")){
            int selectedFrogKey = cursor_user.getInt(cursor_user.getColumnIndex("selected_frog_key"));
            return  (selectedFrogKey);
        }
        if(columnName.equals("user_money")){
            return cursor_user.getInt(cursor_user.getColumnIndex("user_money"));
        }

        return 0;
    }
    void upDateUserDB( String columnName, int newData){
        database_user = context.openOrCreateDatabase("userDB.db", context.MODE_PRIVATE, null);
        cursor_user = database_user.rawQuery("SELECT * FROM user_data_set", null);
        cursor_user.moveToNext();

        if(columnName.equals("selected_frog_key")){
            database_user.execSQL("UPDATE user_data_set SET"
                    +" selected_frog_key = " + newData
            );
        }
        if(columnName.equals("user_money")){
            database_user.execSQL("UPDATE user_data_set SET"
                    +" user_money = " + newData
            );
        }

    }

    void setOneFrogSetList(){
        oneFrogSetList.clear();
        int selectedFrogKey = getUserDB("selected_frog_key");

        database_frog = context.openOrCreateDatabase("frogsDB.db", context.MODE_PRIVATE, null);
        cursor_frog= database_frog.rawQuery("SELECT * FROM frogs_data_set", null);//WHERE절이 없기에 모든 레코드가 검색됨
        if(cursor_frog!=null) {
            while (cursor_frog.moveToNext()) {//[레코드:row]로 커서이동
                int frog_key = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_key"));
                int house_type = cursor_frog.getInt(cursor_frog.getColumnIndex("house_type"));
                String creator_name = cursor_frog.getString(cursor_frog.getColumnIndex("creator_name"));
                String frog_name = cursor_frog.getString(cursor_frog.getColumnIndex("frog_name"));
                int frog_state = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_state"));
                int frog_species = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_species"));
                int frog_size = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_size"));
                int frog_power = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_power"));

                if(frog_key == selectedFrogKey) {
                    oneFrogSetList.add(0, new OneFrogSet(
                            frog_key,
                            house_type,
                            creator_name,
                            frog_name,
                            frog_state,
                            frog_species,
                            frog_size,
                            frog_power));
                    Log.i("hyunju", frog_key+"");
                    continue;
                }
                oneFrogSetList.add(new OneFrogSet(
                        frog_key,
                        house_type,
                        creator_name,
                        frog_name,
                        frog_state,
                        frog_species,
                        frog_size,
                        frog_power));
            }

        }
        //add buy new house event
        oneFrogSetList.add(new OneFrogSet());
    }

    void setSelectedViews(){
//        tradeCenterFrog.setImageResource( oneFrogSetList.get(0).getFrogSrc());
        Glide.with(context).load( oneFrogSetList.get(0).getFrogSrc() ).into(tradeCenterFrog);
        Log.i("trade", oneFrogSetList.get(0).getHouseType()+"");
        if(Frog.STATE_SOLD == oneFrogSetList.get(0).getFrogState()){
            woodNoticeText.setText("근처에 개구리가 나타나면 터치해서 잡으세요.");
        }else{
            woodNoticeText.setText("개구리를 꾹 누르면 근처 사람에게 공유됩니다.");
        }
    }

    void showHouseSellAlertDialog( int layoutPosition){
        //sell house 얼럿 다이어로그
        AlertDialog isHouseSellAlertDialog;
        LayoutInflater inflater = LayoutInflater.from(context);
        AlertDialog.Builder eggSaveOrSellBuilder = new AlertDialog.Builder(context);
        View alertSaveOrSellView = inflater.inflate(R.layout.alert_is_sell_house, null);
        eggSaveOrSellBuilder.setView(alertSaveOrSellView);
        isHouseSellAlertDialog = eggSaveOrSellBuilder.create();

        //다이얼로그의 바깥쪽 영역을 터치했을때 다이얼로그가 사라지지 않도록
        isHouseSellAlertDialog.setCanceledOnTouchOutside(false);

        //button setting
        ImageView bigFrogSrc = alertSaveOrSellView.findViewById(R.id.iv);
        ImageView frogSrc = alertSaveOrSellView.findViewById(R.id.iv_power);
        ImageView cancelBtn = alertSaveOrSellView.findViewById(R.id.cancel_button);
        TextView sellBtn = alertSaveOrSellView.findViewById(R.id.tv_sell);
        TextView tvFrogPrice= alertSaveOrSellView.findViewById(R.id.tv_power);
        TextView tvSumPrice= alertSaveOrSellView.findViewById(R.id.sum);
        TextView frogName = alertSaveOrSellView.findViewById(R.id.frog_name);

        OneFrogSet selectedFrogSet = oneFrogSetList.get(layoutPosition);

        bigFrogSrc.setImageResource(selectedFrogSet.getFrogSrc());
        frogName.setText(selectedFrogSet.getFrogName());
        int frogPrice=0;
        if(selectedFrogSet.getFrogState() == Frog.STATE_ALIVE){
            frogPrice = selectedFrogSet.getFrogSize()+selectedFrogSet.getFrogPower();
            tvFrogPrice.setText(frogPrice+"원");
        }
        if(selectedFrogSet.getFrogState() == Frog.STATE_DEATH){
            frogPrice = (selectedFrogSet.getFrogSize()+selectedFrogSet.getFrogPower())/10;
            tvFrogPrice.setText(frogPrice+"원");
        }
        if(selectedFrogSet.getFrogState() == Frog.STATE_SOLD){
            sellBtn.setText("집 판매");
            frogName.setVisibility(View.INVISIBLE);
            frogPrice = 0;
            tvFrogPrice.setVisibility(View.INVISIBLE);
            frogSrc.setVisibility(View.INVISIBLE);
        }

        int sumPrice = frogPrice+20;
        tvSumPrice.setText("합계: "+sumPrice+"원");

        sellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHouseSellAlertDialog.cancel();

                if(selectedFrogSet.getFrogState() == Frog.STATE_EXERCISE){
                    //운동중이던 개구리 그냥 운동 캔슬해버리기
                    cancelExercise(selectedFrogSet);
                }

                int userMoney = getUserDB("user_money");
                upDateUserDB("user_money", sumPrice + userMoney);
                houseSell(layoutPosition);

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHouseSellAlertDialog.cancel();
            }
        });

        //다이얼로그를 화면에 보이기
        isHouseSellAlertDialog.show();
    }


    void showRefuseDialogDialog( String msg) {
        AlertDialog.Builder refuseBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
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

        alertRefuseView.findViewById(R.id.free_btn).setVisibility(View.INVISIBLE);
        TextView tv = alertRefuseView.findViewById(R.id.tv);
        tv.setText(msg);

        refuseDialog.show();
    }

    void showIsBuyHouseAlert(OneFrogSet selectedFrogSet){
        LayoutInflater inflater = LayoutInflater.from(context);
        AlertDialog.Builder isBuyAlertDialogBuilder = new AlertDialog.Builder(context);
        View viewBuyNewHouse = inflater.inflate(R.layout.alert_is_buy_new_frog,null);
        isBuyAlertDialogBuilder.setView(viewBuyNewHouse);

        AlertDialog isBuyAlertDialog= isBuyAlertDialogBuilder.create();
        isBuyAlertDialog.setCanceledOnTouchOutside(false);
        isBuyAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        isBuyAlertDialog.show();

        ImageView iv = isBuyAlertDialog.findViewById(R.id.iv);
        TextView tv = isBuyAlertDialog.findViewById(R.id.tv);
        TextView price = isBuyAlertDialog.findViewById(R.id.price);

        ImageView cancelBtn = isBuyAlertDialog.findViewById(R.id.cancel_button);
        View payButton = isBuyAlertDialog.findViewById(R.id.pay_button);

        iv.setImageResource(R.drawable.main_house);
        tv.setText("새 집을 구매하시겠습니까?");
        price.setText("-20");

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBuyAlertDialog.cancel();
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database_frog = context.openOrCreateDatabase("frogsDB.db", context.MODE_PRIVATE, null);
                database_frog.execSQL("INSERT INTO frogs_data_set(house_type, creator_name, frog_name, frog_state, frog_species, frog_size, frog_power) VALUES('"
                        + Frog.HOUSE_TYPE_LENT + "','"
                        + FragmentCenter.getUserName() + "','"
                        + Frog.FROG_NAME_NULL + "','"
                        + Frog.STATE_SOLD + "','"
                        + Frog.SPECIES_BASIC + "','"
                        + Frog.SIZE_DEFAULT + "','"
                        + Frog.POWER_DEFAULT + "')"
                );

                cursor_frog= database_frog.rawQuery("SELECT * FROM frogs_data_set", null);//WHERE절이 없기에 모든 레코드가 검색됨
                cursor_frog.moveToLast();
                int newFrogKey = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_key"));
                upDateUserDB("selected_frog_key", newFrogKey);

                updateListView();
                isBuyAlertDialog.cancel();
            }
        });
    }

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

}
