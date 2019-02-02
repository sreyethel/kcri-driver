package com.hbidriver.app.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbidriver.app.R;
import com.hbidriver.app.callback.HomeCallback;
import com.hbidriver.app.model.Home;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>{

    private List<Home> list;
    private HomeCallback listener;

    public HomeAdapter(List<Home> list, HomeCallback listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int i) {
        final Home item = list.get(i);
        holder.id.setText(item.getId()+"");
        holder.name.setText(item.getName());

        holder.imageView.setImageResource(item.getImage());
//        Glide.with(holder.itemView.getContext())
//                .load(item.getImage())
//                .into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(item,i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView id, name;
        private ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            id =  itemView.findViewById(R.id.id);
            name = itemView.findViewById(R.id.name);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
