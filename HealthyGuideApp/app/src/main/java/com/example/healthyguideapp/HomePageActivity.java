package com.example.healthyguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {

    private LinearLayout summaryLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        summaryLayout = findViewById(R.id.summaryLayout);
        Button todaysPlanButton = findViewById(R.id.todaysPlanButton);
        Button settingsButton = findViewById(R.id.settingsButton);

        Intent intent = getIntent();
        String healthPlan = intent.getStringExtra("healthPlan");
        String age = intent.getStringExtra("age");
        String gender = intent.getStringExtra("gender");
        String height = intent.getStringExtra("height");
        String weight = intent.getStringExtra("weight");
        String healthCondition = intent.getStringExtra("healthCondition");
        String goal = intent.getStringExtra("goal");
        String dietaryPreferences = intent.getStringExtra("dietaryPreferences");

        assert healthPlan != null;
        displayHealthPlan(healthPlan);

        todaysPlanButton.setOnClickListener(v -> {
            Intent planIntent = new Intent(HomePageActivity.this, TodaysPlanActivity.class);
            planIntent.putExtra("age", age);
            planIntent.putExtra("gender", gender);
            planIntent.putExtra("height", height);
            planIntent.putExtra("weight", weight);
            planIntent.putExtra("healthCondition", healthCondition);
            planIntent.putExtra("goal", goal);
            planIntent.putExtra("dietaryPreferences", dietaryPreferences);
            startActivity(planIntent);
        });

        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(HomePageActivity.this, SelfInformation1Activity.class);
            settingsIntent.putExtra("age", age);
            settingsIntent.putExtra("gender", gender);
            settingsIntent.putExtra("height", height);
            settingsIntent.putExtra("weight", weight);
            settingsIntent.putExtra("healthCondition", healthCondition);
            settingsIntent.putExtra("goal", goal);
            settingsIntent.putExtra("dietaryPreferences", dietaryPreferences);
            settingsIntent.putExtra("isUpdate", true);
            startActivity(settingsIntent);
        });
    }

    private void displayHealthPlan(String healthPlan) {
        String[] sections = healthPlan.split("\n\n");

        for (String section : sections) {
            TextView textView = new TextView(this);
            textView.setText(section.trim());
            textView.setTextSize(16);
            textView.setPadding(0, 10, 0, 10);
            summaryLayout.addView(textView);
        }
    }
}
