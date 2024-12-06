package com.example.recipebook.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import model.User;
import model.UserModel;
import com.example.recipebook.FragmentNavigator;
import com.example.recipebook.home.MainActivity;
import com.example.recipebook.R;

/**
 * A fragment that handles the login process for the application.
 * It allows users to enter their credentials (username and password) and
 * logs them into the application.
 * Additionally, it provides a link to navigate to the sign-up fragment if the user
 * does not have an account.
 * @author MarioG
 */
public class LoginFragment extends Fragment {
    // -Declaring constants for argument keys-
    private static final String ARG_USERNAME = "username";
    private static final String ARG_PWD = "pwd";

    // -Declaring data members-
    private String mUsername;
    private String mPwd;
    // -Declaring UI elements-
    EditText identityField;
    EditText pwdField;
    Button loginBtn;
    TextView signUpLink;
    // -Declaring intent-
    Intent mainActIntent;
    // -Declaring string resources-
    String requiredStr;
    String invalidLoginStr;

    /**
     * Creates a new instance of LoginFragment with the specified username and password.
     * It is used from the SignUpFragment to prefill the login form with the user's credentials.
     * @param username The username to be prefilled in the login form.
     * @param pwd The password to be prefilled in the login form.
     * @return A new instance of LoginFragment with the provided username and password.
     */
    public static LoginFragment newInstance(String username, String pwd) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_PWD, pwd);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // -Getting arguments passed to the fragment-
        if(getArguments() != null){
            mUsername = getArguments().getString(ARG_USERNAME);
            mPwd = getArguments().getString(ARG_PWD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // -Initializing UI elements-
        identityField = view.findViewById(R.id.loginIdentityField);
        pwdField = view.findViewById(R.id.loginPwdField);
        loginBtn = view.findViewById(R.id.signInBtn);
        signUpLink = view.findViewById(R.id.loginSignUpLink);
        // -Initializing intent-
        mainActIntent = new Intent(getActivity(), MainActivity.class);
        // -Initializing string resources-
        requiredStr = getString(R.string.required);
        invalidLoginStr = getString(R.string.invalid_login);

        // -Setting username and password if passed from the previous screen-
        if(mUsername != null && mPwd != null){
            identityField.setText(mUsername);
            pwdField.setText(mPwd);
        }

        // -Setting up listeners-
        loginBtn.setOnClickListener(v -> tryLogin());

        signUpLink.setOnClickListener(v -> {
            cleanErrors();
            FragmentNavigator.loadFragment(null, this, new SignUpFragment(), R.id.authFrContainer);
        });
        return view;
    }

    /**
     * Attempts to log the user in by validating their input and starting the main activity if credentials are correct.
     */
    private void tryLogin() {
        if (fieldsAreEmpty()) return;
        // -Getting the valid user if the credentials match-
        User validUser = getValidUser();

        if (validUser == null) {
            Toast.makeText(getContext(), invalidLoginStr, Toast.LENGTH_SHORT).show();
        } else {
            navToHomePage(validUser);
        }
    }

    /**
     * Checks whether the login fields (username and password) are empty.
     * If any field is empty, an error message is displayed for the corresponding field.
     *
     * @return True if any field is empty, false otherwise.
     */
    private boolean fieldsAreEmpty(){
        boolean empty = false;
        if (identityField.getText().toString().trim().isEmpty()) {
            identityField.setError(requiredStr);
            empty = true;
        }
        if (pwdField.getText().toString().trim().isEmpty()) {
            pwdField.setError(requiredStr);
            empty = true;
        }
        return empty;
    }

    /**
     * Validates the user credentials by checking if the entered username/email and password
     * match any of the users in the UserModel.
     *
     * @return The valid user if credentials match, null otherwise.
     */
    public @Nullable User getValidUser(){
        String userInputIdentity = identityField.getText().toString();
        String userInputPwd = pwdField.getText().toString();
        for(User user : UserModel.getInstance()){
            // -Checking if either email or username matches and if the password is correct-
            if ((user.getEmail().equals(userInputIdentity) ||
                 user.getUsername().equals(userInputIdentity)) &&
                   user.getPassword().equals(userInputPwd)){
                return user;
            }
        }
        return null;
    }

    /**
     * Clears the error messages from the input fields (username and password).
     */
    public void cleanErrors(){
        identityField.setError(null);
        pwdField.setError(null);
    }

    /**
     * Navigates to the home page (MainActivity) with the valid user's data.
     *
     * @param validUser The user that successfully logged in.
     */
    private void navToHomePage(User validUser) {
        mainActIntent.putExtra("user", validUser);
        startActivity(mainActIntent);
        if(getActivity() != null) getActivity().finish();
    }
}