package org.ssu.entity;

import java.util.List;

public class YearStatistics {

    private Integer year;
    private Long totalYearViews;
    private Long totalYearDownloads;
    private List<Long> yearViews;
    private List<Long> yearDownloads;
    private Integer currentMonth;

    private YearStatistics(Builder builder) {
        year = builder.year;
        totalYearViews = builder.totalYearViews;
        totalYearDownloads = builder.totalYearDownloads;
        yearViews = builder.yearViews;
        yearDownloads = builder.yearDownloads;
        currentMonth = builder.currentMonth;
    }

    public Integer getYear() {
        return year;
    }

    public Long getTotalYearViews() {
        return totalYearViews;
    }

    public Long getTotalYearDownloads() {
        return totalYearDownloads;
    }

    public List<Long> getYearViews() {
        return yearViews;
    }

    public List<Long> getYearDownloads() {
        return yearDownloads;
    }

    public Integer getCurrentMonth() {
        return currentMonth;
    }


    public static final class Builder {
        private Integer year;
        private Long totalYearViews;
        private Long totalYearDownloads;
        private List<Long> yearViews;
        private List<Long> yearDownloads;
        private Integer currentMonth;

        public Builder() {
        }

        public Builder(YearStatistics copy) {
            this.year = copy.getYear();
            this.totalYearViews = copy.getTotalYearViews();
            this.totalYearDownloads = copy.getTotalYearDownloads();
            this.yearViews = copy.getYearViews();
            this.yearDownloads = copy.getYearDownloads();
            this.currentMonth = copy.getCurrentMonth();
        }

        public Builder withYear(Integer year) {
            this.year = year;
            return this;
        }

        public Builder withTotalYearViews(Long totalYearViews) {
            this.totalYearViews = totalYearViews;
            return this;
        }

        public Builder withTotalYearDownloads(Long totalYearDownloads) {
            this.totalYearDownloads = totalYearDownloads;
            return this;
        }

        public Builder withYearViews(List<Long> yearViews) {
            this.yearViews = yearViews;
            while(this.yearViews.size() < 12)
                this.yearViews.add(0L);
            return this;
        }

        public Builder withYearDownloads(List<Long> yearDownloads) {
            this.yearDownloads = yearDownloads;
            while(this.yearDownloads.size() < 12)
                this.yearDownloads.add(0L);
            return this;
        }

        public Builder withCurrentMonth(Integer currentMonth) {
            this.currentMonth = currentMonth;
            return this;
        }

        public YearStatistics build() {
            return new YearStatistics(this);
        }
    }
}