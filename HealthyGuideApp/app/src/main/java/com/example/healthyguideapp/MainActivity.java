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

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);
        Button signupButton = findViewById(R.id.signupButton);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            Log.d(TAG, "Attempting to log in with username: " + username);
            loginUser(username, password);
        });

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String username, String password) {
        String url = "http://10.0.2.2:3000/login";
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject loginData = new JSONObject();
        try {
            loginData.put("username", username);
            loginData.put("password", password);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException: " + e.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, loginData,
                response -> {
                    try {
                        if (response.getString("message").equals("Login successful")) {
                            String userId = response.getJSONObject("user").getString("_id");
                            Log.d(TAG, "Login successful. User ID: " + userId);
                            fetchHealthPlan(userId);
                        } else {
                            Log.d(TAG, "Login failed: " + response.getString("message"));
                            Toast.makeText(MainActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSONException: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e(TAG, "Error logging in: " + error.getMessage());
                    Toast.makeText(MainActivity.this, "Error logging in: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(jsonObjectRequest);
    }

    private void fetchHealthPlan(String userId) {
        String url = "http://10.0.2.2:3000/health_plan";
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject userIdJson = new JSONObject();
        try {
            userIdJson.put("userId", userId);
            Log.d(TAG, "Fetching health plan for User ID: " + userId);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException: " + e.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, userIdJson,
                response -> {
                    try {
                        Log.d(TAG, "Health plan response: " + response.toString());
                        String healthPlan = response.getString("healthPlan");
                        Log.d(TAG, "Health plan fetched successfully.");
                        Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("healthPlan", healthPlan);
                        startActivity(intent);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSONException: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching health plan: " + error.toString());
                    if (error.networkResponse != null) {
                        String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Log.e(TAG, "Error response body: " + body);
                        Toast.makeText(MainActivity.this, "Error fetching health plan: " + body, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Error fetching health plan: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(jsonObjectRequest);
    }

}
