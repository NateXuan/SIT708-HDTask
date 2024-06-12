package com.example.healthyguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
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
        String age = intent.getStringExtra("age");
        String gender = intent.getStringExtra("gender");
        String height = intent.getStringExtra("height");
        String weight = intent.getStringExtra("weight");
        String healthCondition = intent.getStringExtra("healthCondition");
        String goal = intent.getStringExtra("goal");
        String dietaryPreferences = intent.getStringExtra("dietaryPreferences");

        fetchTodaysPlan(age, gender, height, weight, healthCondition, goal, dietaryPreferences);

        backToHomeButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(TodaysPlanActivity.this, HomePageActivity.class);
            startActivity(homeIntent);
        });

        completeExerciseButton.setOnClickListener(v -> {
            String weightInput = todaysWeightEditText.getText().toString();
            if (weightInput.isEmpty()) {
                Toast.makeText(TodaysPlanActivity.this, "Please enter today's weight", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TodaysPlanActivity.this, "Exercise completed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTodaysPlan(String age, String gender, String height, String weight, String healthCondition, String goal, String dietaryPreferences) {
        String url = "http://10.0.2.2:5001/today_plan";
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject requestObj = new JSONObject();
        try {
            requestObj.put("age", age);
            requestObj.put("gender", gender);
            requestObj.put("height", height);
            requestObj.put("weight", weight);
            requestObj.put("health_condition", healthCondition);
            requestObj.put("goal", goal);
            requestObj.put("dietary_preferences", dietaryPreferences);
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
                        Toast.makeText(TodaysPlanActivity.this, "Error parsing today's plan", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(TodaysPlanActivity.this, "Error fetching today's plan: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                180000, // 180 seconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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
