package com.example.weddingapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter_screen);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Find the logo ImageView and app title TextView
        ImageView logoImage = findViewById(R.id.logoImage);
        TextView appTitle = findViewById(R.id.appTitle);

        // Ensure the logoImage view is fully attached before starting animations
        logoImage.post(() -> {
            // Get the center of the logo view and calculate the radius
            int centerX = logoImage.getWidth() / 2;
            int centerY = logoImage.getHeight() / 2;
            float radius = (float) Math.hypot(logoImage.getWidth(), logoImage.getHeight());

            // Create the circular reveal animation
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(
                    logoImage, centerX, centerY, 0, radius);

            // Set the animation duration
            revealAnimator.setDuration(3000); // 3000ms for the circular reveal animation

            // Make the logo visible
            logoImage.setVisibility(View.VISIBLE);

            // Start the circular reveal animation
            revealAnimator.start();

            // Add a listener to start the fade-in animation for the app title after the reveal
            revealAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    // Load fade-in animation from resources
                    Animation fadeInAnimation = AnimationUtils.loadAnimation(starter_screen.this, R.anim.fade_in);

                    // Apply the fade-in animation to the app title
                    appTitle.startAnimation(fadeInAnimation);

                    // Delay before navigating based on the authentication state
                    new Handler().postDelayed(() -> {
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        Intent intent;
                        if (currentUser != null) {
                            // User is signed in; navigate to dashboard
                            intent = new Intent(starter_screen.this, dashboard.class);
                        } else {
                            // User is not signed in; navigate to signin_page
                            intent = new Intent(starter_screen.this, signin_page.class);
                        }
                        startActivity(intent);
                        finish(); // Close the starter screen activity
                    }, 2000); // Wait for fade-in animation to complete
                }
            });
        });
    }
}