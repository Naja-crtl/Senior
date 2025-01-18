package com.example.weddingapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class starter_screen extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private static final String TAG = "StarterScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter_screen);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Find the logo ImageView and app title TextView
        ImageView logoImage = findViewById(R.id.logoImage);
        TextView appTitle = findViewById(R.id.appTitle);

        // Start animations
        startAnimations(logoImage, appTitle);
    }

    private void startAnimations(ImageView logoImage, TextView appTitle) {
        Log.d(TAG, "Starting animations...");

        // Ensure the logo view is laid out before starting animations
        logoImage.post(() -> {
            // Perform the circular reveal animation
            try {
                int centerX = logoImage.getWidth() / 2;
                int centerY = logoImage.getHeight() / 2;
                float radius = (float) Math.hypot(logoImage.getWidth(), logoImage.getHeight());

                Animator revealAnimator = ViewAnimationUtils.createCircularReveal(
                        logoImage, centerX, centerY, 0, radius);
                revealAnimator.setDuration(1500); // Animation duration: 1500ms
                logoImage.setVisibility(View.VISIBLE);
                revealAnimator.start();

                revealAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        // Start fade-in animation for the app title
                        Animation fadeInAnimation = AnimationUtils.loadAnimation(starter_screen.this, R.anim.fade_in);
                        appTitle.startAnimation(fadeInAnimation);

                        // Navigate to the next screen after animations
                        navigateToNextScreen();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Animation error: " + e.getMessage());
                navigateToNextScreen(); // Fallback to navigation if animation fails
            }
        });
    }

    private void navigateToNextScreen() {
        Log.d(TAG, "Navigating to the next screen...");
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Intent intent;

        if (currentUser != null) {
            Log.d(TAG, "User is signed in.");
            intent = new Intent(starter_screen.this, dashboard.class);
        } else {
            Log.d(TAG, "User is not signed in.");
            intent = new Intent(starter_screen.this, signin_page.class);
        }

        startActivity(intent);
        finish();
    }
}
