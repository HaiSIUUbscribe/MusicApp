# 📘 VÍ DỤ REFACTOR: LoginActivity.java

> **Mục đích:** Ví dụ cụ thể refactor 1 file hoàn chỉnh từ đầu đến cuối
> 
> **File:** `app/src/main/java/com/example/musicapplication/ui/activity/auth/LoginActivity.java`
> 
> **Thời gian:** ~10 phút

---

## 📊 TRƯỚC KHI REFACTOR

### Phân tích code hiện tại

**LoginActivity.java** có các vấn đề:
- ❌ Validation code lặp lại (email, password)
- ❌ Toast messages không nhất quán
- ❌ Log.d/Log.e với TAG
- ❌ Hardcoded strings

---

## 🔧 BƯỚC 1: THÊM IMPORTS

### Thêm vào đầu file (sau package declaration)

```java
package com.example.musicapplication.ui.activity.auth;

// ... các imports cũ ...

// ✅ THÊM CÁC IMPORTS MỚI
import com.example.musicapplication.utils.ToastUtils;
import com.example.musicapplication.utils.ValidationUtils;
import com.example.musicapplication.utils.Logger;
import com.example.musicapplication.utils.NetworkUtils;
```

---

## 🔧 BƯỚC 2: XÓA TAG CONSTANT

### ❌ Trước

```java
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    
    // ... rest of code ...
}
```

### ✅ Sau

```java
public class LoginActivity extends AppCompatActivity {
    // XÓA dòng TAG
    
    // ... rest of code ...
}
```

**Lý do:** Logger tự động tạo TAG từ tên class

---

## 🔧 BƯỚC 3: REFACTOR VALIDATION

### ❌ Trước

```java
private boolean validateInputs() {
    String email = etEmail.getText().toString().trim();
    String password = etPassword.getText().toString();
    
    // Validate email
    if (email.isEmpty()) {
        etEmail.setError("Email không được để trống");
        etEmail.requestFocus();
        return false;
    }
    
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        etEmail.setError("Email không hợp lệ");
        etEmail.requestFocus();
        return false;
    }
    
    // Validate password
    if (password.isEmpty()) {
        etPassword.setError("Mật khẩu không được để trống");
        etPassword.requestFocus();
        return false;
    }
    
    if (password.length() < 6) {
        etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
        etPassword.requestFocus();
        return false;
    }
    
    return true;
}
```

### ✅ Sau

```java
private boolean validateInputs() {
    String email = etEmail.getText().toString().trim();
    String password = etPassword.getText().toString();
    
    // Validate email
    if (!ValidationUtils.isValidEmail(email)) {
        etEmail.setError(ValidationUtils.getEmailError(email));
        etEmail.requestFocus();
        return false;
    }
    
    // Validate password
    if (!ValidationUtils.isValidPassword(password)) {
        etPassword.setError(ValidationUtils.getPasswordError(password));
        etPassword.requestFocus();
        return false;
    }
    
    return true;
}
```

**Kết quả:** Giảm từ 28 dòng xuống 16 dòng! (-43%)

---

## 🔧 BƯỚC 4: REFACTOR TOAST

### ❌ Trước

```java
private void loginUser() {
    if (!validateInputs()) {
        return;
    }
    
    String email = etEmail.getText().toString().trim();
    String password = etPassword.getText().toString();
    
    progressBar.setVisibility(View.VISIBLE);
    
    mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            
            if (task.isSuccessful()) {
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                String error = task.getException() != null ? 
                    task.getException().getMessage() : "Đăng nhập thất bại";
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
            }
        });
}
```

### ✅ Sau

```java
private void loginUser() {
    if (!validateInputs()) {
        return;
    }
    
    String email = etEmail.getText().toString().trim();
    String password = etPassword.getText().toString();
    
    progressBar.setVisibility(View.VISIBLE);
    
    mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            
            if (task.isSuccessful()) {
                ToastUtils.showSuccess(this, "Đăng nhập thành công!");
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                String error = task.getException() != null ? 
                    task.getException().getMessage() : "Đăng nhập thất bại";
                ToastUtils.showError(this, error);
            }
        });
}
```

---

## 🔧 BƯỚC 5: REFACTOR LOG

### ❌ Trước

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    
    Log.d(TAG, "LoginActivity created");
    
    initViews();
    setupListeners();
}

private void loginUser() {
    // ...
    
    mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Login successful for user: " + email);
                // ...
            } else {
                Log.e(TAG, "Login failed", task.getException());
                // ...
            }
        });
}
```

### ✅ Sau

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    
    Logger.d("LoginActivity created");
    
    initViews();
    setupListeners();
}

private void loginUser() {
    // ...
    
    mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Logger.d("Login successful for user: " + email);
                // ...
            } else {
                Logger.e("Login failed", task.getException());
                // ...
            }
        });
}
```

---

## 🔧 BƯỚC 6: THÊM NETWORK CHECK (BONUS)

### ✅ Thêm kiểm tra mạng trước khi login

```java
private void loginUser() {
    // ✅ THÊM: Kiểm tra kết nối mạng
    if (!NetworkUtils.isNetworkAvailable(this)) {
        ToastUtils.showError(this, "Không có kết nối mạng");
        return;
    }
    
    if (!validateInputs()) {
        return;
    }
    
    // ... rest of code ...
}
```

---

## 📊 KẾT QUẢ SAU KHI REFACTOR

### So sánh

| Metric | Trước | Sau | Cải thiện |
|--------|-------|-----|-----------|
| **Số dòng code** | ~150 | ~110 | -27% |
| **Validation code** | 28 dòng | 16 dòng | -43% |
| **Toast calls** | 5 chỗ | 5 chỗ | Ngắn gọn hơn |
| **Log calls** | 3 chỗ | 3 chỗ | Không cần TAG |
| **Imports** | 15 | 19 | +4 (utilities) |

### Lợi ích

✅ **Code ngắn gọn hơn** - Giảm 40 dòng code

✅ **Dễ đọc hơn** - Validation rõ ràng, Toast có emoji

✅ **Dễ bảo trì hơn** - Thay đổi validation ở 1 chỗ (ValidationUtils)

✅ **Nhất quán hơn** - Tất cả Toast/Log đều giống nhau

✅ **An toàn hơn** - Kiểm tra network trước khi login

---

## ✅ CHECKLIST HOÀN THÀNH

- [x] Thêm imports: ToastUtils, ValidationUtils, Logger, NetworkUtils
- [x] Xóa TAG constant
- [x] Refactor email validation
- [x] Refactor password validation
- [x] Refactor Toast messages
- [x] Refactor Log calls
- [x] Thêm network check
- [x] Xóa imports không dùng (Patterns nếu không dùng nữa)
- [x] Test chạy app
- [x] Commit changes

---

## 🧪 TESTING

### Test cases

1. **Nhập email sai** → Hiện "Email không hợp lệ"
2. **Nhập password < 6 ký tự** → Hiện "Mật khẩu phải có ít nhất 6 ký tự"
3. **Login thành công** → Hiện "✅ Đăng nhập thành công!"
4. **Login thất bại** → Hiện "❌ [error message]"
5. **Không có mạng** → Hiện "❌ Không có kết nối mạng"

---

## 💾 COMMIT MESSAGE

```bash
git add app/src/main/java/com/example/musicapplication/ui/activity/auth/LoginActivity.java
git commit -m "Refactor LoginActivity: Use ValidationUtils, ToastUtils, Logger, NetworkUtils"
```

---

**🎉 HOÀN THÀNH! BẠN ĐÃ REFACTOR THÀNH CÔNG LoginActivity!**

**Tiếp theo:** Áp dụng tương tự cho RegisterActivity.java


