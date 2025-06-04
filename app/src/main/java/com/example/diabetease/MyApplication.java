// MyApplication.java
package com.example.diabetease; // Your package name

import android.app.Application;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "divhwzsik"); // Replace with your Cloudinary cloud name
        // For unsigned uploads, you often don't need API key/secret here in the client.
        // The upload preset handles the authorization.
        // config.put("api_key", "YOUR_API_KEY");
        // config.put("api_secret", "YOUR_API_SECRET"); // AGAIN, AVOID IN CLIENT FOR SECURITY
        MediaManager.init(this, config);
    }
}