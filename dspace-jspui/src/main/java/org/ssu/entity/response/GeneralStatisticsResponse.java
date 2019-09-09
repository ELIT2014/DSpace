package org.ssu.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeneralStatisticsResponse {
    @JsonProperty("total_count")
    private Long totalCount;

    @JsonProperty("total_views")
    private Long totalViews;

    @JsonProperty("total_downloads")
    private Long totalDownloads;

    @JsonProperty("current_month_statistics_views")
    private Long currentMonthStatisticsViews;

    @JsonProperty("current_month_statistics_downloads")
    private Long currentMonthStatisticsDownloads;

    @JsonProperty("current_year_statistics_views")
    private Long currentYearStatisticsViews;

    @JsonProperty("current_year_statistics_downloads")
    private Long currentYearStatisticsDownloads;

    private GeneralStatisticsResponse(Builder builder) {
        totalCount = builder.totalCount;
        totalViews = builder.totalViews;
        totalDownloads = builder.totalDownloads;
        currentMonthStatisticsViews = builder.currentMonthStatisticsViews;
        currentMonthStatisticsDownloads = builder.currentMonthStatisticsDownloads;
        currentYearStatisticsViews = builder.currentYearStatisticsViews;
        currentYearStatisticsDownloads = builder.currentYearStatisticsDownloads;
    }


    public Long getTotalCount() {
        return totalCount;
    }

    public Long getTotalViews() {
        return totalViews;
    }

    public Long getTotalDownloads() {
        return totalDownloads;
    }

    public Long getCurrentMonthStatisticsViews() {
        return currentMonthStatisticsViews;
    }

    public Long getCurrentMonthStatisticsDownloads() {
        return currentMonthStatisticsDownloads;
    }

    public Long getCurrentYearStatisticsViews() {
        return currentYearStatisticsViews;
    }

    public Long getCurrentYearStatisticsDownloads() {
        return currentYearStatisticsDownloads;
    }


    public static final class Builder {
        private Long totalCount;
        private Long totalViews;
        private Long totalDownloads;
        private Long currentMonthStatisticsViews;
        private Long currentMonthStatisticsDownloads;
        private Long currentYearStatisticsViews;
        private Long currentYearStatisticsDownloads;

        public Builder() {
        }

        public Builder(GeneralStatisticsResponse copy) {
            this.totalCount = copy.getTotalCount();
            this.totalViews = copy.getTotalViews();
            this.totalDownloads = copy.getTotalDownloads();
            this.currentMonthStatisticsViews = copy.getCurrentMonthStatisticsViews();
            this.currentMonthStatisticsDownloads = copy.getCurrentMonthStatisticsDownloads();
            this.currentYearStatisticsViews = copy.getCurrentYearStatisticsViews();
            this.currentYearStatisticsDownloads = copy.getCurrentYearStatisticsDownloads();
        }

        public Builder withTotalCount(Long totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        public Builder withTotalViews(Long totalViews) {
            this.totalViews = totalViews;
            return this;
        }

        public Builder withTotalDownloads(Long totalDownloads) {
            this.totalDownloads = totalDownloads;
            return this;
        }

        public Builder withCurrentMonthStatisticsViews(Long currentMonthStatisticsViews) {
            this.currentMonthStatisticsViews = currentMonthStatisticsViews;
            return this;
        }

        public Builder withCurrentMonthStatisticsDownloads(Long currentMonthStatisticsDownloads) {
            this.currentMonthStatisticsDownloads = currentMonthStatisticsDownloads;
            return this;
        }

        public Builder withCurrentYearStatisticsViews(Long currentYearStatisticsViews) {
            this.currentYearStatisticsViews = currentYearStatisticsViews;
            return this;
        }

        public Builder withCurrentYearStatisticsDownloads(Long currentYearStatisticsDownloads) {
            this.currentYearStatisticsDownloads = currentYearStatisticsDownloads;
            return this;
        }

        public GeneralStatisticsResponse build() {
            return new GeneralStatisticsResponse(this);
        }
    }
}
