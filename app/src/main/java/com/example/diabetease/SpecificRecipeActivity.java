package com.example.diabetease;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.util.Map;

public class SpecificRecipeActivity extends AppCompatActivity {
    private ImageView recipeImage;
    private TextView name, category, calories, servings, cookTime, description;
    private TextView nutritionInfo, instructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_specific_recipe);


        recipeImage = findViewById(R.id.recipe_image);
        name = findViewById(R.id.recipe_name);
        category = findViewById(R.id.recipe_category);
        calories = findViewById(R.id.recipe_calories);
        servings = findViewById(R.id.recipe_servings);
        cookTime = findViewById(R.id.recipe_cook_time);
        description = findViewById(R.id.recipe_description);
        nutritionInfo = findViewById(R.id.recipe_nutrition);
        instructions = findViewById(R.id.recipe_instructions);

        Recipes recipe = getIntent().getParcelableExtra("recipe");

        if (recipe != null) {
            Glide.with(this).load(recipe.getImage_url()).into(recipeImage);
            name.setText(recipe.getName());
            category.setText(recipe.getCategory());
            calories.setText(String.valueOf(recipe.getCalories()) + " kcal");
            servings.setText("Servings: " + recipe.getServings());
            cookTime.setText("Cook Time: " + recipe.getCook_time() + " mins");
            description.setText(recipe.getDescription());

            nutritionInfo.setText(TextUtils.join("\n", recipe.getNutri_info()));

            StringBuilder instructionText = new StringBuilder();
            for (Map<String, Object> step : recipe.getInstructions()) {
                int stepNum = ((Long) step.get("number")).intValue();
                String text = (String) step.get("text");
                instructionText.append("Step ").append(stepNum).append(": ").append(text).append("\n\n");
            }
            instructions.setText(instructionText.toString());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });
    }
}
}