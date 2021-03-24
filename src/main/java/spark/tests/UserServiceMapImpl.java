package spark.tests;

import java.util.Collection;
import java.util.HashMap;

public class UserServiceMapImpl implements UserService {
    private HashMap<String, User> userMap;

    public UserServiceMapImpl() {
        userMap = new HashMap<>();
    }

    @Override
    public void addUser(User user) {
        userMap.put(user.getId(), user);
    }

    @Override
    public Collection<User> getUsers() {
        return userMap.values();
    }

    @Override
    public User getUser(String id) {
        return userMap.get(id);
    }

    @Override
    public User editUser(User user) throws UserException {
        try {
            if (user.getId() == null) throw new UserException("ID must be set");

            User userTemp = userMap.get(user.getId());

            if (userTemp == null) throw new UserException("No such User");

            if (user.getEmail() != null) userTemp.setEmail(user.getEmail());

            if (user.getFirstName() != null) userTemp.setFirstName(user.getFirstName());

            if (user.getLastName() != null) userTemp.setLastName(user.getLastName());

            if (user.getId() != null) userTemp.setId(user.getId());

            return userTemp;

        } catch (Exception ex) {
            throw new UserException(ex.getMessage());
        }
    }

    @Override
    public void deleteUser(String id) {
        userMap.remove(id);
    }

    @Override
    public boolean userExist(String id) {
        return userMap.containsKey(id);
    }
}
