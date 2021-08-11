package com.pyrion.poison_frog.center.house;

import com.bumptech.glide.Glide;
import com.pyrion.poison_frog.center.FragmentCenter;

import com.pyrion.poison_frog.data.Frog;
import com.pyrion.poison_frog.data.OneFrogSet;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pyrion.poison_frog.R;

import java.util.ArrayList;

public class AdapterFrogHouse extends BaseAdapter {
    Context context; //운영체제 대리 참조 변수
    ArrayList<OneFrogSet> oneFrogSetList;
    Cursor cursor_user;
    SQLiteDatabase database_user;

    public AdapterFrogHouse(Context context, ArrayList<OneFrogSet> oneFrogSetList){
        this.context= context;
        this.oneFrogSetList = oneFrogSetList;
    }

    @Override
    public int getCount() {
        //이 아답터가 만들 총 뷰의 개수를 리턴(데이터의 총 개수)
        return oneFrogSetList.size();
    }

    @Override
    public Object getItem(int position) {
        //아답터가 가지고 있는 대량의 데이터 중에 특정한 위치 요소를 리턴
        return oneFrogSetList.get(position);
    }

    @Override
    public long getItemId(int position) {
        //해당 아이템뷰의 포지션 번호 말고 id로 구별자를 만들고 싶을 때 사용
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //////////////////////////////////////////////////////////
        //설정할 현재번째 데.이.터.를 얻어오기
        OneFrogSet oneFrogSet = this.oneFrogSetList.get(position);

        LayoutInflater inflater= LayoutInflater.from(context);

        if(oneFrogSet.getHouseType()==Frog.HOUSE_TYPE_BUY_NEW){
            convertView = inflater.inflate(R.layout.listview_house_event_item, null);
            return convertView;
        }

        if(oneFrogSet.getFrogState()==Frog.STATE_SOLD){
            convertView = inflater.inflate(R.layout.listview_house_event_item, null);
            ImageView mainSrc= convertView.findViewById(R.id.main_house_icon);
            ImageView subSrc= convertView.findViewById(R.id.main_buy_icon);
            mainSrc.setImageResource(R.drawable.main_gift);
            mainSrc.setPadding(80,80,80,80);
            subSrc.setVisibility(View.GONE);

            return convertView;
        }
        convertView= inflater.inflate(R.layout.listview_house_frog_item, null);

        //기존에 있던 convertView 쓰거나 위에서 새로 만든 프레임을 쓰게 됨
        //생성된 뷰객체에게 값을 설정하는 작업 - bind view
        //항목 뷰 (convertView) 안에 있는  뷰 들 참조하기
        ImageView frogSrc= convertView.findViewById(R.id.frog_src);
        TextView creatorName= convertView.findViewById(R.id.creator_name);
        TextView frogName= convertView.findViewById(R.id.frog_name);
        TextView frogProperty= convertView.findViewById(R.id.frog_property);
        TextView frogSize= convertView.findViewById(R.id.frog_size);
        TextView frogPower= convertView.findViewById(R.id.frog_power);

        //현재 선택되어있는 개구리 집
        database_user = context.openOrCreateDatabase("userDB.db", context.MODE_PRIVATE, null);
        cursor_user = database_user.rawQuery("SELECT * FROM user_data_set", null);
        cursor_user.moveToNext();
        int selectedFrogKey = cursor_user.getInt(cursor_user.getColumnIndex("selected_frog_key"));

//        frogSrc.setImageResource( oneFrogSet.getFrogSrc());
        Glide.with(context).load( oneFrogSet.getFrogSrc() ).into(frogSrc);
        frogName.setText( oneFrogSet.getFrogName() );
        creatorName.setText("제작자: " + oneFrogSet.getCreatorName());
        frogProperty.setText( "품종: " +(oneFrogSet.getFrogSpecies() ));
        frogSize.setText( "크기: "+(oneFrogSet.getFrogSize() ));
        frogPower.setText( "힘: "+(oneFrogSet.getFrogPower() ));

        //위에서 만들어진 View를 리턴하면 리스트뷰가 이를 보여줌.
        return convertView;
    }

}
