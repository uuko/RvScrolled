package com.example.rvscrolled;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener,ActivityBacklistener{

    MyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // data to populate the RecyclerView with
        ArrayList<String> animalNames = new ArrayList<>();
        for (int i=0;i<100;i++){
            animalNames.add("Horse"+i);
        }


        // set up the RecyclerView
        TvRecyclerView recyclerView = findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new TvGridLayoutManager(this,5));
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new MyRecyclerViewAdapter(this, animalNames,recyclerView,this);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecycleviewTop() {
        Button button=findViewById(R.id.btn);
        button.requestFocus();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        View v=getCurrentFocus();
        if (v.getId()==R.id.btn){
            adapter.setPosition(0);
        }
        return super.onKeyDown(keyCode, event);
    }
}
