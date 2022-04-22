package com.haibin.TimeManager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.haibin.TimeManager.R;
import com.haibin.TimeManager.menu.Img;

import java.util.List;

public  class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Img> ImgList;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView Image;

        public ViewHolder(View view) {
            super(view);
            Image = (ImageView) view.findViewById(R.id.item_img);
//            fruitName = (TextView) view.findViewById(R.id.fruitname);
        }

    }

    public RecyclerViewAdapter(List<Img> List) {
        ImgList = List;
    }

    @Override

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.help_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Img img = ImgList.get(position);
        holder.Image.setImageResource(img.getImageId());

    }
    public void notifyDataSetChanged(List<Img> dataList) {
        this.ImgList = dataList;
        super.notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return ImgList.size();
    }
}