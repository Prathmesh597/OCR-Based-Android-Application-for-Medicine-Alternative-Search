package com.example.medicineapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class search extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private EditText searchInput;
    private List<String> alternativesList;
    private AlternativesAdapter alternativesAdapter;
    private TextView alternativesListHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        searchInput = findViewById(R.id.searchInput);
        ImageView searchIcon = findViewById(R.id.searchIcon);
        RecyclerView alternativesRecyclerView = findViewById(R.id.resultsRecyclerView);
        alternativesListHeader = findViewById(R.id.alternativesList);

        // Initialize RecyclerView and Adapter
        alternativesList = new ArrayList<>();
        alternativesAdapter = new AlternativesAdapter(alternativesList);
        alternativesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        alternativesRecyclerView.setAdapter(alternativesAdapter);

        // Set listeners for search
        searchIcon.setOnClickListener(v -> initiateSearch());
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                initiateSearch();
                return true;
            }
            return false;
        });
    }

    private void initiateSearch() {
        String searchQuery = searchInput.getText().toString().trim();
        if (searchQuery.isEmpty()) {
            Toast.makeText(this, "Please enter a medicine name", Toast.LENGTH_SHORT).show();
            return;
        }
        searchMedicine(searchQuery);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void searchMedicine(String query) {
        try {
            String fileName = "medicine_data.xlsx";
            InputStream inputStream = getApplicationContext().getAssets().open(fileName);
            Log.d(TAG, "Attempting to open file: " + fileName);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            alternativesList.clear();  // Clear previous results
            boolean found = false;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                try {
                    String name = row.getCell(1).getStringCellValue().trim();
                    Log.d(TAG, "Comparing: " + name + " with query: " + query);

                    // If match found, collect alternatives from columns 4 to 6
                    if (name.equalsIgnoreCase(query)) {
                        found = true;
                        for (int i = 2; i <= 5; i++) {
                            if (row.getCell(i) != null) {
                                String alternative = row.getCell(i).getStringCellValue().trim();
                                if (!alternative.isEmpty()) {
                                    alternativesList.add(alternative);
                                    Log.d(TAG, "Added alternative: " + alternative);
                                }
                            }
                        }
                        break;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error reading row: " + row.getRowNum(), e);
                }
            }

            workbook.close();
            inputStream.close();

            // Run on the UI thread
            boolean finalFound = found;
            runOnUiThread(() -> {
                if (finalFound) {
                    // Make sure the header is visible
                    alternativesListHeader.setVisibility(View.VISIBLE);
                    alternativesAdapter.notifyDataSetChanged(); // Update the RecyclerView
                    Log.d(TAG, "Found alternatives: " + alternativesList.size());
                } else {
                    // Hide the alternatives list header if no alternatives are found
                    alternativesListHeader.setVisibility(View.GONE);
                    Toast.makeText(this, "No medicine found", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error reading Excel file", e);
            e.printStackTrace();
            // Show error message
            runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }
}
