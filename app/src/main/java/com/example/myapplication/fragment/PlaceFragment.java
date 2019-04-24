package com.example.myapplication.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.myapplication.ListView.PlaceList;
import com.example.myapplication.ListView.PlaceListAdapter;
import com.example.myapplication.R;
import com.example.myapplication.activity.MainActivity;
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
    List<PlaceList> placeLists;
    private String jsonWebToken;
    private PlaceListAdapter adapter;
    private ListView lv_placeList;

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
        updateList();

        return view;
    }

    public void updateList() {
        final String getAllurl = Constants.BASE_URL + "places";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = ApiCall.getHttp(getAllurl, jsonWebToken);
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    placeLists = new ArrayList<>();
                    placeLists = gson.fromJson(responseBody, new TypeToken<List<PlaceList>>() {}.getType());

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new PlaceListAdapter(mActivity, placeLists);
                            lv_placeList.setAdapter(adapter);
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
