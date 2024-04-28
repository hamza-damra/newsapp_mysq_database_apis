package com.hamza.newsapp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hamza.newsapp.model.Source;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
        String url = "http://192.168.0.58:8080/api/sources/getAll";

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
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    Type listType = new SourceListTypeToken().getType();
                    List<Source> sources = gson.fromJson(responseBody, listType);

                    postToMainThread(() -> callback.onSuccess(sources));
                } else {
                    postToMainThread(() -> callback.onError(new IOException("Unexpected code " + response)));
                }
            }
        });
    }

    public void searchByQuery(String query, final RequestCallback callback) {
        String url = "http://192.168.0.58:8080/api/sources/search?query=" + query;

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
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    Type listType = new SourceListTypeToken().getType();
                    List<Source> sources = gson.fromJson(responseBody, listType);

                    postToMainThread(() -> callback.onSuccess(sources));
                } else {
                    postToMainThread(() -> callback.onError(new IOException("Unexpected code " + response)));
                }
            }
        });
    }

    public void fetchSourcesByCategory(String category, final RequestCallback callback) {
        String url = "http://192.168.0.58:8080/api/sources/category/" + category;

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
                    assert response.body() != null;
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

    public void addNews(Source source, final RequestCallback callback) {
        String url = "http://192.168.0.58:8080/api/sources/addSource";
        String sourceJson = gson.toJson(source);

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), sourceJson))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                postToMainThread(() -> callback.onError(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    postToMainThread(() -> callback.onSuccess(null));
                } else {
                    postToMainThread(() -> callback.onError(new IOException("Unexpected code " + response)));
                }
            }
        });
    }

    public void login(String email, String password, final RequestCallback callback) {
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse("http://192.168.0.58:8080/api/users/login")).newBuilder()
                .addQueryParameter("email", email)
                .addQueryParameter("password", password)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(new byte[0]))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                postToMainThread(() -> callback.onError(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    postToMainThread(() -> callback.onSuccess(null)); // Pass null or actual response based on your implementation
                } else {
                    postToMainThread(() -> {
                        try {
                            assert response.body() != null;
                            callback.onError(new IOException("Login failed: " + response.body().string()));
                        } catch (IOException e) {
                            postToMainThread(() -> callback.onError(e));
                        }
                    });
                }
            }
        });
    }

    public void register(String firstName, String lastName, String email, String password, final RequestCallback callback) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("firstName", firstName);
            json.put("lastName", lastName);
            json.put("email", email);
            json.put("password", password);
        } catch (JSONException e) {
            Log.e("RequestManager", "Error creating JSON for registration", e);
            postToMainThread(() -> callback.onError(e));
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url("http://192.168.0.58:8080/api/users/register")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("RequestManager", "Registration failed", e);
                postToMainThread(() -> callback.onError(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    postToMainThread(() -> callback.onSuccess(null)); // null or actual response
                } else {
                    String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                    postToMainThread(() -> callback.onError(new IOException("Registration failed: " + errorMessage)));
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
