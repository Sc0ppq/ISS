package pr.iss.repo;

import pr.iss.domain.Bug;

import java.util.List;

public interface BugRepository {
    void save(Bug bug);
    void delete(Bug bug);
    List<Bug> findAll();
    List<Bug> findByName(String name);
}

