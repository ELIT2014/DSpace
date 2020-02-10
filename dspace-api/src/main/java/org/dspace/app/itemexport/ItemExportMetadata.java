package org.dspace.app.itemexport;

public class ItemExportMetadata {
    private String title;
    private String handle;
    private String collection;
    private String submitterEmail;
    private String submitterFirstName;
    private String submitterLastName;
    private String chairName;
    private String facultyName;
    private String dateAvailable;
    private String type;

    private ItemExportMetadata(Builder builder) {
        title = builder.title;
        handle = builder.handle;
        collection = builder.collection;
        submitterEmail = builder.submitterEmail;
        submitterFirstName = builder.submitterFirstName;
        submitterLastName = builder.submitterLastName;
        chairName = builder.chairName;
        facultyName = builder.facultyName;
        dateAvailable = builder.dateAvailable;
        type = builder.type;
    }

    public String getTitle() {
        return title;
    }

    public String getHandle() {
        return handle;
    }

    public String getCollection() {
        return collection;
    }

    public String getSubmitterEmail() {
        return submitterEmail;
    }

    public String getSubmitterFirstName() {
        return submitterFirstName;
    }

    public String getSubmitterLastName() {
        return submitterLastName;
    }

    public String getChairName() {
        return chairName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public String getDateAvailable() {
        return dateAvailable;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s", title, handle, dateAvailable, type, collection, submitterEmail, submitterFirstName, submitterLastName, chairName, facultyName);
    }


    public static final class Builder {
        private String title;
        private String handle;
        private String collection;
        private String submitterEmail;
        private String submitterFirstName;
        private String submitterLastName;
        private String chairName;
        private String facultyName;
        private String dateAvailable;
        private String type;

        public Builder() {
        }

        public Builder(ItemExportMetadata copy) {
            this.title = copy.getTitle();
            this.handle = copy.getHandle();
            this.collection = copy.getCollection();
            this.submitterEmail = copy.getSubmitterEmail();
            this.submitterFirstName = copy.getSubmitterFirstName();
            this.submitterLastName = copy.getSubmitterLastName();
            this.chairName = copy.getChairName();
            this.facultyName = copy.getFacultyName();
            this.dateAvailable = copy.getDateAvailable();
            this.type = copy.getType();
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withHandle(String handle) {
            this.handle = handle;
            return this;
        }

        public Builder withCollection(String collection) {
            this.collection = collection;
            return this;
        }

        public Builder withSubmitterEmail(String submitterEmail) {
            this.submitterEmail = submitterEmail;
            return this;
        }

        public Builder withSubmitterFirstName(String submitterFirstName) {
            this.submitterFirstName = submitterFirstName;
            return this;
        }

        public Builder withSubmitterLastName(String submitterLastName) {
            this.submitterLastName = submitterLastName;
            return this;
        }

        public Builder withChairName(String chairName) {
            this.chairName = chairName;
            return this;
        }

        public Builder withFacultyName(String facultyName) {
            this.facultyName = facultyName;
            return this;
        }

        public Builder withDateAvailable(String dateAvailable) {
            this.dateAvailable = dateAvailable;
            return this;
        }

        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        public ItemExportMetadata build() {
            return new ItemExportMetadata(this);
        }
    }
}
