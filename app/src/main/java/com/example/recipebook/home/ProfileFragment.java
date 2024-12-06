package com.example.recipebook.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.recipebook.R;

import model.User;
import model.UserModel;
/**
 * ProfileFragment allows the user to view and update their profile details,
 * including their username, email, and password.It is loaded at main activity
 * when the profile option of BottomNavigationView is clicked.
 * @author MarioG
 */
public class ProfileFragment extends Fragment {
    // -Declaring constants for argument keys-
    private static final String ARG_USER = "user";

    // -Declaring data members-
    private User mUser;
    // -Declaring UI elements-
    EditText nameField;
    EditText emailField;
    EditText pwdField;
    EditText newPwdField;
    EditText newPwd2Field;
    Button saveBtn;
    // -Declaring string resources-
    String dataSavedStr;
    String requiredStr;
    String invalidEmailStr;
    String emailAlreadyStr;
    String shortPwdStr;
    String pwdDMatchStr;
    String wrongPwdStr;

    /**
     * Creates a new instance of ProfileFragment with the given user.
     *
     * @param user The user whose profile is to be displayed and edited.
     * @return A new instance of ProfileFragment with the user data.
     */
    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user); // -Passing the user object to the fragment arguments-
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // -Retrieving the user object from the arguments-
        if (getArguments() != null) {
            mUser = (User) getArguments().getSerializable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // -Initializing UI elements-
        nameField = view.findViewById(R.id.profileNameField);
        emailField = view.findViewById(R.id.profileEmailField);
        pwdField = view.findViewById(R.id.profilePwdField);
        newPwdField = view.findViewById(R.id.profileNewPwdField);
        newPwd2Field = view.findViewById(R.id.profileNewPwd2Field);
        saveBtn = view.findViewById(R.id.profileSaveBtn);

        // -Initializing string resources-
        dataSavedStr = getString(R.string.data_saved);
        requiredStr = getString(R.string.required);
        invalidEmailStr = getString(R.string.invalid_email);
        emailAlreadyStr = getString(R.string.email_already);
        shortPwdStr = getString(R.string.short_pwd);
        pwdDMatchStr = getString(R.string.pwd_d_match);
        wrongPwdStr = getString(R.string.wrong_pwd);

        // -Setting up listeners-
        saveBtn.setOnClickListener(v -> {
            if(isValidData()){
                // -Saving updated user data-
                mUser.saveNewIdentity(
                        nameField.getText().toString(),
                        emailField.getText().toString()
                );
                if(!newPwdField.getText().toString().trim().isEmpty()){
                    mUser.saveNewPassword(newPwdField.getText().toString());
                }
                UserModel.updateUser(mUser); // -Updating the user model-
                Toast.makeText(getContext(), dataSavedStr, Toast.LENGTH_SHORT).show();
            }
        });
        loadData(); // -Loading the user's current data into the fields-
        return view;
    }

    /**
     * Loads the user's data into the respective fields.
     */
    private void loadData(){
        nameField.setText(mUser.getUsername());
        emailField.setText(mUser.getEmail());
    }

    /**
     * Validates the data entered by the user.
     *
     * @return True if all data is valid, false otherwise.
     */
    private boolean isValidData(){
        return  (!fieldsAreEmpty()  &&
                isValidEmail()      &&
                !isExistentEmail()  &&
                isValidPwd()        &&
                pwdsMatch()
        );
    }

    /**
     * Checks if any required fields are empty and sets appropriate errors.
     *
     * @return True if any field is empty, false otherwise.
     */
    private boolean fieldsAreEmpty(){
        boolean empty = false;
        if (nameField.getText().toString().trim().isEmpty()) {
            nameField.setError(requiredStr);
            empty = true;
        }
        if (emailField.getText().toString().trim().isEmpty()) {
            emailField.setError(requiredStr);
            empty = true;
        }
        if (pwdField.getText().toString().trim().isEmpty()) {
            pwdField.setError(requiredStr);
            empty = true;
        }
        /* -Checking if the new password field is filled but the
        confirmation password field is empty */
        if (!newPwdField.getText().toString().trim().isEmpty() &&
                newPwd2Field.getText().toString().trim().isEmpty()) {
            newPwd2Field.setError(requiredStr);
            empty = true;
        }
        /* -Checking reverse */
        if (!newPwd2Field.getText().toString().trim().isEmpty() &&
                newPwdField.getText().toString().trim().isEmpty()) {
            newPwdField.setError(requiredStr);
            empty = true;
        }
        return empty;
    }

    /**
     * Validates the email format using a pattern.
     *
     * @return True if the email format is valid, false otherwise.
     */
    private boolean isValidEmail() {
        String email = emailField.getText().toString();
        boolean isValid = Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if (!isValid) {
            emailField.setError(invalidEmailStr);
        }
        return isValid;
    }

    /**
     * Checks if the entered email already exists for another user.
     *
     * @return True if the email exists, false otherwise.
     */
    private boolean isExistentEmail(){
        String userInputEmail = emailField.getText().toString();
        for(User user : UserModel.getInstance()){
            if (user.getEmail().equals(userInputEmail) &&
                    !user.getEmail().equals(mUser.getEmail())){
                emailField.setError(emailAlreadyStr);
                return true;
            }
        }
        return false;
    }

    /**
     * Validates the new password length.
     *
     * @return True if the password is valid, false otherwise.
     */
    private boolean isValidPwd(){
        boolean isValid = true;
        if(newPwdField.getText().toString().trim().isEmpty()){
            return isValid; // -If the field is empty, password will remain unchanged-
        }
        if (newPwdField.getText().toString().length() < 5) {
            newPwdField.setError(shortPwdStr);
            isValid = false;
        }
        return isValid;
    }

    /**
     * Checks if the entered passwords match and if the user's current password is correct.
     *
     * @return True if passwords match, false otherwise.
     */
    private boolean pwdsMatch(){
        boolean areMatching = true;
        if(!pwdField.getText().toString().equals(mUser.getPassword())){
            pwdField.setError(wrongPwdStr);
            return false;
        }
        if(!newPwdField.getText().toString().equals(newPwd2Field.getText().toString())){
            areMatching = false;
            newPwd2Field.setError(pwdDMatchStr);
        }
        return areMatching;
    }
}