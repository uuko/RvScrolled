package com.example.rvscrolled;


import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


public abstract class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {
    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
    }


    public abstract void  setBindPosition();
    public abstract void focusIn();

    public abstract void focusOut();
}
