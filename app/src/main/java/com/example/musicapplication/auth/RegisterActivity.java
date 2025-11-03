package com.example.musicapplication.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapplication.R;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText etEmail = findViewById(R.id.et_email);
        EditText etPassword = findViewById(R.id.et_password);
        EditText etConfirm = findViewById(R.id.et_confirm_password);
        Button btnRegister = findViewById(R.id.btn_register_action);

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString();
            String conf = etConfirm.getText().toString();
            if (email.isEmpty() || pass.isEmpty() || !pass.equals(conf)) {
                Toast.makeText(this, "Vui lòng nhập thông tin chính xác", Toast.LENGTH_SHORT).show();
                return;
            }
            // Placeholder: accept any valid input
            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

