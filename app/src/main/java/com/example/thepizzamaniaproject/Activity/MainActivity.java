package com.example.thepizzamaniaproject.Activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.thepizzamaniaproject.Helper.SessionManager;
import com.example.thepizzamaniaproject.R;

public class MainActivity extends AppCompatActivity {


    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });


        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // To check if user is logged in
        if (sessionManager.isLoggedIn())
        {
            goToHomeActivity(); // Redirect to home if logged in
            return; // Stop execution here
        }


        // Rotaion animation in main activity

        ImageView pizzaImage = findViewById(R.id.imageView2);

        ObjectAnimator slowRotate = ObjectAnimator.ofFloat(pizzaImage, "rotation", 0f, 360f);
        slowRotate.setDuration(60000); // 60 seconds = 1 rotation per minute
        slowRotate.setInterpolator(new LinearInterpolator());
        slowRotate.setRepeatCount(ObjectAnimator.INFINITE); // loop forever
        slowRotate.start();


        // Open login activity when get started button clicked
        AppCompatButton getStartBtn = findViewById(R.id.getStartBtn);

//        getStartBtn.setOnClickListener(v ->
//        {
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//        });


        // DEBUG: Check if button is found
        if (getStartBtn == null)
        {
            System.out.println("MainActivity: Button not found!"); // DEBUG
        }
        else
        {
            System.out.println("MainActivity: Button found successfully"); // DEBUG
        }


        // To applies a smooth animation between the activities
        getStartBtn.setOnClickListener(v ->
        {
            System.out.println("MainActivity: Button clicked!"); // DEBUG
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            // close MainActivity
            //finish();

            System.out.println("MainActivity: onCreate completed");
        });


    }


    private void goToHomeActivity()
    {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();

        // finish(); - used to close MainActivity so user can't go back
    }
}



