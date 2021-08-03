package com.pyrion.poison_frog.center.house;

import com.pyrion.poison_frog.center.FragmentCenter;

import com.pyrion.poison_frog.data.OneFrogSet;
import android.content.Context;
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
        //뷰 재활용 함 : convertView
            //convertiView란 화면밖 벗어난 view를 자동으로 참조!!!!!!!!!!!!!
        if( convertView == null ){ //재활용할 뷰가 없음.
            LayoutInflater inflater= LayoutInflater.from(context);

            //재활용 할 뷰로 만들기
            //빈 객체 xml 만들기 인플레이터로
            convertView= inflater.inflate(R.layout.listview_afrog_house, null);
        }

        //기존에 있던 convertView 쓰거나 위에서 새로 만든 프레임을 쓰게 됨
        //생성된 뷰객체에게 값을 설정하는 작업 - bind view
        //항목 뷰 (convertView) 안에 있는  뷰 들 참조하기
        ImageView frogSrc= convertView.findViewById(R.id.frog_src);
        TextView creatorName= convertView.findViewById(R.id.creator_name);
        TextView frogName= convertView.findViewById(R.id.frog_name);
        TextView frogProperty= convertView.findViewById(R.id.frog_property);
        TextView frogSize= convertView.findViewById(R.id.frog_size);
        TextView frogPower= convertView.findViewById(R.id.frog_power);


        //////////////////////////////////////////////////////////
        //설정할 현재번째 데.이.터.를 얻어오기
        OneFrogSet oneFrogSet = this.oneFrogSetList.get(position);


        //각 뷰들에 값 설정!
        if(oneFrogSet.getFrogKey() == FragmentCenter.getSelectedFrogKey()){
            //배경만 추가적으로 바꾸기
        }
        frogSrc.setImageResource( oneFrogSet.getFrogSpecies());
        frogName.setText( oneFrogSet.getFrogName() );
        creatorName.setText("제작자: " + oneFrogSet.getCreatorName());
        frogProperty.setText( "품종: " +(oneFrogSet.getFrogSpecies() ));
        frogSize.setText( "크기: "+(oneFrogSet.getFrogSize() ));
        frogPower.setText( "힘: "+(oneFrogSet.getFrogPower() ));

        //위에서 만들어진 View를 리턴하면 리스트뷰가 이를 보여줌.
        return convertView;
    }
}
