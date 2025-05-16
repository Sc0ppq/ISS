package pr.iss.service;
import pr.iss.domain.Bug;
import pr.iss.domain.Programmer;
import pr.iss.domain.Tester;
import pr.iss.domain.User;
import pr.iss.repo.BugRepository;
import pr.iss.repo.UserRepository;
import pr.iss.observer.BugObserver;

import java.util.ArrayList;
import java.util.List;

public class BugTrackingService {

    private final UserRepository userRepository;
    private final BugRepository bugRepository;
    private final List<BugObserver> observers = new ArrayList<>();

    public BugTrackingService(UserRepository userRepository, BugRepository bugRepository) {
        this.userRepository = userRepository;
        this.bugRepository = bugRepository;
        System.out.println("[DEBUG] Service instance created: " + this);
    }

    public void addObserver(BugObserver observer) {
        System.out.println("[DEBUG] Observer added: " + observer);
        observers.add(observer);
    }

    public void removeObserver(BugObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        System.out.println("[DEBUG] Notifying observers... Total: " + observers.size());
        for (BugObserver obs : observers) {
            System.out.println("[DEBUG] Sending to: " + obs);
            obs.bugsUpdated();
        }
    }

    public User login(String username, String password) throws Exception {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new Exception("Please fill out all the fields.");
        }

        User user = userRepository.findByUsernameAndPassword(username, password);

        if (user == null) {
            throw new Exception("Invalid username or password.");
        }

        return user;
    }

    public void reportBug(Tester tester, String name, String description) throws Exception {
        if (tester == null) {
            throw new Exception("You must be logged in as a Tester.");
        }
        if (name == null || name.isEmpty() || description == null || description.isEmpty()) {
            throw new Exception("Bug name and description must be filled.");
        }

        Bug bug = new Bug();
        bug.setName(name);
        bug.setDescription(description);
        bug.setReportedBy(tester);

        bugRepository.save(bug);
        notifyObservers();
    }

    public void deleteBug(Programmer programmer, Bug bug) throws Exception {
        if (programmer == null) {
            throw new Exception("You must be logged in as a Programmer.");
        }
        if (bug == null) {
            throw new Exception("No bug selected.");
        }

        bugRepository.delete(bug);
        notifyObservers();
    }

    public List<Bug> getAllBugs() {
        return bugRepository.findAll();
    }

    public List<Bug> searchBugsByName(String name) {
        return bugRepository.findByName(name);
    }
}
