package com.example.myapplication.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.myapplication.ListView.CommentListAdapter;
import com.example.myapplication.R;
import com.example.myapplication.activity.PlaceDetailActivity;
import com.example.myapplication.models.Comment;
import com.example.myapplication.utils.ApiCall;
import com.example.myapplication.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class CommentFragment extends Fragment {
    private SharedPreferences mSharedPreferences;
    private String place_id;
    private PlaceDetailActivity mActivity;
    private String jsonWebToken;
    private ListView lv_comment_list;
    private ProgressBar process;
    private List<Comment> comments;
    private CommentListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        mActivity = PlaceDetailActivity.getInstance();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        if (mSharedPreferences.contains(Constants.SHARE_KEY_TOKEN)) {
            jsonWebToken = mSharedPreferences.getString(Constants.SHARE_KEY_TOKEN, "");
        }

        lv_comment_list = view.findViewById(R.id.lv_comment_lists);
        process = view.findViewById(R.id.progress);

        if (getArguments() != null) {
            place_id = getArguments().getString("place_id");
        }

        Log.v("place_id", place_id);
        updateCommentList(place_id);

        return view;
    }

    private void updateCommentList(String place_id) {
        final String getCommentListUrl = Constants.BASE_URL + "comments/place/" + place_id;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = ApiCall.getHttp(getCommentListUrl, jsonWebToken);
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    comments = new ArrayList<Comment>();
                    comments = gson.fromJson(responseBody, new TypeToken<List<Comment>>() {}.getType());
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new CommentListAdapter(mActivity, comments);
                            lv_comment_list.setAdapter(adapter);

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
