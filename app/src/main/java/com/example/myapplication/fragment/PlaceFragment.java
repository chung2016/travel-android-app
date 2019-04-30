package com.example.myapplication.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.myapplication.models.Place;
import com.example.myapplication.ListView.PlaceListAdapter;
import com.example.myapplication.R;
import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.activity.PlaceDetailActivity;
import com.example.myapplication.utils.ApiCall;
import com.example.myapplication.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class PlaceFragment extends Fragment {
    private SharedPreferences mSharedPreferences;
    private MainActivity mActivity;
    private List<Place> places;
    private String jsonWebToken;
    private PlaceListAdapter adapter;
    private ListView lv_placeList;
    private ProgressBar process;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place, container, false);
        mActivity = MainActivity.getInstance();
        mActivity.setTitle(mActivity.getResources().getString(R.string.text_place));
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        if (mSharedPreferences.contains(Constants.SHARE_KEY_TOKEN)) {
            jsonWebToken = mSharedPreferences.getString(Constants.SHARE_KEY_TOKEN, "");
        }
        lv_placeList = view.findViewById(R.id.lv_placeLists);

        process = view.findViewById(R.id.progress);
        updateList();

        lv_placeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mActivity, PlaceDetailActivity.class);
                intent.putExtra("place_id", places.get(position).getId());
                startActivity(intent);
            }
        });

        return view;
    }

    public void updateList() {
        final String getAllUrl = Constants.BASE_URL + "places";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = ApiCall.getHttp(getAllUrl, jsonWebToken);
                    String responseBody = response.body().string();


                    Gson gson = new Gson();
                    places = new ArrayList<Place>();
                    places = gson.fromJson(responseBody, new TypeToken<List<Place>>() {}.getType());

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new PlaceListAdapter(mActivity, places);
                            lv_placeList.setAdapter(adapter);

                            process.setVisibility(View.GONE);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
