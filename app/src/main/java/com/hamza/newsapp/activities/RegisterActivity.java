package com.hamza.newsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.hamza.newsapp.R;
import com.hamza.newsapp.RequestManager;
import com.hamza.newsapp.model.Source;

import java.util.List;

import okhttp3.OkHttpClient;

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
        buttonSubmit.setOnClickListener(v -> performRegistration());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void performRegistration() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();

        if (fieldsAreValid(firstName, lastName, email, password)) {
            RequestManager requestManager = new RequestManager();
            requestManager.register(firstName, lastName, email, password, new RequestManager.RequestCallback() {
                @Override
                public void onSuccess(List<Source> sources) {
                    showToastOnMainThread("Registration successful!");
                    launchLoginActivity();
                }

                @Override
                public void onError(Exception e) {
                    showToastOnMainThread("Registration failed: " + e.getMessage());
                }
            });
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

    private void showToastOnMainThread(String message) {
        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
