package com.example.medicineapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class image_search extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private EditText searchBox;
    private List<String> alternativesList;
    private AlternativesAdapter alternativesAdapter;
    private static final String TAG = "ImageSearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);

        // Initialize views
        searchBox = findViewById(R.id.search_box);
        ImageView searchIcon = findViewById(R.id.search_icon);
        RecyclerView recyclerView = findViewById(R.id.alternatives_recycler_view);

        // Initialize RecyclerView and Adapter
        alternativesList = new ArrayList<>();
        alternativesAdapter = new AlternativesAdapter(alternativesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(alternativesAdapter);

        // Set click listener for the search icon
        searchIcon.setOnClickListener(v -> {
            // Get the search query from the EditText
            String searchQuery = searchBox.getText().toString().trim();
            if (searchQuery.isEmpty()) {
                Toast.makeText(image_search.this, "Please enter a medicine name", Toast.LENGTH_SHORT).show();
            } else {
                searchMedicine(searchQuery);  // Call the method to search for alternatives
            }
        });

        // Set click listener for the upload button (camera or gallery)
        findViewById(R.id.btn_upload).setOnClickListener(v -> {
            showUploadOptions();  // Show upload options (Capture Image / Upload from Gallery)
        });
    }

    // Method to show upload options (camera or gallery)
    private void showUploadOptions() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Choose an option");
        builder.setItems(new CharSequence[]{"Capture Image", "Upload from Gallery"},
                (dialog, which) -> {
                    if (which == 0) {
                        // Check camera permission and open camera
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                        } else {
                            openCamera();
                        }
                    } else if (which == 1) {
                        // Open gallery to select image
                        openGallery();
                    }
                });
        builder.show();
    }

    // Method to open the camera
    private void openCamera() {
        // Start camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    // Method to open the gallery
    private void openGallery() {
        // Start gallery intent to pick an image
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to capture images.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;

            if (requestCode == REQUEST_CAMERA) {
                // Get the captured image from the camera
                bitmap = (Bitmap) data.getExtras().get("data");
            } else if (requestCode == REQUEST_GALLERY) {
                try {
                    // Get the image from the gallery
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (bitmap != null) {
                // Perform OCR on the image
                performOCR(bitmap);
            }
        }
    }

    // Method to perform OCR on the image and extract text
    private void performOCR(Bitmap bitmap) {
        // Convert the Bitmap to InputImage for ML Kit
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        // Perform text recognition
        recognizer.process(image)
                .addOnSuccessListener(this::onTextRecognized)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "OCR failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("OCR", "OCR failed", e);
                });
    }

    // Method to handle the OCR result and populate the search box
    private void onTextRecognized(Text text) {
        String extractedText = text.getText();
        searchBox.setText(extractedText);  // Set the recognized text in the search box
    }

    // Method to perform search based on the extracted text or user input
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
                    alternativesAdapter.notifyDataSetChanged(); // Update the RecyclerView
                    Log.d(TAG, "Found alternatives: " + alternativesList.size());
                } else {
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
