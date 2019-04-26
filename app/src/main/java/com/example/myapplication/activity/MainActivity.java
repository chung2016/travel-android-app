package com.example.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.fragment.PlaceFragment;
import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.ApiCall;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static MainActivity mActivity;
    private SharedPreferences mSharedPreferences;
    private Intent intent;
    private ProgressDialog loadingDialog;

    private DrawerLayout drawer;

    private TextView nav_username;
    private TextView nav_email;
    private ImageView nav_image;

    private String jsonWebToken;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = new User();
        mActivity = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        View hView = navigationView.getHeaderView(0);

        nav_username = hView.findViewById(R.id.nav_username);
        nav_email = hView.findViewById(R.id.nav_email);
        nav_image = hView.findViewById(R.id.nav_image);

//        get user jwt
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (mSharedPreferences.contains(Constants.SHARE_KEY_TOKEN)) {
            jsonWebToken = mSharedPreferences.getString(Constants.SHARE_KEY_TOKEN, "");
        }

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new PlaceFragment()).commit();
        }
        // start to get user data
        getCurrentUser();
    }

    public static MainActivity getInstance() {
        return mActivity;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            logout();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        getCurrentUser();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new PlaceFragment()).commit();
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
                            loadingDialog = ProgressDialog
                                    .show(mActivity, "", getResources().getString(R.string.loading), true);
                            if (responseCode == 200) {
                                try {
                                    user.setId(responseJsonBody.get("id").toString());
                                    user.setUsername(responseJsonBody.get("username").toString());
                                    user.setEmail(responseJsonBody.get("email").toString());

                                    if (responseJsonBody.has("image")) {
                                        user.setImage(responseJsonBody.get("image").toString());
                                        Picasso
                                                .get()
                                                .load(user.getImage())
                                                .fit()
                                                .error(R.drawable.error)
                                                .into(nav_image);
                                    }
                                    nav_username.setText(user.getUsername());
                                    nav_email.setText(user.getEmail());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                nav_username.setVisibility(View.VISIBLE);
                                nav_email.setVisibility(View.VISIBLE);
                                nav_image.setVisibility(View.VISIBLE);
                            } else {
                                finish();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Helper.toast(mActivity, getResources().getString(R.string.login_again));
                                    }
                                });
                            }

                            loadingDialog.dismiss();
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

    private void logout() {
        loadingDialog = ProgressDialog
                .show(mActivity, "", getResources().getString(R.string.loading), true);

        nav_username.setVisibility(View.INVISIBLE);
        nav_email.setVisibility(View.INVISIBLE);
        nav_image.setVisibility(View.INVISIBLE);

        SharedPreferences mySPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mySPrefs.edit();
        editor.remove(Constants.SHARE_KEY_TOKEN);
        editor.apply();

        loadingDialog.dismiss();
        finish();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_item_profile:
                intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_item_place:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PlaceFragment()).commit();
                break;
            case R.id.nav_item_logout:
                logout();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miCreate) {
            try {
//                Intent intent = new Intent(mActivity, PlaceEditActivity.class);
////                intent.putExtra("isEditPlace", false);
////                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
