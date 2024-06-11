package com.example.healthyguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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
        String userId = intent.getStringExtra("userId");
        String age = intent.getStringExtra("age");
        String gender = intent.getStringExtra("gender");
        String height = intent.getStringExtra("height");
        String weight = intent.getStringExtra("weight");
        boolean isUpdate = intent.getBooleanExtra("isUpdate", false);

        submitButton.setOnClickListener(v -> {
            String healthCondition = healthConditionEditText.getText().toString();
            String goal = goalEditText.getText().toString();
            String dietaryPreferences = dietaryPreferencesEditText.getText().toString();

            if (healthCondition.isEmpty() || goal.isEmpty() || dietaryPreferences.isEmpty()) {
                Toast.makeText(SelfInformation2Activity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                if (isUpdate) {
                    updateUserInfo(userId, age, gender, height, weight, healthCondition, goal, dietaryPreferences);
                } else {
                    saveUserInfo(userId, age, gender, height, weight, healthCondition, goal, dietaryPreferences);
                }
            }
        });
    }

    private void saveUserInfo(String userId, String age, String gender, String height, String weight, String healthCondition, String goal, String dietaryPreferences) {
        String url = "http://10.0.2.2:3000/user_information";
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject userInfo = new JSONObject();
        try {
            userInfo.put("userId", userId);
            userInfo.put("age", age);
            userInfo.put("gender", gender);
            userInfo.put("height", height);
            userInfo.put("weight", weight);
            userInfo.put("healthCondition", healthCondition);
            userInfo.put("goal", goal);
            userInfo.put("dietaryPreferences", dietaryPreferences);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, userInfo,
                response -> {
                    Toast.makeText(SelfInformation2Activity.this, "Information saved", Toast.LENGTH_SHORT).show();
                    fetchHealthPlan(userId);
                },
                error -> Toast.makeText(SelfInformation2Activity.this, "Error saving information: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(jsonObjectRequest);
    }

    private void updateUserInfo(String userId, String age, String gender, String height, String weight, String healthCondition, String goal, String dietaryPreferences) {
        String url = "http://10.0.2.2:3000/user_information";
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject userInfo = new JSONObject();
        try {
            userInfo.put("userId", userId);
            userInfo.put("age", age);
            userInfo.put("gender", gender);
            userInfo.put("height", height);
            userInfo.put("weight", weight);
            userInfo.put("healthCondition", healthCondition);
            userInfo.put("goal", goal);
            userInfo.put("dietaryPreferences", dietaryPreferences);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, userInfo,
                response -> {
                    Toast.makeText(SelfInformation2Activity.this, "Information updated", Toast.LENGTH_SHORT).show();
                    fetchHealthPlan(userId);
                },
                error -> Toast.makeText(SelfInformation2Activity.this, "Error updating information: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(jsonObjectRequest);
    }

    private void fetchHealthPlan(String userId) {
        String url = "http://10.0.2.2:3000/health_plan";
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject userIdJson = new JSONObject();
        try {
            userIdJson.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, userIdJson,
                response -> {
                    try {
                        String healthPlan = response.getString("healthPlan");
                        Intent intent = new Intent(SelfInformation2Activity.this, HomePageActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("healthPlan", healthPlan);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(SelfInformation2Activity.this, "Error fetching health plan: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(jsonObjectRequest);
    }
}
