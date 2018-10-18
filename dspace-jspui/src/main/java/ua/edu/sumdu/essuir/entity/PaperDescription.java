package ua.edu.sumdu.essuir.entity;

import java.time.LocalDateTime;

public class PaperDescription {
    private Integer resourceId;
    private String speciality;
    private LocalDateTime added;

    public Integer getResourceId() {
        return resourceId;
    }

    public String getSpeciality() {
        return speciality;
    }

    public LocalDateTime getAdded() {
        return added;
    }

    private PaperDescription(Builder builder) {
        resourceId = builder.resourceId;
        speciality = builder.speciality;
        added = builder.added;
    }


    public static final class Builder {
        private Integer resourceId;
        private String speciality;
        private LocalDateTime added;

        public Builder() {
        }

        public Builder(PaperDescription copy) {
            this.resourceId = copy.getResourceId();
            this.speciality = copy.getSpeciality();
            this.added = copy.getAdded();
        }

        public Builder withResourceId(Integer resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public Builder withSpeciality(String speciality) {
            this.speciality = speciality;
            return this;
        }

        public Builder withAdded(LocalDateTime added) {
            this.added = added;
            return this;
        }

        public PaperDescription build() {
            return new PaperDescription(this);
        }
    }
}
