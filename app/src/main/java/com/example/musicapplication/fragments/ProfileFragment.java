package com.example.musicapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.musicapplication.R;
import com.example.musicapplication.auth.LoginActivity;
import com.example.musicapplication.auth.RegisterActivity;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button btnLogin = view.findViewById(R.id.btn_login);
        Button btnRegister = view.findViewById(R.id.btn_register);

        btnLogin.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));
        btnRegister.setOnClickListener(v -> startActivity(new Intent(getContext(), RegisterActivity.class)));

        return view;
    }
}

