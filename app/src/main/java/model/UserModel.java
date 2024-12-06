package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class for managing a collection of {@link User} objects.
 * Provides methods for registering new users and updating existing ones.
 */
public class UserModel implements Serializable {
    // -Singleton instance of the user list-
    private static List<User> instance = null;

    /**
     * Retrieves the singleton instance of the user list.
     * Initializes the list with a default admin user if not already created.
     *
     * @return the singleton list of users
     */
    public static List<User> getInstance() {
        if (instance == null) {
            instance = new ArrayList<>();
            register(new User("admin", "admin@a.com", "12345"));
        }
        return instance;
    }

    /**
     * Registers a new user in the singleton list.
     *
     * @param user the user to be added
     */
    public static void register(User user) {
        instance.add(user);
    }

    /**
     * Updates an existing user's information in the list.
     * Matches users by their unique ID and replaces the old user object.
     *
     * @param updatedUser the user object containing updated information
     */
    public static void updateUser(User updatedUser) {
        for (int i = 0; i < instance.size(); i++) {
            // -Checking if the user ID matches-
            if (instance.get(i).getId().equals(updatedUser.getId())) {
                instance.set(i, updatedUser); // -Replacing the old user with the updated one-
                return;
            }
        }
    }
}
