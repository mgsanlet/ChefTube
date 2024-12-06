package com.example.recipebook.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.recipebook.FragmentNavigator;
import com.example.recipebook.R;

import java.io.Serializable;
import java.util.List;

import model.Recipe;
import model.RecipeModel;
/**
 * A fragment that displays a list of recipes. Each recipe is shown with its title and an image.
 * When a recipe is clicked, the fragment navigates to a detailed view of the selected recipe,
 * displaying additional information such as ingredients, preparation steps, and video content.
 * @author MarioG
 */
public class RecipeListFragment extends Fragment {
    // -Declaring constant for argument key-
    private static final String ARG_RECIPELIST = "recipeList";
    // -Declaring data members-
    private List<Recipe> mRecipeList;
    // -Declaring UI elements-
    LinearLayout recipesLayout;

    /**
     * Creates a new instance of the fragment with a list of recipes.
     *
     * @param recipeList A list of recipes to display.
     * @return A new instance of RecipeListFragment.
     */
    public static Fragment newInstance(List<Recipe> recipeList) {
        RecipeListFragment fragment = new RecipeListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECIPELIST, (Serializable) recipeList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // -Getting the recipe list passed as an argument to the fragment-
        if(getArguments() != null){

            try{
                //noinspection unchecked
                mRecipeList = (List<Recipe>) getArguments().getSerializable("recipeList");
            }catch (Exception e){
                System.err.println("Wrong casting from Serializable to List<Recipe>");
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        // -Initializing UI elements-
        recipesLayout = view.findViewById(R.id.recipesLayout);

        // -Checking if mRecipeList is null and retrieving default recipes from RecipeModel-
        verifyRecipeList();

        if(mRecipeList.isEmpty()){
            displayNoResults();
        }else{
            displayRecipeList(inflater);
        }
        return view;
    }

    /**
     * Verifies the availability of the recipe list, falling back to RecipeModel if necessary.
     */
    private void verifyRecipeList() {
        if( this.mRecipeList == null){
            this.mRecipeList = RecipeModel.getInstance();
        }
    }

    /**
     * Displays a message indicating no results were found.
     */
    private void displayNoResults() {
        TextView noResults = new TextView(getContext());
        noResults.setText(R.string.no_results);
        if (getContext() != null){
            noResults.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
        noResults.setTextSize(20);
        noResults.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        recipesLayout.addView(noResults);
    }

    /**
     * Displays the list of recipes dynamically.
     *
     * @param inflater The LayoutInflater used to inflate the recipe item views.
     */
    private void displayRecipeList(LayoutInflater inflater) {
        for (final Recipe recipe : mRecipeList) {
            @SuppressLint("InflateParams")
            View recipeView = inflater.inflate(R.layout.item_recipe, null);
            bindRecipeData(recipe, recipeView);

            recipeView.setOnClickListener(v -> navToRecipeDetail(recipe));

            recipesLayout.addView(recipeView);
        }
    }

    /**
     * Binds the recipe data (title and image) to the given recipe view.
     *
     * @param recipe The recipe to bind data from.
     * @param recipeView The view to bind data to.
     */
    private static void bindRecipeData(Recipe recipe, View recipeView) {
        TextView title = recipeView.findViewById(R.id.recipeTitle);
        ImageView image = recipeView.findViewById(R.id.recipeImage);
        title.setText(recipe.getTtlRId());
        image.setImageResource(recipe.getImgRId());
    }

    /**
     * Opens the detailed view of the selected recipe.
     *
     * @param recipe The recipe whose details are to be displayed.
     */
    private void navToRecipeDetail(Recipe recipe) {
        RecipeDetailFragment detailFragment = RecipeDetailFragment.newInstance(recipe);
        FragmentNavigator.loadFragmentInstance(
                null, this, detailFragment, R.id.mainFrContainer
        );
    }
}
