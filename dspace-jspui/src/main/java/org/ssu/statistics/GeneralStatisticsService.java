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
import java.util.stream.Collectors;

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


        Function<List<GeneralStatistics>, Integer> getDownloadCount = (data) -> data.stream().filter(item -> item.getMonth() == -1).mapToInt(GeneralStatistics::getDownloadsCount).sum();
        Function<List<GeneralStatistics>, Integer> getViewCount = (data) -> data.stream().filter(item -> item.getMonth() == -1).mapToInt(GeneralStatistics::getViewsCount).sum();
        Function<List<GeneralStatistics>, List<Integer>> getViewByMonth = (data) -> data.stream().sorted(Comparator.comparing(GeneralStatistics::getMonth)).filter(item -> item.getMonth() != -1).map(GeneralStatistics::getViewsCount).collect(Collectors.toList());
        Function<List<GeneralStatistics>, List<Integer>> getDownloadsByMonth = (data) -> data.stream().sorted(Comparator.comparing(GeneralStatistics::getMonth)).filter(item -> item.getMonth() != -1).map(GeneralStatistics::getDownloadsCount).collect(Collectors.toList());

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

    public Integer getCurrentMonthStatisticsViews(long totalViews) {
        Integer res = getCurrentYearStatisticsViews(totalViews);
        List<Integer> currentYearStatisticsViews = cacheListYearsStatistics.get(0).getYearViews();
        for (int i = 0; i < currentYearStatisticsViews.size(); i++) {
            res -= currentYearStatisticsViews.get(i);
        }
        return res;
    }

    public Integer getCurrentMonthStatisticsDownloads(long totalDownloads) {
        Integer res = getCurrentYearStatisticsDownloads(totalDownloads);
        List<Integer> currentYearStatisticsDownloads = cacheListYearsStatistics.get(0).getYearDownloads();
        for (int i = 0; i < currentYearStatisticsDownloads.size(); i++) {
            res -= currentYearStatisticsDownloads.get(i);
        }
        return res;
    }

    public Integer getCurrentYearStatisticsViews(long totalViews) {
        Integer res = Long.valueOf(totalViews).intValue();
        for (int i = 0; i < cacheListYearsStatistics.size(); i++) {
            res -= cacheListYearsStatistics.get(i).getTotalYearViews();
        }
        return res;
    }

    public Integer getCurrentYearStatisticsDownloads(long totalDownloads) {
        Integer res = Long.valueOf(totalDownloads).intValue();
        for (int i = 0; i < cacheListYearsStatistics.size(); i++) {
            if (cacheListYearsStatistics.get(i).getYear() != LocalDate.now().getYear())
                res -= cacheListYearsStatistics.get(i).getTotalYearDownloads();
        }
        return res;
    }

}