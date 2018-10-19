package ua.edu.sumdu.essuir.entity;

import javax.persistence.*;

@Entity
public class Speciality {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @OneToOne
    @JoinColumn(name = "chair_id")
    private ChairEntity chairEntity;

    private Speciality(Builder builder) {
        id = builder.id;
        name = builder.name;
        chairEntity = builder.chairEntity;
    }


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ChairEntity getChairEntity() {
        return chairEntity;
    }


    public static final class Builder {
        private Integer id;
        private String name;
        private ChairEntity chairEntity;

        public Builder() {
        }

        public Builder(Speciality copy) {
            this.id = copy.getId();
            this.name = copy.getName();
            this.chairEntity = copy.getChairEntity();
        }

        public Builder withId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withChairEntity(ChairEntity chairEntity) {
            this.chairEntity = chairEntity;
            return this;
        }

        public Speciality build() {
            return new Speciality(this);
        }
    }
}
