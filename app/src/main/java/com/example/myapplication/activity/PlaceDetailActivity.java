package com.example.myapplication.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.R;
import com.example.myapplication.utils.ApiCall;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

public class PlaceDetailActivity extends AppCompatActivity {
    private SharedPreferences mSharedPreferences;
    private String jsonWebToken;
    private PlaceDetailActivity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        mActivity = this;
        String place_id = getIntent().getExtras().getString("place_id");
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (mSharedPreferences.contains(Constants.SHARE_KEY_TOKEN)) {
            jsonWebToken = mSharedPreferences.getString(Constants.SHARE_KEY_TOKEN, "");
        }
        loadPlaceDetail(place_id);
    }

    private void loadPlaceDetail(String place_id) {
        final String getPlaceUrl = Constants.BASE_URL + "places/" + place_id;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = ApiCall.getHttp(getPlaceUrl, jsonWebToken);
                    final String responseBody = response.body().string();
                    final JSONObject responseJsonBody = new JSONObject(responseBody);
                    final int responseCode = response.code();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (responseCode == 200) {
                                    try {
                                        Log.v("response", responseJsonBody.toString());
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        Helper.toast(mActivity, responseJsonBody.get("message").toString());
                                        finish();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }  catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
