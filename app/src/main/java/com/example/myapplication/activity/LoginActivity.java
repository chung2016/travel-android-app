package com.example.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.myapplication.R;
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
        String email = mEtEmail.getText().toString();
        String password = mEtPassword.getText().toString();
        int err = 0;
        if (!validateEmail(email)) {
            err++;
            mTiEmail.setError("Email should be valid !");
        }
        if (!validateFields(password)) {
            err++;
            mTiPassword.setError("Password should not be empty !");
        }
        if (err == 0) {
            disableForm();
            loginProcess(email, password);
        } else {
            showSnackBarMessage("Enter Valid Details !");
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

    private void loginProcess(final String email, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Helper.isNetworkAvailable(mActivity)) {

                    try {
                        String url = Constants.BASE_URL + "users/authenticate";
                        JSONObject requestJsonBody = new JSONObject();
                        requestJsonBody.put("email", email);
                        requestJsonBody.put("password", password);
                        Response response = ApiCall.postHttp(url, requestJsonBody.toString(), jsonWebToken);

                        final int responseCode = response.code();
                        final JSONObject responseJsonBody = new JSONObject(response.body().string());

                        switch (responseCode) {
                            case 200:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String jsonWebToken = responseJsonBody.get("token").toString();
                                            String email = responseJsonBody.get("email").toString();
                                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                                            editor.putString(Constants.SHARE_KEY_TOKEN, jsonWebToken);
                                            editor.putString(Constants.SHARE_KEY_EMAIL, email);
                                            editor.commit();
                                            Helper.toast(mActivity, jsonWebToken);

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
        mEtEmail.setEnabled(false);
        mEtPassword.setEnabled(false);
        mBtLogin.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void enableForm() {
        mEtEmail.setEnabled(true);
        mEtPassword.setEnabled(true);
        mBtLogin.setEnabled(true);
        mProgressBar.setVisibility(View.GONE);
    }

}
