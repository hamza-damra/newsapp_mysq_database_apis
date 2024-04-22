package com.hamza.newsapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.hamza.newsapp.MainActivity;
import com.hamza.newsapp.R;
import java.io.IOException;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        if (isLoggedIn()) {
            startMainActivity();
        }

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonCreateAccount = findViewById(R.id.buttonCreateNewAccount);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonLogin.setOnClickListener(v -> attemptLogin());
        buttonCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

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
                Log.e("LoginActivity", "Login request failed: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(LoginActivity.this,
                        "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    saveLoginStatus();
                    runOnUiThread(LoginActivity.this::startMainActivity);
                } else {
                    runOnUiThread(() -> {
                        try {
                            assert response.body() != null;
                            Toast.makeText(LoginActivity.this,
                                    "Login failed: " + response.body().string(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });
    }

    private void saveLoginStatus() {
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IsLoggedIn", true);
        editor.apply();
    }

    private boolean isLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return preferences.getBoolean("IsLoggedIn", false);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
