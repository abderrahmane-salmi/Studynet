package com.salmi.bouchelaghem.studynet.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.salmi.bouchelaghem.studynet.databinding.ActivitySignUpBinding;
import com.salmi.bouchelaghem.studynet.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

//        binding.splashMotionLayout.addTransitionListener(new MotionLayout.TransitionListener() {
//            @Override
//            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {
//
//            }
//
//            @Override
//            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {
//
//            }
//
//            @Override
//            public void onTransitionCompleted(MotionLayout motionLayout, int i) {
//                startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
//            }
//
//            @Override
//            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {
//
//            }
//        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
                finish();
            }
        }, 1500);
    }
}