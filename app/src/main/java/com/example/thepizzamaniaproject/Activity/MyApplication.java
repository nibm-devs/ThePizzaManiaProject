package com.example.thepizzamaniaproject.Activity;

import android.app.Application;

import com.cloudinary.android.MediaManager;
import com.example.thepizzamaniaproject.BuildConfig;

import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", BuildConfig.CLOUDINARY_CLOUD_NAME);
        config.put("api_key", BuildConfig.CLOUDINARY_API_KEY);
        config.put("api_secret", BuildConfig.CLOUDINARY_API_SECRET);
        MediaManager.init(this, config);
    }
}

