package com.pyrion.poison_frog.trade;

import android.content.Context;
import android.content.Intent;
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
import com.pyrion.poison_frog.MainActivity;
import com.pyrion.poison_frog.R;
import com.pyrion.poison_frog.center.FragmentCenter;
import com.pyrion.poison_frog.data.Frog;
import com.pyrion.poison_frog.data.OneFrogSet;

import java.util.ArrayList;

public class AdapterRecyclerViewTrade extends RecyclerView.Adapter {
    Context context;
    ArrayList<OneFrogSet> oneFrogSetList;

    public AdapterRecyclerViewTrade(Context context, ArrayList<OneFrogSet> oneFrogSetList){
        this.context = context;
        this.oneFrogSetList = oneFrogSetList;
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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OneFrogSet selected_frog = oneFrogSetList.get(position);



        Log.i("adapter", selected_frog.getHouseType()+"");
        if(selected_frog.getHouseType() == Frog.HOUSE_TYPE_BUY_NEW ){
            ViewHolderEvent viewHolderEvent = (ViewHolderEvent) holder;
            return;
        }

        if(selected_frog.getFrogState()==Frog.STATE_SOLD) {
            ViewHolderEvent viewHolderEvent = (ViewHolderEvent) holder;
            viewHolderEvent.mainSrc.setImageResource(R.drawable.main_gift);
            viewHolderEvent.mainSrc.setPadding(80, 80, 80, 80);
            viewHolderEvent.subSrc.setVisibility(View.GONE);
            return;
        }

        ViewHolderFrog viewHolderFrog = (ViewHolderFrog) holder;
        if(selected_frog.getFrogKey() == FragmentCenter.getSelectedFrogKey()){

            //TODO KEY PUBLIC 된 것 바꾸기  꼭.. .
            //현재 선택된 녀석이니까 배경만 추가적으로 바꾸기
        }

        //Normal Frogs
        viewHolderFrog.frogSrc.setImageResource( selected_frog.getFrogSrc());
        Glide.with(context).load( selected_frog.getFrogSrc() ).into(viewHolderFrog.frogSrc);
        viewHolderFrog.frogName.setText( selected_frog.getFrogName() );
        viewHolderFrog.creatorName.setText("제작자: " + selected_frog.getCreatorName());
        viewHolderFrog.frogProperty.setText( "품종: " +(selected_frog.getFrogSpecies() ));
        viewHolderFrog.frogSize.setText( "크기: "+(selected_frog.getFrogSize() ));
        viewHolderFrog.frogPower.setText( "힘: "+(selected_frog.getFrogPower() ));
        return;
    }

    @Override
    public int getItemCount() {
        return oneFrogSetList.size();
    }

    class ViewHolderFrog extends RecyclerView.ViewHolder{
        View backGround;
        ImageView frogSrc;
        TextView creatorName;
        TextView frogName;
        TextView frogProperty;
        TextView frogSize;
        TextView frogPower;

        public ViewHolderFrog(@NonNull View itemView) {
            super(itemView);
            backGround = itemView.findViewById(R.id.back_ground);
            frogSrc= itemView.findViewById(R.id.frog_src);
            creatorName= itemView.findViewById(R.id.creator_name);
            frogName= itemView.findViewById(R.id.frog_name);
            frogProperty= itemView.findViewById(R.id.frog_property);
            frogSize= itemView.findViewById(R.id.frog_size);
            frogPower= itemView.findViewById(R.id.frog_power);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    OneFrogSet selected_frog = oneFrogSetList.get(getLayoutPosition());

                    Intent intent= new Intent(context, MainActivity.class);
//                    intent.putExtra("selected_frog_key", selected_frog.getFrogKey());
//to-do db 업데이트, 얼럿다이어로그 띄우기, 센터 개구리 이미지 바꾸기
                    context.startActivity(intent);
                }
            });

        }
    }

    class ViewHolderEvent extends RecyclerView.ViewHolder{
        ImageView mainSrc;
        ImageView subSrc;

        public ViewHolderEvent(@NonNull View itemView) {
            super(itemView);
            mainSrc= itemView.findViewById(R.id.main_house_icon);
            subSrc= itemView.findViewById(R.id.main_buy_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    OneFrogSet selected_frog = oneFrogSetList.get(getLayoutPosition());
                    Intent intent= new Intent(context, MainActivity.class);

                    intent.putExtra("new_frog_house_type", selected_frog.getHouseType());

                    context.startActivity(intent);
                }
            });

        }
    }
}
