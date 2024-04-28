package com.hamza.newsapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hamza.newsapp.R;
import com.hamza.newsapp.RequestManager;
import com.hamza.newsapp.model.Source;

import java.util.List;

public class AddNewsActivity extends AppCompatActivity {

    EditText etTitle, etDescription, etUrlToImage, etAuthor;
    Spinner spSource;
    Button btnAddNews, btnCancel;

    RequestManager requestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_news);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        requestManager = new RequestManager();
        initViews();
        initCategoriesSpinner();
        btnAddNews.setOnClickListener(v -> performAddNews());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void initCategoriesSpinner() {
        String[] categories = {"Business", "Entertainment", "General", "Health", "Science", "Sports", "Technology"};
        SpinnerAdapter adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spSource.setAdapter(adapter);
    }


    private void performAddNews() {
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String urlToImage = etUrlToImage.getText().toString();
        String author = etAuthor.getText().toString();
        String sourceCategories = spSource.getSelectedItem().toString();
        if (title.isEmpty() || description.isEmpty() || urlToImage.isEmpty() || author.isEmpty() || sourceCategories.isEmpty()) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
        }
        else {
            Source source = new Source(title, description, urlToImage, sourceCategories, author);
            requestManager.addNews(source, new RequestManager.RequestCallback() {

                @Override
                public void onSuccess(List<Source> sources) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddNewsActivity.this, "News added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddNewsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });

        }

    }

    private void initViews() {
        etTitle = findViewById(R.id.editTextTitle);
        etDescription = findViewById(R.id.editTextDescription);
        etUrlToImage = findViewById(R.id.editTextUrlToImage);
        etAuthor = findViewById(R.id.editTextAuthorName);
        spSource = findViewById(R.id.spinnerSource);
        btnAddNews = findViewById(R.id.buttonAddNews);
        btnCancel = findViewById(R.id.buttonCancel);
    }




}