package com.example.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences mSharedPreferences;

    private EditText mEtName;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private Button mBtRegister;
    private Button mBtGoLogin;
    private TextInputLayout mTiName;
    private TextInputLayout mTiEmail;
    private TextInputLayout mTiPassword;
    private ProgressBar mProgressBar;


    private String jsonWebToken;

    RegisterActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle(R.string.text_register);
        mActivity = this;

        mEtName = (EditText) findViewById(R.id.et_name);
        mEtEmail = (EditText) findViewById(R.id.et_email);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mBtRegister = (Button) findViewById(R.id.btn_register);
        mBtGoLogin = (Button) findViewById(R.id.btn_go_login);
        mTiName = (TextInputLayout) findViewById(R.id.ti_name);
        mTiEmail = (TextInputLayout) findViewById(R.id.ti_email);
        mTiPassword = (TextInputLayout) findViewById(R.id.ti_password);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        mBtRegister.setOnClickListener(this);
        mBtGoLogin.setOnClickListener(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (mSharedPreferences.contains(Constants.SHARE_KEY_TOKEN)) {
            jsonWebToken = mSharedPreferences.getString(Constants.SHARE_KEY_TOKEN, "");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                register();
                break;
            case R.id.btn_go_login:
                goToLogin();
                break;
        }
    }

    private void register() {
        setError();
        String name = mEtName.getText().toString();
        String email = mEtEmail.getText().toString();
        String password = mEtPassword.getText().toString();
        int err = 0;
        if (!validateFields(name)) {
            err++;
            mTiName.setError(getResources().getText(R.string.name_empty));
        }
        if (!validateEmail(email)) {
            err++;
            mTiEmail.setError(getResources().getText(R.string.email_valid));
        }
        if (!validateFields(password)) {
            err++;
            mTiPassword.setError(getResources().getText(R.string.password_empty));
        }
        if (err == 0) {
            disableForm();
            registerProcess(email, password, name);
        } else {
            showSnackBarMessage((String) getResources().getText(R.string.password_empty));
        }
    }

    private void showSnackBarMessage(String message) {
        if (getWindow().getDecorView().findViewById(android.R.id.content) != null) {
            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void setError() {
        mTiEmail.setError(null);
        mTiName.setError(null);
        mTiPassword.setError(null);
    }

    private void goToLogin() {
        finish();
    }

    private void registerProcess(final String email, final String password, final String username) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Helper.isNetworkAvailable(mActivity)) {

                    try {
                        String url = Constants.BASE_URL + "users/register";
                        JSONObject requestJsonBody = new JSONObject();
                        requestJsonBody.put("email", email);
                        requestJsonBody.put("password", password);
                        requestJsonBody.put("username", username);
                        Response response = ApiCall.postHttp(url, requestJsonBody.toString(), jsonWebToken);

                        final int responseCode = response.code();
                        final JSONObject responseJsonBody = new JSONObject(response.body().string());

                        switch (responseCode) {
                            case 200:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String email = responseJsonBody.get("email").toString();
                                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                                            editor.putString(Constants.SHARE_KEY_EMAIL, email);
                                            editor.commit();
                                            Helper.toast(mActivity, getResources().getString(R.string.register_success));
                                            finish();
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
                                        try {
                                            Helper.toast(mActivity, responseJsonBody.get("message").toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
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
        mEtName.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void enableForm() {
        mEtEmail.setEnabled(true);
        mEtPassword.setEnabled(true);
        mEtName.setEnabled(true);
        mProgressBar.setVisibility(View.GONE);
    }
}
