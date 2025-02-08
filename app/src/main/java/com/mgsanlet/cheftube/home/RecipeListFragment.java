package com.mgsanlet.cheftube.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mgsanlet.cheftube.FragmentNavigator;
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
 * @author MarioG
 */
public class RecipeListFragment extends Fragment {
    // -Declaring constant for argument key-
    private static final String ARG_RECIPELIST = "recipeList";
    // -Declaring data members-
    private List<Recipe> mRecipeList;
    // -Declaring UI elements-
    RecyclerView recipesRecycler;

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
        recipesRecycler = view.findViewById(R.id.recipeFeedRecyclerView);
        recipesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // -Checking if mRecipeList is null and retrieving default recipes from RecipeModel-
        verifyRecipeList();

        if(mRecipeList.isEmpty()){
            displayNoResults();
        }else{
            RecipeFeedAdapter adapter = new RecipeFeedAdapter(mRecipeList, getParentFragmentManager());
            recipesRecycler.setAdapter(adapter);
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
    private void displayNoResults() { //TODO fix
        TextView noResults = new TextView(getContext());
        noResults.setText(R.string.no_results);
        if (getContext() != null){
            noResults.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
        noResults.setTextSize(20);
        noResults.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        recipesRecycler.addView(noResults);
    }
}
