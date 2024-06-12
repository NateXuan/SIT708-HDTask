package com.example.healthyguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SelfInformation2Activity extends AppCompatActivity {

    private EditText healthConditionEditText;
    private EditText goalEditText;
    private EditText dietaryPreferencesEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_information2);

        healthConditionEditText = findViewById(R.id.healthCondition);
        goalEditText = findViewById(R.id.goal);
        dietaryPreferencesEditText = findViewById(R.id.dietaryPreferences);
        Button submitButton = findViewById(R.id.submitButton);

        Intent intent = getIntent();
        String age = intent.getStringExtra("age");
        String gender = intent.getStringExtra("gender");
        String height = intent.getStringExtra("height");
        String weight = intent.getStringExtra("weight");

        submitButton.setOnClickListener(v -> {
            String healthCondition = healthConditionEditText.getText().toString();
            String goal = goalEditText.getText().toString();
            String dietaryPreferences = dietaryPreferencesEditText.getText().toString();

            if (healthCondition.isEmpty() || goal.isEmpty() || dietaryPreferences.isEmpty()) {
                Toast.makeText(SelfInformation2Activity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                saveUserInfo(age, gender, height, weight, healthCondition, goal, dietaryPreferences);
            }
        });
    }

    private void saveUserInfo(String age, String gender, String height, String weight, String healthCondition, String goal, String dietaryPreferences) {
        JSONObject userInfo = new JSONObject();
        try {
            userInfo.put("age", age);
            userInfo.put("gender", gender);
            userInfo.put("height", height);
            userInfo.put("weight", weight);
            userInfo.put("health_condition", healthCondition);
            userInfo.put("goal", goal);
            userInfo.put("dietary_preferences", dietaryPreferences);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fetchHealthPlan(userInfo);
    }

    private void fetchHealthPlan(JSONObject userInfo) {
        String url = "http://10.0.2.2:5001/health_plan";
        RequestQueue queue = Volley.newRequestQueue(this);
        Log.d("HealthPlanRequest", "Sending data: " + userInfo.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, userInfo,
                response -> {
                    try {
                        String healthPlan = response.getString("healthPlan");
                        Intent intent = new Intent(SelfInformation2Activity.this, HomePageActivity.class);
                        intent.putExtra("healthPlan", healthPlan);
                        startActivity(intent);
                    } catch (JSONException e) {
                        Log.e("HealthPlanResponse", "Error parsing health plan", e);
                        Toast.makeText(SelfInformation2Activity.this, "Error parsing health plan", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("HealthPlanError", "Error fetching health plan: " + error.toString());
                    Toast.makeText(SelfInformation2Activity.this, "Error fetching health plan: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                180000, // 180 seconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest);
    }


}
