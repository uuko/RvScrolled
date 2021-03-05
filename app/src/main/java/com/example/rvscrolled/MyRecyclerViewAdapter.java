package com.example.rvscrolled;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyRecyclerViewAdapter extends BaseRecyclerAdapter<MyRecyclerViewAdapter.ViewHolder,String> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    TvRecyclerView tvRecyclerView;
    ActivityBacklistener mActivityBacklistener;
    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<String> data,TvRecyclerView tvRecyclerView,ActivityBacklistener mActivityBacklistener) {
        super(context,data);
        this.tvRecyclerView=tvRecyclerView;
        this.mActivityBacklistener=mActivityBacklistener;
     this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    public void setPosition(int position){
        this.position=position;
    }

    // inflates the row layout from xml when needed
    int position=0;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        final ViewHolder holder=new ViewHolder(view);
        holder.itemView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {

                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:

                            if (! (position%5==0) ){
                                position--;
                            }
                            Log.d("setOnKeyListener", "isLeftEdge = " +position);
//                            if (mRecyclerView.isLeftEdge(position)){
//                                viewHolder.itemView.clearFocus();
//                                mActivityBacklistener.onRecycleviewBack(true);
//                            }

                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:

                            if (! (position%5==4)){
                                position++;
                            }
                            Log.i("setOnKeyListener", "isRightEdge = " +position);
                            break;
                        case KeyEvent.KEYCODE_DPAD_UP:

                            if (!(position/5==0)){
                                position=position-5;
                            }
                            else {
                                holder.itemView.clearFocus();
                                mActivityBacklistener.onRecycleviewTop();
                            }
                            Log.i("setOnKeyListener", "isTopEdge = "+position );
                            break;
                        case KeyEvent.KEYCODE_DPAD_DOWN:

                            if (! (tvRecyclerView.isBottomEdge(position)) ){
                                position=position+5;
                            }
                            Log.i("setOnKeyListener", "isBottomEdge = "+position );
                            break;
                    }
                }
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerViewHolder holder, int position) {

    }



    // binds the data to the TextView in each row

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }
    @Override
    protected void onBindBaseViewHolder(final ViewHolder viewHolder, final int position,List payloads) {
        String animal = mData.get(position);
        viewHolder.myTextView.setText(animal);
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends BaseRecyclerViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvAnimalName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void setBindPosition() {

        }

        @Override
        public void focusIn() {

        }

        @Override
        public void focusOut() {

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}