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

public class SignUpActivity extends AppCompatActivity {

    private EditText fullNameEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText phoneEditText;
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fullNameEditText = findViewById(R.id.fullName);
        usernameEditText = findViewById(R.id.signupUsername);
        passwordEditText = findViewById(R.id.signupPassword);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        phoneEditText = findViewById(R.id.phone);
        emailEditText = findViewById(R.id.email);
        Button createAccountButton = findViewById(R.id.createAccountButton);

        createAccountButton.setOnClickListener(v -> {
            String fullName = fullNameEditText.getText().toString();
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String email = emailEditText.getText().toString();

            if (password.equals(confirmPassword)) {
                createUser(fullName, username, password, phone, email);
            } else {
                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUser(String fullName, String username, String password, String phone, String email) {
        String url = "http://10.0.2.2:3000/signup";
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject user = new JSONObject();
        try {
            user.put("fullName", fullName);
            user.put("username", username);
            user.put("password", password);
            user.put("phone", phone);
            user.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, user,
                response -> {
                    try {
                        Toast.makeText(SignUpActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                        JSONObject userObject = response.getJSONObject("user");
                        String userId = userObject.getString("_id");
                        Intent intent = new Intent(SignUpActivity.this, SelfInformation1Activity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(SignUpActivity.this, "Error creating account: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(jsonObjectRequest);
    }
}
