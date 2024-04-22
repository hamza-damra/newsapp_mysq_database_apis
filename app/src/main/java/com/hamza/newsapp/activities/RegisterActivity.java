package com.hamza.newsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.hamza.newsapp.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword;
    private Button buttonSubmit, buttonCancel;

    private static final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        initViews();
        setupListeners();
    }

    private void initViews() {
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonCancel = findViewById(R.id.buttonCancel);
    }

    private void setupListeners() {
        buttonSubmit.setOnClickListener(v -> tryRegister());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void tryRegister() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();

        if (fieldsAreValid(firstName, lastName, email, password)) {
            postRegistrationData(firstName, lastName, email, password);
        } else {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean fieldsAreValid(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void postRegistrationData(String firstName, String lastName, String email, String password) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("firstName", firstName);
            json.put("lastName", lastName);
            json.put("email", email);
            json.put("password", password);
        } catch (JSONException e) {
            Log.e("RegisterActivity", "Error creating JSON object", e);
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
                Log.e("RegisterActivity", "Registration failed", e);
                showToastOnMainThread("Registration failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    showToastOnMainThread("Registration successful!");
                    launchLoginActivity();
                } else {
                    String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                    showToastOnMainThread("Registration failed: " + errorMessage);
                }
            }
        });
    }




    private void showToastOnMainThread(String message) {
        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
