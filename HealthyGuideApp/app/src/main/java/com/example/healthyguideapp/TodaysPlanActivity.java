package com.example.healthyguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
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

    private static final String TAG = "TodaysPlanActivity";
    private LinearLayout mealPlanLayout;
    private LinearLayout exercisePlanLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todays_plan);

        mealPlanLayout = findViewById(R.id.mealPlanLayout);
        exercisePlanLayout = findViewById(R.id.exercisePlanLayout);
        Button backToHomeButton = findViewById(R.id.backToHomeButton);

        Intent intent = getIntent();
        String age = intent.getStringExtra("age");
        String gender = intent.getStringExtra("gender");
        String height = intent.getStringExtra("height");
        String weight = intent.getStringExtra("weight");
        String healthCondition = intent.getStringExtra("healthCondition");
        String goal = intent.getStringExtra("goal");
        String dietaryPreferences = intent.getStringExtra("dietaryPreferences");

        Log.d(TAG, "Received user info: " +
                "age=" + age + ", gender=" + gender + ", height=" + height +
                ", weight=" + weight + ", healthCondition=" + healthCondition +
                ", goal=" + goal + ", dietaryPreferences=" + dietaryPreferences);

        fetchTodaysPlan(age, gender, height, weight, healthCondition, goal, dietaryPreferences);

        backToHomeButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(TodaysPlanActivity.this, HomePageActivity.class);
            startActivity(homeIntent);
        });
    }

    private void fetchTodaysPlan(String age, String gender, String height, String weight,
                                 String healthCondition, String goal, String dietaryPreferences) {
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

        Log.d(TAG, "Sending data: " + requestObj);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestObj,
                response -> {
                    try {
                        Log.d(TAG, "Received response: " + response.toString());
                        if (response.has("todayPlan")) {
                            String todayPlan = response.getString("todayPlan");
                            displayTodaysPlan(todayPlan);
                        } else {
                            Log.e(TAG, "No 'todayPlan' field in response");
                            Toast.makeText(TodaysPlanActivity.this, "Error: No 'todayPlan' field in response", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing today's plan", e);
                        Toast.makeText(TodaysPlanActivity.this, "Error parsing today's plan", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching today's plan: " + error.toString());
                    Toast.makeText(TodaysPlanActivity.this, "Error fetching today's plan: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                180000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(jsonObjectRequest);
    }

    private void displayTodaysPlan(String todayPlan) {
        Log.d(TAG, "Displaying plan: " + todayPlan);

        String[] sections = todayPlan.split("\n\n");

        boolean isMealPlan = true;

        for (String section : sections) {
            TextView textView = new TextView(this);
            textView.setText(section.trim());
            textView.setTextSize(16);
            textView.setPadding(0, 10, 0, 10);

            if (section.contains("**Meal Plan:**")) {
                isMealPlan = true;
            } else if (section.contains("**Exercise Plan:**")) {
                isMealPlan = false;
            }

            if (isMealPlan) {
                mealPlanLayout.addView(textView);
            } else {
                exercisePlanLayout.addView(textView);
            }
        }
    }

}
