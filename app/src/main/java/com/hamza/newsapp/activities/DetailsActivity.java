package com.hamza.newsapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.hamza.newsapp.R;
import com.hamza.newsapp.model.Source;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    private TextView titleTextView;
    private ImageView imageView;
    private TextView descriptionTextView;
    private TextView authorTextView;
    private TextView publishDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        titleTextView = findViewById(R.id.title);
        imageView = findViewById(R.id.image);
        descriptionTextView = findViewById(R.id.description);
        authorTextView = findViewById(R.id.tv_author);
        publishDateTextView = findViewById(R.id.tv_date);
        Button backButton = findViewById(R.id.back);

        String sourceJson = getIntent().getStringExtra("source");
        if (sourceJson != null) {
            Source source = new Gson().fromJson(sourceJson, Source.class);
            populateDetails(source);
        } else {
            Toast.makeText(this, "Source data not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        backButton.setOnClickListener(v -> finish());
    }

    private void populateDetails(Source source) {
        titleTextView.setText(source.getName());
        descriptionTextView.setText(source.getDescription());
        authorTextView.setText(source.getAuthor());
        publishDateTextView.setText(source.getPublishDate());

        if (source.getUrl() != null && !source.getUrl().isEmpty()) {
            Picasso.get().load(source.getUrl()).
                    error(R.drawable.not_available).
                    into(imageView);
        }
    }
}
