package com.example.healthyguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class TodaysPlanActivity extends AppCompatActivity {

    private TextView mealPlanTextView;
    private TextView exercisePlanTextView;
    private EditText todaysWeightEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todays_plan);

        mealPlanTextView = findViewById(R.id.mealPlanTextView);
        exercisePlanTextView = findViewById(R.id.exercisePlanTextView);
        todaysWeightEditText = findViewById(R.id.todaysWeightEditText);
        Button completeExerciseButton = findViewById(R.id.completeExerciseButton);
        Button backToHomeButton = findViewById(R.id.backToHomeButton);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");

        fetchTodaysPlan(userId);

        backToHomeButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(TodaysPlanActivity.this, HomePageActivity.class);
            homeIntent.putExtra("userId", userId);
            startActivity(homeIntent);
        });

        completeExerciseButton.setOnClickListener(v -> {
            String weight = todaysWeightEditText.getText().toString();
            if (weight.isEmpty()) {
                Toast.makeText(TodaysPlanActivity.this, "Please enter today's weight", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(TodaysPlanActivity.this, "Exercise completed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTodaysPlan(String userId) {
        String url = "http://10.0.2.2:3000/today_plan";
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject requestObj = new JSONObject();
        try {
            requestObj.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestObj,
                response -> {
                    try {
                        String todayPlan = response.getString("todayPlan");
                        displayTodaysPlan(todayPlan);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(TodaysPlanActivity.this, "Error fetching today's plan: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(jsonObjectRequest);
    }

    private void displayTodaysPlan(String todayPlan) {
        String[] sections = todayPlan.split("\n\n");

        for (String section : sections) {
            if (section.startsWith("Meal Plan:")) {
                mealPlanTextView.setText(section.trim());
            } else if (section.startsWith("Exercise Plan:")) {
                exercisePlanTextView.setText(section.trim());
            }
        }
    }
}
