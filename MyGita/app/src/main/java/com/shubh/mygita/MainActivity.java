package com.shubh.mygita;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    GridView adhyayGrid;
    MyAdapter myAdapter;
    ArrayList<String> data=new ArrayList<>();
    Integer adhyay_count;
    public String[] slide_headings = new String[18];

    public String[] slide_desc = new String[18];
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adhyayGrid = findViewById(R.id.adhyayGrid);
        myAdapter=new MyAdapter();

        Intent intent = getIntent();
        adhyay_count = intent.getIntExtra("ADHYAY_COUNT",1);

        for(int i=1;i<=18;i++){
            data.add("Adhyay "+i);
        }

        // instantiateContent();
        for(int i=1;i<19;i++){
            String id1= "adh"+i;
            String id2="adhdes"+i;
            int stringId = getResources().getIdentifier(id1,"string",this.getPackageName());
            String adhyay_count =getResources().getString(stringId);
            slide_headings[i-1] = adhyay_count;

            stringId =  getResources().getIdentifier(id2,"string",this.getPackageName());
            String adhyay_des = getResources().getString(stringId);
            slide_desc[i-1] = adhyay_des;
        }

        adhyayGrid.setAdapter(myAdapter);

    }

    class MyAdapter extends BaseAdapter {

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
            View slokView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.adhyay_layout,null);
            TextView adhyayCount = slokView.findViewById(R.id.adhyayCount);
            TextView adhyayDesc = slokView.findViewById(R.id.adhyayDescription);

            adhyayCount.setText(slide_headings[position]);
            adhyayDesc.setText(slide_desc[position]);

            slokView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ShlokCountActivity.class);
                    intent.putExtra("ADHYAY_COUNT",position+1);
                    startActivity(intent);
                }
            });


            return slokView;
        }
    }
}
