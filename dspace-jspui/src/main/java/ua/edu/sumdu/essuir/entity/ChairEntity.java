package ua.edu.sumdu.essuir.entity;


import javax.persistence.*;

@Entity
@Table(name = "chair")
public class ChairEntity {
    @Id
    @Column(name = "chair_id")
    private Integer id;

    @Column(name = "chair_name")
    private String chairName;

    @OneToOne
    @JoinColumn(name = "faculty_id")
    private FacultyEntity facultyEntityName;

    private ChairEntity(Builder builder) {
        setId(builder.id);
        setChairName(builder.chairName);
        setFacultyEntityName(builder.facultyEntityName);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChairName() {
        return chairName;
    }

    public void setChairName(String chairName) {
        this.chairName = chairName;
    }

    public String getFacultyEntityName() {
        return facultyEntityName.getName();
    }
    public FacultyEntity getFacultyEntity() {
        return this.facultyEntityName;
    }

    public Integer getFacultyEntityId() {
        return facultyEntityName.getId();
    }

    public void setFacultyEntityName(FacultyEntity facultyEntityName) {
        this.facultyEntityName = facultyEntityName;
    }


    public static final class Builder {
        private Integer id;
        private String chairName;
        private FacultyEntity facultyEntityName;

        public Builder() {
        }

        public Builder(ChairEntity copy) {
            this.id = copy.getId();
            this.chairName = copy.getChairName();
            this.facultyEntityName = copy.getFacultyEntity();
        }

        public Builder withId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder withChairName(String chairName) {
            this.chairName = chairName;
            return this;
        }

        public Builder withFacultyEntityName(FacultyEntity facultyEntityName) {
            this.facultyEntityName = facultyEntityName;
            return this;
        }

        public ChairEntity build() {
            return new ChairEntity(this);
        }
    }
}
