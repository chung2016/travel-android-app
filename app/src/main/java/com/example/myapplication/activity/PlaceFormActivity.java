package com.example.myapplication.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.models.Place;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.ApiCall;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Response;

import static com.example.myapplication.utils.Validation.validateFields;

public class PlaceFormActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {
    private Uri uri;
    private File file;
    private String path;
    private static final int CAMERA_TAKE_REQUEST = 200;
    private static final int GALLERY_SELECT_REQUEST = 100;

    private Boolean isChangeImage;

    private String jsonWebToken;
    private ProgressDialog loadingDialog;
    private Place place;
    private static PlaceFormActivity mActivity;
    private SharedPreferences mSharedPreferences;
    private JSONObject requestJsonBody;

    private RelativeLayout rl_place_image;
    private ImageView iv_place_image;
    private Button get_current_location;

    private Geocoder geocoder;

    private EditText et_place_name;
    private EditText et_place_location;
    private EditText et_description;
    private EditText et_author_comment;
    private Button btn_submit;
    private Spinner s_place_type;

    private TextInputLayout til_place_name;
    private TextInputLayout til_place_location;

    private Boolean isEdit;

    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_form);

        mActivity = this;
        this.isEdit = getIntent().getExtras().getBoolean("isEdit");
        requestJsonBody = new JSONObject();

        isChangeImage = false;

        geocoder = new Geocoder(this, Locale.getDefault());
        loadingDialog = ProgressDialog
                .show(mActivity, "", getResources().getString(R.string.loading), true);
        place = new Place();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (mSharedPreferences.contains(Constants.SHARE_KEY_TOKEN)) {
            jsonWebToken = mSharedPreferences.getString(Constants.SHARE_KEY_TOKEN, "");
        }


        initViewForm();
        if (this.isEdit == true) {
            this.place.setId(getIntent().getExtras().getString("place_id"));
            getPlaceById();
        }

        if (this.isEdit == false) {
            setTitle(getResources().getText(R.string.text_add) + " " + getResources().getText(R.string.text_place));
        } else {
            setTitle(getResources().getText(R.string.text_edit) + " " + getResources().getText(R.string.text_place));
        }
    }

    private void getPlaceById() {
        final String getPlaceUrl = Constants.BASE_URL + "places/" + this.place.getId();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response response = ApiCall.getHttp(getPlaceUrl, jsonWebToken);
                    final String responseBody = response.body().string();
                    final int responseCode = response.code();
                    final JSONObject responseJsonBody = new JSONObject(responseBody);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseCode == 200) {
                                try {
                                    place.setId(responseJsonBody.get("id").toString());
                                    place.setName(responseJsonBody.get("name").toString());
                                    place.setLocation(responseJsonBody.get("location").toString());
                                    place.setDescription(responseJsonBody.has("description") ? responseJsonBody.get("description").toString() : "");
                                    place.setAuthorComment(responseJsonBody.has("authorComment") ? responseJsonBody.get("authorComment").toString(): "");
                                    place.setType(responseJsonBody.has("type") ? responseJsonBody.get("type").toString() : "");
                                    if (responseJsonBody.has("photo")) {
                                        place.setPhoto(responseJsonBody.get("photo").toString());
                                    }

                                    et_place_name.setText(place.getName());
                                    et_place_location.setText(place.getLocation());
                                    et_description.setText(place.getDescription());
                                    et_author_comment.setText(place.getAuthorComment());
                                    ArrayAdapter myAdap = (ArrayAdapter) s_place_type.getAdapter();
                                    int spinnerPosition = myAdap.getPosition(place.getType());
                                    s_place_type.setSelection(spinnerPosition);

                                    if (place.getPhoto() != null) {
                                        Picasso
                                                .get()
                                                .load(place.getPhoto())
                                                .fit()
                                                .error(R.drawable.error)
                                                .into(iv_place_image);
                                    }
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

    public static PlaceFormActivity getInstance() {
        return mActivity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                submitForm();
                break;
            case R.id.rl_place_image:
                showPictureDialog();
                break;
            case R.id.get_current_location:
                getCurrentLocation();
                break;
        }
    }

    private void getCurrentLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
                            loadingDialog.show();
                            if (responseCode == 200) {
                                try {
                                    User user = new User();
                                    user.setId(responseJsonBody.get("id").toString());
                                    place.setAuthor(user);

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

    private void initViewForm() {
        rl_place_image = findViewById(R.id.rl_place_image);
        iv_place_image = findViewById(R.id.iv_place_image);

        et_place_name = findViewById(R.id.et_place_name);
        et_place_location = findViewById(R.id.et_place_location);
        s_place_type = findViewById(R.id.s_place_type);
        et_description = findViewById(R.id.et_description);
        et_author_comment = findViewById(R.id.et_author_comment);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        rl_place_image.setOnClickListener(this);

        get_current_location = findViewById(R.id.get_current_location);
        get_current_location.setOnClickListener(this);

        til_place_name = findViewById(R.id.til_place_name);
        til_place_location = findViewById(R.id.til_place_location);

        getCurrentUser();
    }

    private void submitForm() {
        setError();
        place.setName(et_place_name.getText().toString());
        place.setLocation(et_place_location.getText().toString());
        place.setType(s_place_type.getSelectedItem().toString());
        place.setDescription(et_description.getText().toString());
        place.setAuthorComment(et_author_comment.getText().toString());

        int err = 0;
        if (!validateFields(place.getName())) {
            err++;
            til_place_name.setError(getResources().getText(R.string.name_empty));
        }
        if (!validateFields(place.getLocation())) {
            err++;
            til_place_location.setError(getResources().getText(R.string.location_empty));
        }
        if (err == 0) {
            processForm();
        } else {
            showSnackBarMessage((String) getResources().getText(R.string.data_valid));
        }
    }

    private void setError() {
        til_place_name.setError(null);
        til_place_location.setError(null);
    }

    private void showSnackBarMessage(String message) {
        if (getWindow().getDecorView().findViewById(android.R.id.content) != null) {
            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void processForm() {
        try {
            requestJsonBody.put("name", place.getName());
            requestJsonBody.put("location", place.getLocation());
            requestJsonBody.put("type", place.getType());
            requestJsonBody.put("author", place.getAuthor().getId());
            requestJsonBody.put("description", place.getDescription());
            requestJsonBody.put("authorComment", place.getAuthorComment());

            managePlace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void managePlace() {
        String managePlaceUrl = Constants.BASE_URL + "places";
        managePlaceUrl += (isEdit == true) ? "/" + this.place.getId() : "";
        final String finalManagePlaceUrl = managePlaceUrl;


        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            loadingDialog.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                try {
                    if (isChangeImage == true) {
                        uploadPlaceImage();
                        loadingDialog.dismiss();
                    }
                    Response response;
                    if (isEdit == true) {
                        response = ApiCall.putHttp(finalManagePlaceUrl, requestJsonBody.toString(), jsonWebToken);
                    } else {
                        response = ApiCall.postHttp(finalManagePlaceUrl, requestJsonBody.toString(), jsonWebToken);
                    }
                    final int responseCode = response.code();
                    final JSONObject responseJsonBody = new JSONObject(response.body().string());
                    if (responseCode == 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isEdit) {
                                    Helper.toast(mActivity, getResources().getString(R.string.update_success));
                                } else {
                                    Helper.toast(mActivity, getResources().getString(R.string.add_success));
                                }
                                finish();
                                try {
                                    place.setId(responseJsonBody.get("id").toString());
                                    goToShowPlace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Helper.toast(mActivity, responseJsonBody.get("message").toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            loadingDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
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

    private void launchCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        file = new File(Environment.getExternalStorageDirectory(), String.valueOf(System.currentTimeMillis()) + ".jpg");
        uri = FileProvider.getUriForFile(this, "com.travel.android7.fileprovider", file);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);

        startActivityForResult(intent, CAMERA_TAKE_REQUEST);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_TAKE_REQUEST:
                if (file.exists()) {
                    Uri imageUri = Uri.fromFile(file);

                    Glide.with(mActivity)
                            .load(imageUri)
                            .into(iv_place_image);
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
                                .into(iv_place_image);
                    }
                }
                isChangeImage = true;
                break;
        }
    }

    private void goToShowPlace() {
        Intent intent = new Intent(mActivity, PlaceDetailActivity.class);
        intent.putExtra("place_id", this.place.getId());
        startActivity(intent);
    }

    private void uploadPlaceImage() {
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
                    }
                });
                return;
            } else {
                place.setPhoto(uploadResponseJsonBody.get("file").toString());
                requestJsonBody.put("photo", place.getPhoto());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            return addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void onLocationChanged(Location location) {
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            et_place_location.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
