package com.hamza.newsapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.hamza.newsapp.activities.LoginActivity;
import com.hamza.newsapp.model.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private SourceAdapter adapter;
    private RequestManager requestManager;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupSwipeRefreshLayout();
        setupCategoryButtons();
        setupSearchView();
        setSupportActionBar(toolbar);
        requestManager = new RequestManager();
        loadSources();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_main);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        searchView = findViewById(R.id.search_view);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SourceAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadSources();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void setupCategoryButtons() {
        int[] buttonIds = {
                R.id.btn_general, R.id.btn_business, R.id.btn_entertainment,
                R.id.btn_health, R.id.btn_science, R.id.btn_sports, R.id.btn_technology
        };
        String[] categories = {
                "General", "Business", "Entertainment", "Health",
                "Science", "Sports", "Technology"
        };

        for (int i = 0; i < buttonIds.length; i++) {
            MaterialButton button = findViewById(buttonIds[i]);
            int finalI = i;
            button.setOnClickListener(v -> filterSources(categories[finalI]));
        }
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
              performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void performSearch(String query) {
        showLoadingDialog();
        requestManager.searchByQuery(query, new RequestManager.RequestCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<Source> sources) {
                updateAdapter(sources);
            }

            @Override
            public void onError(Exception e) {
                hideLoadingDialog();
                showError(e.getMessage());
            }
        });
    }

    private void filterSources(String category) {
        showLoadingDialog();
        requestManager.fetchSourcesByCategory(category, new RequestManager.RequestCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<Source> sources) {
                updateAdapter(sources);
            }

            @Override
            public void onError(Exception e) {
                hideLoadingDialog();
                showError(e.getMessage());
            }
        });
    }

    private void loadSources() {
        showLoadingDialog();
        requestManager.fetchSources(new RequestManager.RequestCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<Source> sources) {
                updateAdapter(sources);
            }

            @Override
            public void onError(Exception e) {
                Log.e("MainActivity", "Error loading sources: " + Objects.requireNonNull(e.getMessage()));
                hideLoadingDialog();
                showError(e.getMessage());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateAdapter(List<Source> sources) {
        runOnUiThread(() -> {
            if (sources != null) {
                adapter.setSources(sources);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MainActivity.this, "No sources available", Toast.LENGTH_SHORT).show();
            }
            hideLoadingDialog();
        });
    }

    private void showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            showLogoutConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        logoutUser();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }



    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showError(String message) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_LONG).show());
    }

    private void logoutUser() {
        // Clear the login status
        saveLoginStatus();

        // Redirect to LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void saveLoginStatus() {
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IsLoggedIn", false);
        editor.apply();
    }
}
