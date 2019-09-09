package org.ssu.statistics;

import org.springframework.stereotype.Service;
import org.ssu.entity.GeneralStatistics;
import org.ssu.entity.YearStatistics;
import org.ssu.entity.response.GeneralStatisticsResponse;
import org.ssu.repository.GeneralStatisticsRepository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

@Service
public class GeneralStatisticsService {

    private List<YearStatistics> cacheListYearsStatistics = new ArrayList<>();

    @Resource
    private GeneralStatisticsRepository generalStatisticsRepository;

    @Resource
    private EssuirStatistics essuirStatistics;

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


    public GeneralStatisticsResponse collectGeneralStatistics() {
        StatisticsData statisticsData = essuirStatistics.getTotalStatistic();
        return new GeneralStatisticsResponse.Builder()
                .withTotalCount(statisticsData.getTotalCount())
                .withTotalViews(statisticsData.getTotalViews())
                .withTotalDownloads(statisticsData.getTotalDownloads())
                .withCurrentMonthStatisticsViews(getCurrentMonthStatisticsViews(statisticsData))
                .withCurrentMonthStatisticsDownloads(getCurrentMonthStatisticsDownloads(statisticsData))
                .withCurrentYearStatisticsDownloads(getCurrentYearStatisticsDownloads(statisticsData))
                .withCurrentYearStatisticsViews(getCurrentYearStatisticsViews(statisticsData))
                .build();
    }

    public List<YearStatistics> getListYearsStatistics() {
        return cacheListYearsStatistics;
    }

    private Long getCurrentMonthStatisticsViews(StatisticsData statisticsData) {
        return getCurrentYearStatisticsViews(statisticsData) - getMonthStatistics(YearStatistics::getYearViews);
    }

    private Long getCurrentMonthStatisticsDownloads(StatisticsData statisticsData) {
        return getCurrentYearStatisticsDownloads(statisticsData) - getMonthStatistics(YearStatistics::getYearDownloads);
    }

    private Long getCurrentYearStatisticsViews(StatisticsData statisticsData) {
        return statisticsData.getTotalViews() - getCurrentYearStatistics(YearStatistics::getTotalYearViews);
    }

    private Long getCurrentYearStatisticsDownloads(StatisticsData statisticsData) {
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