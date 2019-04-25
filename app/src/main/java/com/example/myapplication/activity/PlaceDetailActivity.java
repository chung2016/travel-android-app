package com.example.myapplication.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication.R;

public class PlaceDetailActivity extends AppCompatActivity {
    private TextView tv_place_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        String place_id = getIntent().getExtras().getString("place_id");
        tv_place_id = findViewById(R.id.tv_place_id);
        tv_place_id.setText(place_id);
    }
}
