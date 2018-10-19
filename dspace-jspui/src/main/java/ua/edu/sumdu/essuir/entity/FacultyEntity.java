package ua.edu.sumdu.essuir.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "faculty")
public class FacultyEntity {
    @Id
    @Column(name = "faculty_id")
    private Integer id;

    @Column(name = "faculty_name")
    private String name;

    private FacultyEntity(Builder builder) {
        setId(builder.id);
        setName(builder.name);
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static final class Builder {
        private Integer id;
        private String name;

        public Builder() {
        }

        public Builder(FacultyEntity copy) {
            this.id = copy.getId();
            this.name = copy.getName();
        }

        public Builder withId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public FacultyEntity build() {
            return new FacultyEntity(this);
        }
    }
}
