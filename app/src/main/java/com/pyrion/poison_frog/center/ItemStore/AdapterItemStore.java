package com.pyrion.poison_frog.center.ItemStore;

import com.bumptech.glide.Glide;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pyrion.poison_frog.R;
import com.pyrion.poison_frog.data.OneItemSet;

import java.util.ArrayList;

public class AdapterItemStore extends BaseAdapter {
    Context context; //운영체제 대리 참조 변수
    ArrayList<OneItemSet> oneItemSetList;
    SQLiteDatabase database_item, database_user;

    int currentUserMoney;

    public AdapterItemStore(Context context, ArrayList<OneItemSet> oneItemSetList, int currentUserMoney){
        this.context= context;
        this.oneItemSetList = oneItemSetList;
        this.currentUserMoney = currentUserMoney;
    }

    @Override
    public int getCount() {
        //이 아답터가 만들 총 뷰의 개수를 리턴(데이터의 총 개수)
        return oneItemSetList.size();
    }

    @Override
    public Object getItem(int position) {
        //아답터가 가지고 있는 대량의 데이터 중에 특정한 위치 요소를 리턴
        return oneItemSetList.get(position);
    }

    @Override
    public long getItemId(int position) {
        //해당 아이템뷰의 포지션 번호 말고 id로 구별자를 만들고 싶을 때 사용
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OneItemSet currentOneItemSet;
        currentOneItemSet = this.oneItemSetList.get(position);


        if(convertView == null){
            LayoutInflater inflater= LayoutInflater.from(context);
            convertView= inflater.inflate(R.layout.listview_store_item, null);
        }
        //기존에 있던 convertView 쓰거나 위에서 새로 만든 프레임을 쓰게 됨
        //생성된 뷰객체에게 값을 설정하는 작업 - bind view
        //항목 뷰 (convertView) 안에 있는  뷰 들 참조하기
        ImageView itemSrc= convertView.findViewById(R.id.item_src);
        TextView itemName= convertView.findViewById(R.id.item_name);
        TextView explain= convertView.findViewById(R.id.explain);
        TextView itemLevel= convertView.findViewById(R.id.item_level);
        Button cashButton= convertView.findViewById(R.id.cash);
        Button adButton= convertView.findViewById(R.id.AD);

        Glide.with(context).load( currentOneItemSet.getItemSrc(currentOneItemSet.getItemName()) ).into(itemSrc);
        itemName.setText( currentOneItemSet.getItemName() );
        explain.setText( currentOneItemSet.getItemExplain() );
        itemLevel.setText( currentOneItemSet.getCurrentLevel()+"") ;
        cashButton.setText (currentOneItemSet.getItemPrice()+"");
        cashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cash
                int itemPrice = currentOneItemSet.getItemPrice();
                if(itemPrice>currentUserMoney){
                    //TODO 아이템 구매 불가
                    return;
                }

                payment(itemPrice);
                upgradeItem(
                        position,
                        currentOneItemSet.getItemName(),
                        currentOneItemSet.getCurrentLevel(),
                        currentOneItemSet.getItemPrice(),
                        currentOneItemSet.getUpgradePriceTimes()
                );

            }
        });
        adButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View v) {
                // TODO 리워드 광고 보여주기
                upgradeItem(
                        position,
                        currentOneItemSet.getItemName(),
                        currentOneItemSet.getCurrentLevel(),
                        currentOneItemSet.getItemPrice(),
                        currentOneItemSet.getUpgradePriceTimes()
                );

            }
        });
        return convertView;
    }


    void payment(int itemPrice){
        database_user = context.openOrCreateDatabase("userDB.db", context.MODE_PRIVATE, null);
        database_user.execSQL("UPDATE user_data_set SET"
                +" user_money = " + (currentUserMoney - itemPrice)
        );
    }

    synchronized void upgradeItem(int position, String itemName, int itemLevel, int itemPrice, double upgradeTimes){
        int currentItemPrice = (int)(itemPrice * upgradeTimes);
        Log.i("price", currentItemPrice+"");
        database_item = context.openOrCreateDatabase("itemDB.db", context.MODE_PRIVATE, null);
        database_item.execSQL("UPDATE item_data_set SET"
                +" current_level =" + "'"+(itemLevel+1)+"'"
                +", current_item_price =" + "'"+currentItemPrice+"'"
                +" WHERE item_name ="+"'" + itemName +"'"
        );

        OneItemSet currentOneItemSet;
        currentOneItemSet = this.oneItemSetList.get(position);
        currentOneItemSet.setCurrentLevel(itemLevel+1);
        currentOneItemSet.setItemPrice(currentItemPrice);

        //data list 초기화 하고 값 갱신하는것 잊지 말것
        notifyDataSetChanged();

    }
}
