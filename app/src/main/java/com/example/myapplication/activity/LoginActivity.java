package com.example.myapplication.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.ApiCall;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

import static com.example.myapplication.utils.Validation.validateEmail;
import static com.example.myapplication.utils.Validation.validateFields;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences mSharedPreferences;
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private ProgressDialog loadingDialog;
    private User user;

    private EditText mEtEmail;
    private EditText mEtPassword;
    private Button mBtLogin;
    private Button mBtGoRegister;
    private TextInputLayout mTiEmail;
    private TextInputLayout mTiPassword;
    private ProgressBar mProgressBar;

    private String jsonWebToken;

    LoginActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.text_login);
        user = new User();
        requestAppPermissions();
        mActivity = this;

        mEtEmail = (EditText) findViewById(R.id.et_email);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mBtLogin = (Button) findViewById(R.id.btn_login);
        mTiEmail = (TextInputLayout) findViewById(R.id.ti_email);
        mTiPassword = (TextInputLayout) findViewById(R.id.ti_password);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mBtGoRegister = (Button) findViewById(R.id.btn_go_register);
        mBtLogin.setOnClickListener(this);
        mBtGoRegister.setOnClickListener(this);


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (mSharedPreferences.contains(Constants.SHARE_KEY_EMAIL)) {
            mEtEmail.setText(mSharedPreferences.getString(Constants.SHARE_KEY_EMAIL, ""));
        }
        if (mSharedPreferences.contains(Constants.SHARE_KEY_TOKEN)) {
            jsonWebToken = mSharedPreferences.getString(Constants.SHARE_KEY_TOKEN, "");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_go_register:
                goToRegister();
                break;
        }
    }

    private void login() {
        setError();
        user.setEmail(mEtEmail.getText().toString());
        user.setPassword(mEtPassword.getText().toString());
        int err = 0;
        if (!validateEmail(user.getEmail())) {
            err++;
            mTiEmail.setError(getResources().getText(R.string.email_valid));
        }
        if (!validateFields(user.getPassword())) {
            err++;
            mTiPassword.setError(getResources().getText(R.string.password_empty));
        }
        if (err == 0) {
            disableForm();
            loginProcess();
        } else {
            showSnackBarMessage((String) getResources().getText(R.string.password_empty));
        }
    }

    private void setError() {
        mTiEmail.setError(null);
        mTiPassword.setError(null);
    }

    private void showSnackBarMessage(String message) {
        if (getWindow().getDecorView().findViewById(android.R.id.content) != null) {
            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void goToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void loginProcess() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Helper.isNetworkAvailable(mActivity)) {
                    try {
                        String url = Constants.BASE_URL + "users/authenticate";
                        JSONObject requestJsonBody = new JSONObject();
                        requestJsonBody.put("email", user.getEmail());
                        requestJsonBody.put("password", user.getPassword());
                        Response response = ApiCall.postHttp(url, requestJsonBody.toString(), jsonWebToken);

                        final int responseCode = response.code();
                        final JSONObject responseJsonBody = new JSONObject(response.body().string());

                        switch (responseCode) {
                            case 200:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            jsonWebToken = responseJsonBody.get("token").toString();
                                            user.setEmail(responseJsonBody.get("email").toString());
                                            user.setUsername(responseJsonBody.get("username").toString());

                                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                                            editor.putString(Constants.SHARE_KEY_TOKEN, jsonWebToken);
                                            editor.putString(Constants.SHARE_KEY_EMAIL, user.getEmail());
                                            editor.commit();

                                            Helper.toast(mActivity, getResources().getString(R.string.login_success));
                                            enableForm();

                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Helper.toast(mActivity, getResources().getString(R.string.login_fail));
                                        enableForm();
                                    }
                                });
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Helper.toast(mActivity, getResources().getString(R.string.network_connection));
                            enableForm();
                        }
                    });
                }
            }
        }).start();
    }

    private void disableForm() {
        loadingDialog = ProgressDialog.show(mActivity, "",
                getResources().getString(R.string.loading), true);
    }

    private void enableForm() {
        loadingDialog.dismiss();
    }

    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (hasReadPermissions() && hasWritePermissions() && hasCameraPermissions() && hasLocationPermissions()) {
            return;
        }
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, ALL_PERMISSIONS_RESULT); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasCameraPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasLocationPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }


}
