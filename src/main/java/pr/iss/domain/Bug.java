package pr.iss.domain;

import jakarta.persistence.*;

@Entity
public class Bug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "reported_by")
    private Tester reportedBy;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Tester getReportedBy() {
        return reportedBy;
    }
    public void setReportedBy(Tester reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
