package pr.iss.repo;

import pr.iss.domain.User;

public interface UserRepository {
    User findByUsernameAndPassword(String username, String password);
}

