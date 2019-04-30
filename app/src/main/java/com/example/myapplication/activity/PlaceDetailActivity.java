package com.example.myapplication.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.fragment.PlaceFragment;
import com.example.myapplication.models.Place;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.ApiCall;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.Helper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;

import okhttp3.Response;

public class PlaceDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static PlaceDetailActivity mActivity;

    private Place place;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog loadingDialog;
    private String jsonWebToken;

    private TextView tv_created_at;
    private ImageView iv_photo;
    private TextView tv_name;
    private TextView tv_user_gender;
    private TextView tv_location;
    private TextView tv_type;
    private TextView tv_description;
    private TextView tv_author_comment;

    private LinearLayout place_btn_group;
    private Button btn_place_edit;
    private Button btn_place_delete;

    private ImageView iv_user_info_image;
    private TextView tv_user_info_name;
    private LinearLayout user_info_ll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        mActivity = this;
        place = new Place();
        place.setId(getIntent().getExtras().getString("place_id"));
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (mSharedPreferences.contains(Constants.SHARE_KEY_TOKEN)) {
            jsonWebToken = mSharedPreferences.getString(Constants.SHARE_KEY_TOKEN, "");
        }
        loadingDialog = ProgressDialog
                .show(mActivity, "", getResources().getString(R.string.loading), true);
        Bundle bundle = new Bundle();
        bundle.putString("place_id", place.getId());
//        CommentFragment commentFragment = new CommentFragment();
//        commentFragment.setArguments(bundle);
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                commentFragment).commit();

        initView();
        loadPlaceDetail(place.getId());
    }

    public static PlaceDetailActivity getInstance() {
        return mActivity;
    }

    private void initView() {
        tv_created_at = findViewById(R.id.tv_created_at);
        iv_photo = findViewById(R.id.iv_photo);
        tv_name = findViewById(R.id.tv_name);
        tv_location = findViewById(R.id.tv_location);
        tv_type = findViewById(R.id.tv_type);
        tv_description = findViewById(R.id.tv_description);
        tv_author_comment = findViewById(R.id.tv_author_comment);
        tv_user_gender = findViewById(R.id.tv_user_gender);

        place_btn_group = findViewById(R.id.place_btn_group);
        btn_place_edit = findViewById(R.id.btn_place_edit);
        btn_place_delete = findViewById(R.id.btn_place_delete);


        tv_user_info_name = findViewById(R.id.tv_user_info_name);
        iv_user_info_image = findViewById(R.id.iv_user_info_image);
        user_info_ll = findViewById(R.id.user_info_ll);

        user_info_ll.setOnClickListener(this);
        btn_place_edit.setOnClickListener(this);
        btn_place_delete.setOnClickListener(this);
    }

    private void loadPlaceDetail(final String place_id) {

        final String getPlaceUrl = Constants.BASE_URL + "places/" + place_id;
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.show();
                    }
                });


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
                                    getCurrentUser();
                                    try {
                                        Gson gson = new Gson();
                                        place = gson.fromJson(responseBody, new TypeToken<Place>() {
                                        }.getType());

                                        if (place.getPhoto() != null) {
                                            Picasso
                                                    .get()
                                                    .load(place.getPhoto())
                                                    .fit()
                                                    .error(R.drawable.error)
                                                    .into(iv_photo);
                                        }
                                        if (place.getAuthor().getImage() != null) {
                                            Picasso
                                                    .get()
                                                    .load(place.getAuthor().getImage())
                                                    .fit()
                                                    .error(R.drawable.error)
                                                    .into(iv_user_info_image);
                                        } else {
                                            iv_user_info_image.setImageResource(R.mipmap.ic_user_image);
                                        }

                                        if (place.getCreatedAt() != null) {
                                            SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
                                            String dateString = format.format(place.getCreatedAt());
                                            tv_created_at.setText(dateString);
                                        }
                                        tv_user_info_name.setText((place.getAuthor().getUsername() != null) ? place.getAuthor().getUsername() : "");
                                        tv_name.setText((place.getName() != null) ? place.getName() : "");
                                        tv_location.setText((place.getLocation() != null) ? place.getLocation() : "");
                                        tv_type.setText((place.getType() != null) ? place.getType() : "");
                                        tv_description.setText((place.getDescription() != null) ? place.getDescription() : "");
                                        tv_author_comment.setText((place.getAuthorComment() != null) ? place.getAuthorComment() : "");
                                        tv_user_gender.setText((place.getAuthor().getGender() != null) ? place.getAuthor().getGender() : "");


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
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                loadingDialog.dismiss();
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPlaceDetail(this.place.getId());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_info_ll:
//                Helper.toast(mActivity, "go user info");
                break;
            case R.id.btn_place_edit:
                goEdit();
                break;
            case R.id.btn_place_delete:
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.text_confirm))
                        .setMessage(getResources().getString(R.string.confirm_delete))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                goDelete();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

                break;
        }
    }

    public void getCurrentUser() {
        final String getCurrentUser = Constants.BASE_URL + "users/current";

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    final Response response = ApiCall.getHttp(getCurrentUser, jsonWebToken);
                    final String responseBody = response.body().string();
                    final int responseCode = response.code();
                    final JSONObject responseJsonBody = new JSONObject(responseBody);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseCode == 200) {
                                try {
                                    User user = new User();
                                    user.setId(responseJsonBody.get("id").toString());

                                    if (!place.getAuthor().getId().equals(responseJsonBody.get("id").toString())) {
                                        place_btn_group.setVisibility(View.INVISIBLE);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                finish();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Helper.toast(mActivity, getResources().getString(R.string.login_again));
                                    }
                                });
                            }
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


    private void goEdit() {
        Intent intent = new Intent(mActivity, PlaceFormActivity.class);
        intent.putExtra("place_id", this.place.getId());
        intent.putExtra("isEdit", true);
        startActivity(intent);
    }

    private void goDelete() {
        final String deletePlaceUrl = Constants.BASE_URL + "places/" + this.place.getId();
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.show();
                    }
                });
                try {
                    final Response response = ApiCall.deleteHttp(deletePlaceUrl, jsonWebToken);
                    final String responseBody = response.body().string();
                    final int responseCode = response.code();
                    final JSONObject responseJsonBody = new JSONObject(responseBody);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.dismiss();
                            if (responseCode == 200) {
                                finish();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Helper.toast(mActivity, getResources().getString(R.string.delete_success));
                                    }
                                });
                            } else {
                                finish();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Helper.toast(mActivity, getResources().getString(R.string.login_again));
                                    }
                                });
                            }
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
