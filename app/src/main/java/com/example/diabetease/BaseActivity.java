package com.example.diabetease;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    protected void setupNavigationBar() {
        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton recipeButton = findViewById(R.id.recipe_button);
        ImageButton logsButton = findViewById(R.id.logs_button);
        ImageButton profileButton = findViewById(R.id.profile_button);

        Class<?> current = this.getClass();

        if (homeButton != null) {
            homeButton.setImageResource(current == HomeActivity.class ? R.drawable.home_1 : R.drawable.home_2);
            homeButton.setOnClickListener(v -> {
                if (!(current == HomeActivity.class)) {
                    startActivity(new Intent(this, HomeActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        }

        if (recipeButton != null) {
            recipeButton.setImageResource(current == RecipeActivity.class ? R.drawable.recipe_2 : R.drawable.recipe_1);
            recipeButton.setOnClickListener(v -> {
                if (!(current == RecipeActivity.class)) {
                    startActivity(new Intent(this, RecipeActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        }

        if (logsButton != null) {
            logsButton.setImageResource(current == LogsActivity.class ? R.drawable.logs_2 : R.drawable.logs_1);
            logsButton.setOnClickListener(v -> {
                if (!(current == LogsActivity.class)) {
                    startActivity(new Intent(this, LogsActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        }

        if (profileButton != null) {
            profileButton.setImageResource(current == ProfileActivity.class ? R.drawable.profile_2 : R.drawable.profile_1);
            profileButton.setOnClickListener(v -> {
                if (!(current == ProfileActivity.class)) {
                    startActivity(new Intent(this, ProfileActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        }
    }
}
