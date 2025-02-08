package com.mgsanlet.cheftube.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mgsanlet.cheftube.R;
import com.mgsanlet.cheftube.home.recycler.RecipeFeedAdapter;

import java.io.Serializable;
import java.util.List;

import model.Recipe;
import model.RecipeModel;

/**
 * A fragment that displays a list of recipes. Each recipe is shown with its title and an image.
 * When a recipe is clicked, the fragment navigates to a detailed view of the selected recipe,
 * displaying additional information such as ingredients, preparation steps, and video content.
 * If there are no recipes to display, a message indicating that no results were found is shown.
 *
 * @author MarioG
 */
public class RecipeListFragment extends Fragment {
    // -Declaring constant for argument key-
    private static final String ARG_RECIPELIST = "recipeList";
    // -Declaring data members-
    private List<Recipe> mRecipeList;
    // -Declaring UI elements-
    private RecyclerView recipesRecycler;
    private TextView noResultsTextView; // Added for no results message

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
        if (getArguments() != null) {
            try {
                mRecipeList = (List<Recipe>) getArguments().getSerializable(ARG_RECIPELIST);
            } catch (Exception e) {
                System.err.println("Wrong casting from Serializable to List<Recipe>");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        // -Initializing UI elements-
        recipesRecycler = view.findViewById(R.id.recipeFeedRecyclerView);
        noResultsTextView = view.findViewById(R.id.noResultsTextView);
        recipesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // -Checking if mRecipeList is null and retrieving default recipes from RecipeModel-
        verifyRecipeList();

        if (mRecipeList.isEmpty()) {
            displayNoResults();
        } else {
            RecipeFeedAdapter adapter = new RecipeFeedAdapter(mRecipeList, getParentFragmentManager());
            recipesRecycler.setAdapter(adapter);
            noResultsTextView.setVisibility(View.GONE); // -Hiding no results message-
            recipesRecycler.setVisibility(View.VISIBLE); // -Showing RecyclerView-
        }
        return view;
    }

    /**
     * Verifies the availability of the recipe list, falling back to RecipeModel if necessary.
     */
    private void verifyRecipeList() {
        if (this.mRecipeList == null) {
            this.mRecipeList = RecipeModel.getInstance();
        }
    }

    /**
     * Displays a message indicating no results were found.
     */
    private void displayNoResults() {
        noResultsTextView.setVisibility(View.VISIBLE); // -Showing the no results message-
        recipesRecycler.setVisibility(View.GONE); // -Hiding the RecyclerView-
    }
}