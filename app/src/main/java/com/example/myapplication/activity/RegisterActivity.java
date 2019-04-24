package com.example.myapplication.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.myapplication.R;

import static com.example.myapplication.utils.Validation.validateEmail;
import static com.example.myapplication.utils.Validation.validateFields;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mEtName;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private Button mBtRegister;
    private Button mBtGoLogin;
    private TextInputLayout mTiName;
    private TextInputLayout mTiEmail;
    private TextInputLayout mTiPassword;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
            mTiName.setError("Name should not be empty !");
        }
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
            registerProcess();
        } else {
            showSnackBarMessage("Enter Valid Details !");
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
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void registerProcess() {
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
