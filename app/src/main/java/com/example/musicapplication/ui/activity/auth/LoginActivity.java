package com.example.musicapplication.ui.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapplication.R;
import com.example.musicapplication.data.repository.AuthRepository;
import com.example.musicapplication.ui.activity.main.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private TextView tvRegister;
    private TextView tvForgotPassword;
    private ProgressBar progressBar;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository(this);
        
        // Check if already logged in
        if (authRepository.isLoggedIn()) {
            navigateToMain();
            return;
        }

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login_action);
        tvRegister = findViewById(R.id.tv_register);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        progressBar = findViewById(R.id.progress_bar);

        // Check if user just registered
        if (getIntent().getBooleanExtra("registered", false)) {
            Toast.makeText(this, "Đăng ký thành công! Vui lòng đăng nhập", Toast.LENGTH_LONG).show();
        }

        btnLogin.setOnClickListener(v -> handleLogin());
        
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        
        tvForgotPassword.setOnClickListener(v -> handleForgotPassword());
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etPassword.requestFocus();
            return;
        }

        setLoading(true);
        
        authRepository.login(email, password, new AuthRepository.OnAuthResultListener() {
            @Override
            public void onSuccess(AuthRepository.FirebaseUserWrapper user) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                navigateToMain();
            }

            @Override
            public void onError(Exception error) {
                setLoading(false);
                String errorMessage = "Đăng nhập thất bại";
                if (error.getMessage() != null) {
                    if (error.getMessage().contains("password")) {
                        errorMessage = "Mật khẩu không đúng";
                    } else if (error.getMessage().contains("user")) {
                        errorMessage = "Tài khoản không tồn tại";
                    } else {
                        errorMessage = error.getMessage();
                    }
                }
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        etEmail.setEnabled(!loading);
        etPassword.setEnabled(!loading);
    }

    private void handleForgotPassword() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email để khôi phục mật khẩu");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return;
        }

        setLoading(true);

        authRepository.resetPassword(email, new AuthRepository.OnResetPasswordListener() {
            @Override
            public void onSuccess() {
                setLoading(false);
                Toast.makeText(LoginActivity.this, 
                    "Email khôi phục mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư!", 
                    Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Exception error) {
                setLoading(false);
                String errorMessage = "Gửi email thất bại";
                if (error.getMessage() != null) {
                    if (error.getMessage().contains("user")) {
                        errorMessage = "Email không tồn tại trong hệ thống";
                    } else {
                        errorMessage = error.getMessage();
                    }
                }
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

