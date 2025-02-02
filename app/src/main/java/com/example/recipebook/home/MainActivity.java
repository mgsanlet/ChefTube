package com.example.recipebook.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.example.recipebook.FragmentNavigator;
import com.example.recipebook.R;
import com.example.recipebook.auth.AuthActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;
import java.util.Locale;
import model.Recipe;
import model.RecipeModel;
import model.User;
/**
 * MainActivity serves as the primary activity for the application's home screen.
 * It manages the top MaterialToolbar, BottomNavigationView, and fragments
 * for various sections of the app.
 * @author MarioG
 */
public class MainActivity extends AppCompatActivity {
    // -Declaring UI elements-
    MaterialToolbar topToolbar;
    BottomNavigationView bottomNavView;
    // -Declaring data members-
    User mloggedUser;
    // -Declaring intent-
    Intent authActIntent;
    // -Declaring string resources-
    String noEmailAppStr;
    String featureInPrStr;
    String resultsStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // -Initializing UI elements-
        topToolbar = findViewById(R.id.materialToolbar);
        bottomNavView = findViewById(R.id.bottomNavigationView);

        // -Initializing string resources-
        noEmailAppStr = getString(R.string.no_email_app);
        featureInPrStr = getString(R.string.feature_in_progress);
        resultsStr = getString(R.string.results);

        // -Saving user data arriving from authentication activity-
        mloggedUser = (User) getIntent().getSerializableExtra("user");

        // -Setting up material toolbar-
        setSupportActionBar(topToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        // -Setting up bottom navigation view-
        setUpBottomNav();
    }

    /* ************************************************************
     *                  TOP MATERIAL BAR MENU                     *
     ************************************************************ */
    // Top menu managing
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_contact_us) {
            onContactUs();
        }
        if (id == R.id.action_language) {
            onLanguage();
        }
        if (id == R.id.action_logout) {
            onLogout();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens an email app to allow the user to contact support.
     * Uses an Intent with `ACTION_SENDTO` and the "mailto" URI scheme.
     * If no email app is available, shows a Toast message.
     */
    private void onContactUs() {
        String emailAddress = "support@cheftube.com";

        // -Creating an Intent to send an email-
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", emailAddress, null));
        /* -Checking if there is an app in package manager (system)
            to handle the action SENDTO/mailto- */
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        } else {
            Toast.makeText(getApplicationContext(), noEmailAppStr, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Displays a dialog for language selection and updates the app's locale.
     */
    private void onLanguage() {
        String[] languages = getResources().getStringArray(R.array.languages);
        // -Language codes ISO 639-1 format-
        final String[] languageCodes = {"en", "es", "it"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.dialog_title));
        builder.setItems(languages, (dialog, which) -> {
            // -This code is executed when a language is selected-
            String selectedLanguage = languageCodes[which]; // -'which' is the item index-
            setLocale(selectedLanguage); // -Setting the language of the app-
        });
        builder.show();
    }

    /**
     * Sets the app's locale to the specified language code and restarts the activity.
     *
     * @param languageCode The language code to set, e.g., "en", "es".
     */
    private void setLocale(String languageCode) {
        Resources res = getResources();
        // -Getting the current configuration of the app-
        Configuration config = res.getConfiguration();
        config.setLocale(new Locale(languageCode));
        // -Updating the resources configuration with the new locale settings-
        res.updateConfiguration(config, res.getDisplayMetrics());
        // -Restarting the activity-
        recreate();
    }

    /**
     * Logs out the user and redirects to the authentication activity.
     */
    private void onLogout() {
        authActIntent = new Intent(MainActivity.this, AuthActivity.class);
        startActivity(authActIntent);
    }

    /* **********************************************************
     *                  BOTTOM NAVIGATION VIEW                  *
     ********************************************************** */

    /**
     * Configures the listener for the BottomNavigationView to handle navigation between fragments.
     */
    private void setUpBottomNav() {
        bottomNavView.setSelectedItemId(R.id.home_item); //Default
        // -Removing insets and padding because they cause issues with the  bottom navigation view-
        bottomNavView.setOnApplyWindowInsetsListener(null);
        bottomNavView.setPadding(0,0,0,0);

        // -Setting up the listener-
        bottomNavView.setOnItemSelectedListener(item -> {
           if (item.getItemId() == R.id.profile_item) {
               Fragment profileFragment = ProfileFragment.newInstance(mloggedUser);
               FragmentNavigator.loadFragmentInstance(
                       this, null, profileFragment, R.id.mainFrContainer
               );
               return true;
           } else if (item.getItemId() == R.id.search_item) {
               setUpSearchBtn();
               return true;
           } else if (item.getItemId() == R.id.home_item) {
               FragmentNavigator.loadFragment(
                       this, null, new RecipeListFragment(), R.id.mainFrContainer
               );
               return true;
           } else if (item.getItemId() == R.id.chrono_item) {
               Toast.makeText(this, featureInPrStr, Toast.LENGTH_SHORT).show();
               return true;
           } else if (item.getItemId() == R.id.health_item){
               FragmentNavigator.loadFragment(
                       this, null, new HealthyFragment(), R.id.mainFrContainer
               );
               return true;
           } else {
               return false;
           }
       });
    }

    /**
     * Sets up the search dialog to filter recipes based on user input.
     */
    private void setUpSearchBtn() {
        AlertDialog.Builder searchDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        // -Inflating a custom layout for the search dialog-
        View dialogView = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.dialog_search, null);
        // -Attaching the custom layout to the dialog builder-
        searchDialogBuilder.setView(dialogView);
        // -Getting a reference to the UI elements in the custom layout-
        EditText input = dialogView.findViewById(R.id.editTextSearch);
        Button okBtn = dialogView.findViewById(R.id.okBtn);

        AlertDialog searchDialog = searchDialogBuilder.create();
        searchDialog.show();

        okBtn.setOnClickListener(v -> {
            String query = input.getText().toString().trim();
            // -Filtering the recipes based on the input query-
            List<Recipe> filteredRecipes = RecipeModel.getFilteredRecipes(
                    MainActivity.this, query
            );

            Toast.makeText(this,resultsStr + filteredRecipes.size(),
                    Toast.LENGTH_SHORT
            ).show();

            // -Creating a new fragment with the filtered recipes and replacing the current one-
            Fragment recipeListFragment = RecipeListFragment.newInstance(filteredRecipes);
            FragmentNavigator.loadFragmentInstance(
                    this, null, recipeListFragment, R.id.mainFrContainer
            );

            searchDialog.dismiss();
        });
    }
}

