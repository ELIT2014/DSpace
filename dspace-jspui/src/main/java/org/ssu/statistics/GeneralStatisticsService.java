package org.ssu.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssu.entity.GeneralStatistics;
import org.ssu.entity.YearStatistics;
import org.ssu.repository.GeneralStatisticsRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GeneralStatisticsService {

    private List<YearStatistics> cacheListYearsStatistics = new ArrayList<>();

    @Autowired
    private GeneralStatisticsRepository generalStatisticsRepository;

    @PostConstruct
    public void updateListYearsStatistics() {
        Map<Integer, List<GeneralStatistics>> collect = generalStatisticsRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(GeneralStatistics::getYear));


        Function<List<GeneralStatistics>, Long> getDownloadCount = (data) -> data.stream().filter(item -> item.getMonth() == -1).mapToLong(GeneralStatistics::getDownloadsCount).sum();
        Function<List<GeneralStatistics>, Long> getViewCount = (data) -> data.stream().filter(item -> item.getMonth() == -1).mapToLong(GeneralStatistics::getViewsCount).sum();
        Function<List<GeneralStatistics>, List<Long>> getViewByMonth = (data) -> data.stream().sorted(Comparator.comparing(GeneralStatistics::getMonth)).filter(item -> item.getMonth() != -1).map(GeneralStatistics::getViewsCount).collect(Collectors.toList());
        Function<List<GeneralStatistics>, List<Long>> getDownloadsByMonth = (data) -> data.stream().sorted(Comparator.comparing(GeneralStatistics::getMonth)).filter(item -> item.getMonth() != -1).map(GeneralStatistics::getDownloadsCount).collect(Collectors.toList());

        cacheListYearsStatistics = collect.entrySet()
                .stream()
                .map(item ->
                        new YearStatistics.Builder()
                                .withYearViews(getViewByMonth.apply(item.getValue()))
                                .withYearDownloads(getDownloadsByMonth.apply(item.getValue()))
                                .withTotalYearDownloads(getDownloadCount.apply(item.getValue()))
                                .withTotalYearViews(getViewCount.apply(item.getValue()))
                                .withYear(item.getKey())
                                .withCurrentMonth(item.getValue().size() < 12 ? item.getValue().size() : -1)
                                .build()
                )
                .sorted(Comparator.comparing(YearStatistics::getYear).reversed())
                .collect(Collectors.toList());
    }

    public List<YearStatistics> getListYearsStatistics() {
        return cacheListYearsStatistics;
    }

    public Long getCurrentMonthStatisticsViews(StatisticsData statisticsData) {
        return getCurrentYearStatisticsViews(statisticsData)- getMonthStatistics(YearStatistics::getYearViews);
    }

    public Long getCurrentMonthStatisticsDownloads(StatisticsData statisticsData) {
        return getCurrentYearStatisticsDownloads(statisticsData) - getMonthStatistics(YearStatistics::getYearDownloads);
    }

    public Long getCurrentYearStatisticsViews(StatisticsData statisticsData) {
        return statisticsData.getTotalViews() - getCurrentYearStatistics(YearStatistics::getTotalYearViews);
    }

    public Long getCurrentYearStatisticsDownloads(StatisticsData statisticsData) {
        return statisticsData.getTotalDownloads() - getCurrentYearStatistics(YearStatistics::getTotalYearDownloads);
    }

    private Long getMonthStatistics(Function<YearStatistics, List<Long>> dataTransform) {
        return cacheListYearsStatistics
                .stream()
                .filter(entry -> entry.getYear().equals(LocalDate.now().getYear()))
                .flatMapToLong(item -> dataTransform.apply(item).stream().mapToLong(t -> t))
                .sum();
    }

    private Long getCurrentYearStatistics(ToLongFunction<YearStatistics> mappingFunction) {
        return cacheListYearsStatistics
                .stream()
                .filter(entry -> !entry.getYear().equals(LocalDate.now().getYear()))
                .mapToLong(mappingFunction)
                .sum();
    }

}