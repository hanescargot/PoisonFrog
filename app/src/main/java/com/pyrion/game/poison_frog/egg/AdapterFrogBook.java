package com.pyrion.game.poison_frog.egg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pyrion.game.poison_frog.data.Frog;
import com.pyrion.game.poison_frog.R;


public class AdapterFrogBook extends RecyclerView.Adapter {

    Context context;
    String[] pageList;

    LayoutInflater inflater;
    ImageButton leftBtn, rightBtn;

    public AdapterFrogBook(Context context, String[] pageList, ImageButton leftBtn, ImageButton rightBtn){
                inflater = LayoutInflater.from(context);
        this.context = context;
        this.pageList = pageList;
        this.leftBtn = leftBtn;
        this.rightBtn = rightBtn;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.book_page, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;

        int species = Frog.getFrogSpecies(position);
        String explain = pageList[position];
        Glide.with(context).load( species ).into(viewHolder.iv);

        viewHolder.tvTitle.setText(Frog.getStringSpecies(species));
        viewHolder.tvExplain.setText(explain);
    }

    @Override
    public int getItemCount() {
        return pageList.length;
    }



}

class ViewHolder extends RecyclerView.ViewHolder{
    ImageView iv;
    TextView tvTitle, tvExplain;
    public ViewHolder(View itemView) {
        super(itemView);
        iv = itemView.findViewById(R.id.iv_frog);
        tvTitle = itemView.findViewById(R.id.title);
        tvExplain = itemView.findViewById(R.id.explain);
    }
}
