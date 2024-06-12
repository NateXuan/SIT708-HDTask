package com.example.healthyguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SelfInformation1Activity extends AppCompatActivity {

    private EditText ageEditText;
    private EditText genderEditText;
    private EditText heightEditText;
    private EditText weightEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_information1);

        ageEditText = findViewById(R.id.age);
        genderEditText = findViewById(R.id.gender);
        heightEditText = findViewById(R.id.height);
        weightEditText = findViewById(R.id.weight);
        Button nextPageButton = findViewById(R.id.nextPageButton);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        boolean isUpdate = intent.getBooleanExtra("isUpdate", false);
        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        if (isUpdate) {
            welcomeTextView.setText("Update your information below.");
        } else {
            welcomeTextView.setText("Welcome " + username + ", please enter your information below.");
        }

        nextPageButton.setOnClickListener(v -> {
            String age = ageEditText.getText().toString();
            String gender = genderEditText.getText().toString();
            String height = heightEditText.getText().toString();
            String weight = weightEditText.getText().toString();

            if (age.isEmpty() || gender.isEmpty() || height.isEmpty() || weight.isEmpty()) {
                Toast.makeText(SelfInformation1Activity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                Intent nextIntent = new Intent(SelfInformation1Activity.this, SelfInformation2Activity.class);
                nextIntent.putExtra("age", age);
                nextIntent.putExtra("gender", gender);
                nextIntent.putExtra("height", height);
                nextIntent.putExtra("weight", weight);
                nextIntent.putExtra("isUpdate", isUpdate);
                startActivity(nextIntent);
            }
        });
    }
}
