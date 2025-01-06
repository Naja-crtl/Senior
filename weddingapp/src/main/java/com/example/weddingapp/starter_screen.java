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

public class starter_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter_screen);

        // Find the logo ImageView and app title TextView
        ImageView logoImage = findViewById(R.id.logoImage);
        TextView appTitle = findViewById(R.id.appTitle);

        // Add a small delay to start the circular reveal animation
        new Handler().postDelayed(() -> {
            // Get the center of the logo view and calculate the radius
            int centerX = logoImage.getWidth() / 2;
            int centerY = logoImage.getHeight() / 2;
            float radius = (float) Math.hypot(logoImage.getWidth(), logoImage.getHeight());

            // Create the circular reveal animation
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(
                    logoImage, centerX, centerY, 0, radius);

            // Set the animation duration
            revealAnimator.setDuration(1500);

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

                    // Delay before navigating to the splash screen
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(starter_screen.this, signin_page.class);
                        startActivity(intent);
                        finish(); // Close the starter screen activity
                    }, 2000); // Wait for the fade-in animation to complete
                }
            });
        }, 500); // Initial delay before starting the circular reveal
    }
}
