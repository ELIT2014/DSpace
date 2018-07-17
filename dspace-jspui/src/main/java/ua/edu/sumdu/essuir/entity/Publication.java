package ua.edu.sumdu.essuir.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Publication {
    @JsonProperty("title")
    private String title;
    @JsonProperty("citation")
    private String citation;
    @JsonProperty("authors")
    private String authors;

    private Publication(Builder builder) {
        title = builder.title;
        citation = builder.citation;
        authors = builder.authors;
    }

    public String getTitle() {
        return title;
    }

    public String getCitation() {
        return citation;
    }

    public String getAuthors() {
        return authors;
    }

    @Override
    public String toString() {
        return "Publication{" +
                "title='" + title + '\'' +
                ", citation='" + citation + '\'' +
                ", authors='" + authors + '\'' +
                '}';
    }

    public static final class Builder {
        private String title;
        private String citation;
        private String authors;

        public Builder() {
        }

        public Builder(Publication copy) {
            this.title = copy.getTitle();
            this.citation = copy.getCitation();
            this.authors = copy.getAuthors();
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withCitation(String citation) {
            this.citation = citation;
            return this;
        }

        public Builder withAuthors(String authors) {
            this.authors = authors;
            return this;
        }

        public Publication build() {
            return new Publication(this);
        }
    }
}
