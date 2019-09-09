package org.ssu.entity;

import java.io.Serializable;

public class GeneralStatistics implements Serializable {
    private Integer year;
    private Integer month;
    private Long viewsCount;
    private Long downloadsCount;

    private GeneralStatistics(Builder builder) {
        year = builder.year;
        month = builder.month;
        viewsCount = builder.viewsCount;
        downloadsCount = builder.downloadsCount;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public Long getViewsCount() {
        return viewsCount;
    }

    public Long getDownloadsCount() {
        return downloadsCount;
    }


    public static final class Builder {
        private Integer year;
        private Integer month;
        private Long viewsCount;
        private Long downloadsCount;

        public Builder() {
        }

        public Builder(GeneralStatistics copy) {
            this.year = copy.getYear();
            this.month = copy.getMonth();
            this.viewsCount = copy.getViewsCount();
            this.downloadsCount = copy.getDownloadsCount();
        }

        public Builder withYear(Integer year) {
            this.year = year;
            return this;
        }

        public Builder withMonth(Integer month) {
            this.month = month;
            return this;
        }

        public Builder withViewsCount(Long viewsCount) {
            this.viewsCount = viewsCount;
            return this;
        }

        public Builder withDownloadsCount(Long downloadsCount) {
            this.downloadsCount = downloadsCount;
            return this;
        }

        public GeneralStatistics build() {
            return new GeneralStatistics(this);
        }
    }
}