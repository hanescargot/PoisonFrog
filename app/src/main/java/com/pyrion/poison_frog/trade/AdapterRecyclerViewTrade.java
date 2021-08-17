package com.pyrion.poison_frog.trade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pyrion.poison_frog.R;
import com.pyrion.poison_frog.data.Frog;
import com.pyrion.poison_frog.data.OneFrogSet;

import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class AdapterRecyclerViewTrade extends RecyclerView.Adapter {
    Context context;
    ArrayList<OneFrogSet> oneFrogSetList = new ArrayList<>();
    Cursor cursor_frog, cursor_user;
    SQLiteDatabase database_frog, database_user;

    ImageView tradeCenterFrog;
    TextView woodNoticeText;
    RecyclerView tradeFrogRecyclerView;

    public AdapterRecyclerViewTrade(Context context, ArrayList<OneFrogSet> oneFrogSetList, View view){
        this.context = context;
        this.oneFrogSetList = oneFrogSetList;

        tradeCenterFrog = view.findViewById(R.id.trade_frog_center_src);
        tradeFrogRecyclerView = view.findViewById(R.id.trade_frog_recyclerview);
        woodNoticeText = view.findViewById(R.id.notice_text);
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
        OneFrogSet selected_frog = oneFrogSetList.get(position);

        if(selected_frog.getHouseType() == Frog.HOUSE_TYPE_BUY_NEW ){
            ViewHolderEvent viewHolderEvent = (ViewHolderEvent) holder;
            viewHolderEvent.sellHouseIcon.setVisibility(View.GONE);
            return;
        }

        if(selected_frog.getFrogState()==Frog.STATE_SOLD) {
            //Event item
            ViewHolderEvent viewHolderEvent = (ViewHolderEvent) holder;

            viewHolderEvent.backGround.setBackgroundColor(R.color.black);
            viewHolderEvent.mainSrc.setImageResource(R.drawable.main_gift);
            viewHolderEvent.mainSrc.setPadding(40, 40, 40, 40);
            viewHolderEvent.subSrc.setVisibility(View.GONE);

            if(position == 0){
                //selected event box
                viewHolderEvent.backGround.setBackgroundColor(R.color.purple_200);
            }
            return;
        }

        //Normal Frog item
        ViewHolderFrog viewHolderFrog = (ViewHolderFrog) holder;

        viewHolderFrog.backGround.setBackgroundColor(R.color.black);
        viewHolderFrog.frogSrc.setImageResource( selected_frog.getFrogSrc());
        Glide.with(context).load( selected_frog.getFrogSrc() ).into(viewHolderFrog.frogSrc);
        viewHolderFrog.frogName.setText( selected_frog.getFrogName() );
        viewHolderFrog.creatorName.setText("제작자: " + selected_frog.getCreatorName());
        viewHolderFrog.frogProperty.setText( "품종: " +(selected_frog.getFrogSpecies() ));
        viewHolderFrog.frogSize.setText( "크기: "+(selected_frog.getFrogSize() ));
        viewHolderFrog.frogPower.setText( "힘: "+(selected_frog.getFrogPower() ));

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
            frogSrc= itemView.findViewById(R.id.frog_src);
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
                    updateNewFrogKeyDB(newFrogKey);


                    //TODO
                    updateListView();

                }
            });

            sellHouseIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //sell house 얼럿 다이어로그
                    sellButtonClicked(getLayoutPosition());
                }
            });

        }
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //EVENT Layout

                    OneFrogSet selectedFrogSet = oneFrogSetList.get(getLayoutPosition());

                    // Buy new frog clicked
                    //TODO 집을 산다는 얼럿 다이어로그 띄우기

                    database_frog = context.openOrCreateDatabase("frogsDB.db", context.MODE_PRIVATE, null);
                    if(selectedFrogSet.getHouseType()== Frog.HOUSE_TYPE_BUY_NEW){
                        database_frog.execSQL("INSERT INTO frogs_data_set(house_type, creator_name, frog_name, frog_state, frog_species, frog_size, frog_power) VALUES('"
                                + Frog.HOUSE_TYPE_LENT + "','"
                                + Frog.USER_NAME_NULL + "','"
                                + Frog.FROG_NAME_NULL + "','"
                                + Frog.STATE_SOLD + "','"
                                + Frog.SPECIES_BASIC + "','"
                                + Frog.SIZE_DEFAULT + "','"
                                + Frog.POWER_DEFAULT + "')"
                        );
                    cursor_frog= database_frog.rawQuery("SELECT * FROM frogs_data_set", null);//WHERE절이 없기에 모든 레코드가 검색됨
                    cursor_frog.moveToLast();
                    int newFrogKey = cursor_frog.getInt(cursor_frog.getColumnIndex("frog_key"));
                        updateNewFrogKeyDB(newFrogKey);
                    }

                    //clicked empty house
                    int newFrogKey = selectedFrogSet.getFrogKey();
                    updateNewFrogKeyDB(newFrogKey);

                    updateListView();
                }
            });

            sellHouseIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sellButtonClicked(getLayoutPosition());
                }
            });
        }

    }

    void updateNewFrogKeyDB(int newFrogKey){
        database_user = context.openOrCreateDatabase("userDB.db", context.MODE_PRIVATE, null);
        database_user.execSQL("UPDATE user_data_set SET"
                +" selected_frog_key = " + newFrogKey
        );
    }

    void sellButtonClicked(int layoutPosition){
        //add summit alert dialog

        if(oneFrogSetList.size()<2){
            //Only one house left
            //can not sell the house
            //add 다이어로그 집을 팔수 없습니다.
            Toast.makeText(context, "팔수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        int currentFrogKey = oneFrogSetList.get(layoutPosition).getFrogKey();
        database_frog = context.openOrCreateDatabase("frogsDB.db", context.MODE_PRIVATE, null);
        database_frog.execSQL("DELETE FROM frogs_data_set "+
                "WHERE frog_key =" +"'"+currentFrogKey+"'");



        if(layoutPosition == 0){
            //oneFrogSetList 요소는 삭제할 필요없이 mainActivity에서 DB업데이트 할때 적용 됨
            updateNewFrogKeyDB(oneFrogSetList.get(1).getFrogKey());
        }

        updateListView();
    }


    synchronized void updateListView(){
        setNewFrogList();
        setSelectedViews();
        notifyDataSetChanged();
        //
    }

    void setNewFrogList(){
        oneFrogSetList.clear();

        database_user = context.openOrCreateDatabase("userDB.db", context.MODE_PRIVATE, null);
        cursor_user = database_user.rawQuery("SELECT * FROM user_data_set", null);
        cursor_user.moveToNext();
        int selectedFrogKey = cursor_user.getInt(cursor_user.getColumnIndex("selected_frog_key"));

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
        tradeCenterFrog.setImageResource( oneFrogSetList.get(0).getFrogSrc());
        Log.i("trade", oneFrogSetList.get(0).getHouseType()+"");
        if(Frog.STATE_SOLD == oneFrogSetList.get(0).getFrogState()){
            woodNoticeText.setText("근처에 개구리가 나타나면 터치해서 잡으세요.");
        }else{
            woodNoticeText.setText("개구리를 꾹 누르면 근처 사람에게 공유됩니다.");
        }
    }

}
