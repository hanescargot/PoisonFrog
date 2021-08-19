package com.pyrion.poison_frog.egg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pyrion.poison_frog.R;
import com.pyrion.poison_frog.data.Frog;
import com.pyrion.poison_frog.data.OneFrogSet;
import com.pyrion.poison_frog.trade.AdapterRecyclerViewTrade;

import java.util.ArrayList;

public class AdapterRecyclerViewEgg extends RecyclerView.Adapter {

    Context context;
    ArrayList eggItemArrayList;

    public AdapterRecyclerViewEgg(Context context, ArrayList eggItemArrayList){
        this.context = context;
        this.eggItemArrayList = eggItemArrayList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.listview_egg_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        String[] oneEggItem = (String[])eggItemArrayList.get(position);

        viewHolder.tvTitle.setText(oneEggItem[0]);
        viewHolder.ivItem.setImageResource(getEggSrc(oneEggItem[0]));
        viewHolder.btnPrice.setText(oneEggItem[1]);

        viewHolder.btnPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 돈 빼고 랜덤 개구리 추가하기 / 구매하시겠습니가 yes 취소 간단 얼럿 띄우기
                //TODO 돈 빼고 랜덤 개구리 추가하기 / 구매하시겠습니가 yes 취소 간단 얼럿 띄우기
                //사고나면 중앙 알 그림 셋팅후 깨지는 애니메이션 후에 얼럿 띄우기
                //상세 개구리 스펙 밑에 센터에 버튼 두개/ 개구리 판매하기 하면 돈으로 바로 / 창고로 넣기 하면 창고로
                Toast.makeText(context, oneEggItem[0]+"", Toast.LENGTH_SHORT).show();
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
}


