package com.mgsanlet.cheftube.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.mgsanlet.cheftube.R;
import model.Recipe;

/**
 * A fragment that displays the details of a recipe, including its title, ingredients,
 * preparation steps, and an embedded video (if available).
 * This fragment is intended to be used with a {@link Recipe} object, which is passed to it
 * via a {@link Bundle} of arguments. The fragment then dynamically displays the recipe's
 * information in the user interface.
 */
public class RecipeDetailFragment extends Fragment {

    // -Declaring constant for argument key-
    private static final String ARG_RECIPE = "recipe";

    /**
     * Factory method to create a new instance of this fragment with the specified recipe.
     *
     * @param recipe The recipe whose details will be shown.
     * @return A new instance of the RecipeDetailFragment.
     */
    public static RecipeDetailFragment newInstance(Recipe recipe) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECIPE, recipe); // -Passing recipe object as argument-
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        Recipe recipe = null;
        // -Getting the recipe object passed as an argument to the fragment-
        if(getArguments() != null){
            recipe = (Recipe) getArguments().getSerializable(ARG_RECIPE);
        }

        // -Initializing UI elements-
        TextView title = view.findViewById(R.id.recipeDetailTitle);
        WebView webView = view.findViewById(R.id.recipeDetailVideo);
        LinearLayout ingredientsContainer = view.findViewById(R.id.ingredientsLinearLayout);
        LinearLayout stepsContainer = view.findViewById(R.id.stepsLinearLayout);

        // -Setting title and video-
        if (recipe != null){
            title.setText(getString(recipe.getTtlRId()));
            // -Enabling JavaScript for the WebView and loading the recipe's video-
            webView.getSettings().setJavaScriptEnabled(true);
            String videoUrl = recipe.getVideoUrl();
            webView.loadUrl(videoUrl);

            // -Dynamically adding ingredients to the ingredients container-
            for (Integer ingredientId : recipe.getIngrRIds()) {
                TextView ingredientTextView = new TextView(getContext());
                ingredientTextView.setText(ingredientId);
                if(getContext() != null){
                    ingredientTextView.setTextColor(
                            ContextCompat.getColor(getContext(), R.color.white)
                    );
                }
                ingredientTextView.setTextSize(16);
                ingredientsContainer.addView(ingredientTextView);
            }

            // -Dynamically adding steps to the steps container-
            for (Integer stepId : recipe.getStepsRIds()) {
                TextView stepTextView = new TextView(getContext());
                stepTextView.setText(stepId);
                stepTextView.setPadding(0,4,0,2);
                if(getContext() != null){
                    stepTextView.setTextColor(
                            ContextCompat.getColor(getContext(), R.color.white)
                    );
                }
                stepTextView.setTextSize(12);
                stepsContainer.addView(stepTextView);
            }
        }

        return view;
    }
}
