package com.example.musicapplication.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapplication.R;



public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login_action);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString();
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email hoặc password", Toast.LENGTH_SHORT).show();
                return;
            }
            // Placeholder: accept any non-empty credentials
            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

