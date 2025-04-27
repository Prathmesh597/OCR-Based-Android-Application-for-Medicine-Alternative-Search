package com.example.medicineapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Show the disclaimer dialog when the app starts
        showDisclaimerDialog();

        // Initialize CardViews
        CardView uploadCard = findViewById(R.id.uploadCard);
        CardView searchCard = findViewById(R.id.searchCard);

        // Set Click Listener for Upload Card
        uploadCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Upload Page
                Intent intent = new Intent(MainActivity.this, image_search.class);
                startActivity(intent);
            }
        });

        // Set Click Listener for Search Card
        searchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Search Page
                Intent intent = new Intent(MainActivity.this, search.class);
                startActivity(intent);
            }
        });
    }

    private void showDisclaimerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Disclaimer");
        builder.setMessage("We are just providing information about medicines and their alternatives available in the market. Before using any medicine, please consult with a doctor. If you choose to use a medicine without consulting a doctor and something happens, we are not responsible.");

        // Agree button
        builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Proceed to the main application
                dialog.dismiss();
            }
        });

        // Not Agree button
        builder.setNegativeButton("Not Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Exit the application
                finish();
            }
        });

        // Disable cancellation by tapping outside the dialog
        builder.setCancelable(false);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
