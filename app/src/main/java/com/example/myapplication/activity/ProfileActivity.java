package com.example.myapplication.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.ApiCall;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Response;

import static com.example.myapplication.utils.Validation.validateEmail;
import static com.example.myapplication.utils.Validation.validateFields;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private ProfileActivity mActivity;
    private ProgressDialog loadingDialog;
    private SharedPreferences mSharedPreferences;
    private String jsonWebToken;

    private static final int CAMERA_TAKE_REQUEST = 200;
    private static final int GALLERY_SELECT_REQUEST = 100;

    private Uri uri;
    private File file;
    private String path;
    private Boolean isChangeImage;

    private User user;

    private EditText mEtName;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private Button mBtSubmit;
    private TextInputLayout mTiName;
    private TextInputLayout mTiEmail;
    private TextInputLayout mTiPassword;
    private ImageView iv_profile_edit;
    private RadioButton radio_male;
    private RadioButton radio_female;

    private RelativeLayout rl_profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle(getResources().getString(R.string.text_profile));

        user = new User();

        mActivity = this;
        isChangeImage = false;

        loadingDialog = ProgressDialog.show(mActivity, "",
                getResources().getString(R.string.loading), true);

        mEtName = (EditText) findViewById(R.id.et_name);
        mEtEmail = (EditText) findViewById(R.id.et_email);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mBtSubmit = (Button) findViewById(R.id.btn_submit);
        radio_male = (RadioButton) findViewById(R.id.radio_male);
        radio_female = (RadioButton) findViewById(R.id.radio_female);

        mTiName = (TextInputLayout) findViewById(R.id.ti_name);
        mTiEmail = (TextInputLayout) findViewById(R.id.ti_email);
        mTiPassword = (TextInputLayout) findViewById(R.id.ti_password);

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

    public void setProfile() {
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
                                    user.setId(responseJsonBody.get("id").toString());
                                    user.setUsername(responseJsonBody.get("username").toString());
                                    user.setEmail(responseJsonBody.get("email").toString());
                                    user.setGender(responseJsonBody.get("gender").toString());
                                    if (responseJsonBody.has("image")) {
                                        user.setImage(responseJsonBody.get("image").toString());
                                    }
                                    switch (user.getGender()) {
                                        case "Male":
                                            radio_male.setChecked(true);
                                            break;
                                        case "Female":
                                            radio_female.setChecked(true);
                                            break;
                                    }

                                    mEtEmail.setText(user.getEmail());
                                    mEtName.setText(user.getUsername());
                                    if (user.getImage() != null) {
                                        Picasso
                                                .get()
                                                .load(user.getImage())
                                                .fit()
                                                .error(R.drawable.error)
                                                .into(iv_profile_edit);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
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
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                loadingDialog.dismiss();
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
        pictureDialog.setTitle(getResources().getText(R.string.select_action));
        String[] pictureDialogItems = {
                (String) getResources().getText(R.string.select_from_gallery),
                (String) getResources().getText(R.string.capture_camera)
        };
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

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_male:
                if (checked)
                    user.setGender("Male");
                break;
            case R.id.radio_female:
                if (checked)
                    user.setGender("Female");
                break;
        }
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
        user.setUsername(mEtName.getText().toString());
        user.setEmail(mEtEmail.getText().toString());
        user.setPassword(mEtPassword.getText().toString());
        int err = 0;
        if (!validateFields(user.getUsername())) {
            err++;
            mTiName.setError(getResources().getText(R.string.name_empty));
        }
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
            submitProcess();
        } else {
            showSnackBarMessage((String) getResources().getText(R.string.data_valid));
        }
    }

    private void setError() {
        mTiEmail.setError(null);
        mTiName.setError(null);
        mTiPassword.setError(null);
    }

    private void submitProcess() {


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
                                if (uploadResponseCode != 200) {
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
                                    user.setImage(uploadResponseJsonBody.get("file").toString());
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        String url = Constants.BASE_URL + "users/" + user.getId();
                        JSONObject requestJsonBody = new JSONObject();
                        requestJsonBody.put("email", user.getEmail());
                        requestJsonBody.put("username", user.getUsername());
                        requestJsonBody.put("password", user.getPassword());
                        requestJsonBody.put("gender", user.getGender());

                        if (isChangeImage == true) {
                            requestJsonBody.put("image", user.getImage());
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
        loadingDialog.show();
    }

    private void enableForm() {
        loadingDialog.dismiss();
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
