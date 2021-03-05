package com.example.rvscrolled;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public abstract class BaseRecyclerAdapter<VH extends BaseRecyclerViewHolder, T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {

    public LayoutInflater mInflate;

    public List<T> mDataList;

    public int position;
    public BaseRecyclerAdapter(Context context, List<T> dataList) {
        this.mInflate = LayoutInflater.from(context);
        this.mDataList = dataList;
        setHasStableIds(true);
    }



    public void setDataList(List<T> dataList) {
        this.mDataList = dataList;
    }

    public void addDataList(List<T> dataList) {
        this.mDataList.addAll(dataList);
    }


    @Override
    public void onViewAttachedToWindow(@NonNull BaseRecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);

//        if (holder.getAdapterPosition()==0 && (focusOnOne)){
//            holder.itemView.requestFocus();
//        }
        if (holder instanceof  BaseRecyclerViewHolder){
            Log.d("7777777777", "rrrrrrrrr: ");
            (holder).setBindPosition();
            //  holder.itemView.requestFocus();

        }
    }
    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public List<T> getDataList() {
        return mDataList;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position, List payloads) {
        this.position=position;
        onBindBaseViewHolder((VH) holder, position,payloads);
    }

    protected abstract void onBindBaseViewHolder(VH viewHolder, int position, List payloads);


    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }



}
