package com.example.myapplication.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.utils.ApiCall;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.DownLoadImageTask;
import com.example.myapplication.utils.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Response;

import static com.example.myapplication.utils.Validation.validateEmail;
import static com.example.myapplication.utils.Validation.validateFields;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences mSharedPreferences;

    private static final int CAMERA_TAKE_REQUEST = 200;
    private static final int GALLERY_SELECT_REQUEST = 100;

    private Uri uri;
    private File file;
    private String path;
    private Boolean isChangeImage;

    private ProfileActivity mActivity;

    private String jsonWebToken;
    private String userId;
    private String username;
    private String email;
    private String image;

    private EditText mEtName;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private Button mBtSubmit;
    private TextInputLayout mTiName;
    private TextInputLayout mTiEmail;
    private TextInputLayout mTiPassword;
    private ProgressBar mProgressBar;
    private ImageView iv_profile_edit;

    RelativeLayout rl_profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle(getResources().getString(R.string.text_profile));

        mActivity = this;
        isChangeImage = false;

        mEtName = (EditText) findViewById(R.id.et_name);
        mEtEmail = (EditText) findViewById(R.id.et_email);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mBtSubmit = (Button) findViewById(R.id.btn_submit);

        mTiName = (TextInputLayout) findViewById(R.id.ti_name);
        mTiEmail = (TextInputLayout) findViewById(R.id.ti_email);
        mTiPassword = (TextInputLayout) findViewById(R.id.ti_password);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        iv_profile_edit = (ImageView) findViewById(R.id.iv_profile_edit);
        rl_profile_image = (RelativeLayout) findViewById(R.id.rl_profile_image);

        mBtSubmit.setOnClickListener(this);
        rl_profile_image.setOnClickListener(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (mSharedPreferences.contains(Constants.SHARE_KEY_TOKEN)) {
            jsonWebToken = mSharedPreferences.getString(Constants.SHARE_KEY_TOKEN, "");
        }
        setProfile();
    }

    private void setProfile() {
        final String getCurrentUser = Constants.BASE_URL + "users/current";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = ApiCall.getHttp(getCurrentUser, jsonWebToken);
                    final String responseBody = response.body().string();
                    final JSONObject responseJsonBody = new JSONObject(responseBody);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                userId = responseJsonBody.get("id").toString();
                                username = responseJsonBody.get("username").toString();
                                email = responseJsonBody.get("email").toString();

                                mEtEmail.setText(email);
                                mEtName.setText(username);
                                if (responseJsonBody.get("image") != null) {
                                    image = responseJsonBody.get("image").toString();
                                    new DownLoadImageTask(iv_profile_edit).execute(image);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                submit();
                break;
            case R.id.rl_profile_image:
                showPictureDialog();
                break;
        }
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from Gallery",
                "Capture photo from Camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent fintent = new Intent(Intent.ACTION_GET_CONTENT);
                                fintent.setType("image/jpeg");
                                try {
                                    startActivityForResult(fintent, GALLERY_SELECT_REQUEST);
                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 1:
                                launchCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void launchCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        file = new File(Environment.getExternalStorageDirectory(), String.valueOf(System.currentTimeMillis()) + ".jpg");
        uri = FileProvider.getUriForFile(this, "com.travel.android7.fileprovider", file);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);

        startActivityForResult(intent, CAMERA_TAKE_REQUEST);
    }

    private void submit() {
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
            submitProcess(email, password, name);
        } else {
            showSnackBarMessage("Enter Valid Details !");
        }
    }

    private void setError() {
        mTiEmail.setError(null);
        mTiName.setError(null);
        mTiPassword.setError(null);
    }

    private void submitProcess(final String email, final String password, final String username) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Helper.isNetworkAvailable(mActivity)) {
                    try {
                        if (isChangeImage == true) {
                            String uploadUrl = Constants.BASE_URL + "upload";
                            try {
                                Response uploadResponse = ApiCall.postImg(uploadUrl, file, jsonWebToken);
                                int uploadResponseCode = uploadResponse.code();
                                final JSONObject uploadResponseJsonBody = new JSONObject(uploadResponse.body().string());
                                if (uploadResponseCode!=200) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Helper.toast(mActivity, uploadResponseJsonBody.get("message").toString());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            enableForm();
                                        }
                                    });
                                    return;
                                } else {
                                    image = uploadResponseJsonBody.get("file").toString();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        String url = Constants.BASE_URL + "users/" + userId;
                        JSONObject requestJsonBody = new JSONObject();
                        requestJsonBody.put("email", email);
                        requestJsonBody.put("password", password);
                        requestJsonBody.put("username", username);
                        if (isChangeImage == true) {
                            requestJsonBody.put("image", image);
                        }
                        Response response = ApiCall.putHttp(url, requestJsonBody.toString(), jsonWebToken);

                        final int responseCode = response.code();
                        final JSONObject responseJsonBody = new JSONObject(response.body().string());

                        switch (responseCode) {
                            case 200:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Helper.toast(mActivity, getResources().getString(R.string.update_success));
                                        enableForm();
                                        finish();
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

    private void showSnackBarMessage(String message) {
        if (getWindow().getDecorView().findViewById(android.R.id.content) != null) {
            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void disableForm() {
        mEtEmail.setEnabled(false);
        mEtPassword.setEnabled(false);
        mEtName.setEnabled(false);
        mBtSubmit.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void enableForm() {
        mEtEmail.setEnabled(true);
        mEtPassword.setEnabled(true);
        mEtName.setEnabled(true);
        mBtSubmit.setEnabled(true);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_TAKE_REQUEST:
                if (file.exists()) {
                    Uri imageUri = Uri.fromFile(file);

                    Glide.with(mActivity)
                            .load(imageUri)
                            .into(iv_profile_edit);
                }
                path = file.getPath();
                isChangeImage = true;
                break;
            case GALLERY_SELECT_REQUEST:
                if (resultCode == RESULT_OK) {
                    path = getPath(data.getData());
                    file = new File(path);
                    if (file.exists()) {
                        Uri imageUri = Uri.fromFile(file);

                        Glide.with(mActivity)
                                .load(imageUri)
                                .into(iv_profile_edit);
                    }
                }
                isChangeImage = true;
                break;
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}
