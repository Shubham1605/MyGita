package com.shubh.mygita;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

public class ShlokCountActivity extends AppCompatActivity {

    GridView slokGrid;
    MyAdapter myAdapter;
    ArrayList<String>data=new ArrayList<>();
    Integer adhyay_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slok);
        slokGrid = findViewById(R.id.slokGrid);
        myAdapter=new MyAdapter();

        Intent intent = getIntent();
        adhyay_count = intent.getIntExtra("ADHYAY_COUNT",1);
        int shlok_count =Config.shlokList.get(adhyay_count);

        for(int i=1;i<=shlok_count;i++){
          data.add("Shlok "+i);
        }
        slokGrid.setAdapter(myAdapter);
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View slokView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.shlokcountcircle,null);
            TextView title = slokView.findViewById(R.id.title);
            title.setText(Integer.toString(position+1));
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(ShlokCountActivity.this,ShlokContentActivity.class);
                    intent.putExtra("SHLOK_COUNT",position+1);
                    intent.putExtra("ADHYAY_COUNT",adhyay_count);
                    startActivity(intent);
                }
            });
            return slokView;
        }
    }
}
