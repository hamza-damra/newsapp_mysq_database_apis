package com.hamza.newsapp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hamza.newsapp.model.Source;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RequestManager {
    private final OkHttpClient client;
    private final Gson gson;

    public RequestManager() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public void fetchSources(final RequestCallback callback) {
        String url = "http://192.168.0.58:8080/api/sources/getAll"; // Replace with your API URL

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                postToMainThread(() -> callback.onError(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Type listType = new TypeToken<ArrayList<Source>>(){}.getType();
                    List<Source> sources = gson.fromJson(responseBody, listType);

                    postToMainThread(() -> callback.onSuccess(sources));
                } else {
                    postToMainThread(() -> callback.onError(new IOException("Unexpected code " + response)));
                }
            }
        });
    }

    public void searchByQuery(String query, final RequestCallback callback) {
        String url = "http://192.168.0.58:8080/api/sources/search?query=" + query; // Replace with your API URL

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                postToMainThread(() -> callback.onError(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Type listType = new TypeToken<ArrayList<Source>>(){}.getType();
                    List<Source> sources = gson.fromJson(responseBody, listType);

                    postToMainThread(() -> callback.onSuccess(sources));
                } else {
                    postToMainThread(() -> callback.onError(new IOException("Unexpected code " + response)));
                }
            }
        });
    }

    public void fetchSourcesByCategory(String category, final RequestCallback callback) {
        String url = "http://192.168.0.58:8080/api/sources/category/" + category; // Replace with your API URL

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                postToMainThread(() -> callback.onError(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Type listType = new TypeToken<ArrayList<Source>>(){}.getType();
                    List<Source> sources = gson.fromJson(responseBody, listType);

                    postToMainThread(() -> callback.onSuccess(sources));
                } else {
                    postToMainThread(() -> callback.onError(new IOException("Unexpected code " + response)));
                }
            }
        });
    }

    private void postToMainThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public interface RequestCallback {
        void onSuccess(List<Source> sources);
        void onError(Exception e);
    }
}
