package com.example.myapplication.utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiCall {

    public static Response getHttp(String url, String token) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response postHttp(String url, String jsoinBody, String token) throws IOException {
        OkHttpClient client = new OkHttpClient();
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsoinBody);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response putHttp(String url, String jsonBody, String token) throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonBody);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .put(body)
                .build();

        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response deleteHttp(String url, String token) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .delete()
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    private static Response postImage(MediaType mediaType, String uploadUrl, File file, String token) throws IOException {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM).addFormDataPart("file", file.getName(), RequestBody.create(mediaType, file));
        RequestBody req = builder.build();
        Request request = new Request.Builder()
                .url(uploadUrl)
                .header("Authorization", "Bearer " + token)
                .post(req)
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();
        Response response = client
                .newCall(request)
                .execute();
        return response;
    }


    public static Response postImg(String uploadUrl, File file, String token) throws IOException {
        MediaType imageType = MediaType.parse("image/jpeg; charset=utf-8");
        return postImage(imageType, uploadUrl, file, token);
    }

}
