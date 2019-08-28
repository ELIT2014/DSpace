package org.ssu.entity;

public class ItemTypeResponse {
    private Long count;
    private String title;
    private String searchQuery;

    private ItemTypeResponse(Builder builder) {
        count = builder.count;
        title = builder.title;
        searchQuery = builder.title;
    }

    public Long getCount() {
        return count;
    }

    public String getTitle() {
        return title;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public static final class Builder {
        private Long count;
        private String title;

        public Builder() {
        }

        public Builder(ItemTypeResponse copy) {
            this.count = copy.getCount();
            this.title = copy.getTitle();
        }

        public Builder withCount(Long count) {
            this.count = count;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public ItemTypeResponse build() {
            return new ItemTypeResponse(this);
        }
    }
}
