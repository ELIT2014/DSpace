package org.ssu.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.ssu.entity.GeneralStatistics;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeneralStatisticsRepository {
    private static final org.ssu.entity.jooq.Item ITEM = org.ssu.entity.jooq.Item.TABLE;
    private static final org.ssu.entity.jooq.Statistics STATISTICS = org.ssu.entity.jooq.Statistics.TABLE;
    private static final org.ssu.entity.jooq.GeneralStatistics GENERAL_STATISTICS = org.ssu.entity.jooq.GeneralStatistics.TABLE;
    private static final org.ssu.entity.jooq.Metadatavalue METADATAVALUE = org.ssu.entity.jooq.Metadatavalue.TABLE;
    private static final org.ssu.entity.jooq.Handle HANDLE = org.ssu.entity.jooq.Handle.TABLE;

    @Resource
    private DSLContext dsl;

    public List<GeneralStatistics> findAllYearsStatistics() {
        return dsl.select(GENERAL_STATISTICS.asterisk())
                .from(GENERAL_STATISTICS)
                .where(GENERAL_STATISTICS.month.eq(-1))
                .orderBy(GENERAL_STATISTICS.year.desc())
                .fetch()
                .stream()
                .map(item -> new GeneralStatistics.Builder()
                        .withYear(item.get(GENERAL_STATISTICS.year))
                        .withMonth(item.get(GENERAL_STATISTICS.month))
                        .withDownloadsCount(item.get(GENERAL_STATISTICS.downloadsCount))
                        .withViewsCount(item.get(GENERAL_STATISTICS.viewCount))
                        .build()
                ).collect(Collectors.toList());

    }

    public List<Integer> findAllMonthsViewsStatisticsByYear(Integer year) {
        return dsl.select(GENERAL_STATISTICS.viewCount)
                .from(GENERAL_STATISTICS)
                .where(GENERAL_STATISTICS.year.eq(year).and(GENERAL_STATISTICS.month.notEqual(-1)))
                .orderBy(GENERAL_STATISTICS.year)
                .fetch(GENERAL_STATISTICS.viewCount);
    }

    public List<Integer> findAllMonthsDownloadsStatisticsByYear(Integer year) {
        return dsl.select(GENERAL_STATISTICS.viewCount)
                .from(GENERAL_STATISTICS)
                .where(GENERAL_STATISTICS.year.eq(year).and(GENERAL_STATISTICS.month.notEqual(-1)))
                .orderBy(GENERAL_STATISTICS.month)
                .fetch(GENERAL_STATISTICS.viewCount);
    }
//    @Query("SELECT s FROM GeneralStatistics s WHERE s.month = -1 ORDER BY s.year DESC")
//    List<GeneralStatistics> findAllYearsStatistics();

//    @Query("SELECT s.viewsCount FROM GeneralStatistics s WHERE s.year = :year AND s.month <> -1 ORDER BY s.month")
//    List<Integer> findAllMonthsViewsStatisticsByYear(@Param("year") Integer year);

//    @Query("SELECT s.downloadsCount FROM GeneralStatistics s WHERE s.year = :year AND s.month <> -1 ORDER BY s.month")
//    List<Integer> findAllMonthsDownloadsStatisticsByYear(@Param("year") Integer year);

//    @Query("SELECT s FROM GeneralStatistics s WHERE s.year = :year AND s.month = :month")
//    GeneralStatistics findCurrentYearTotalStatistics(@Param("year") Integer year, @Param("month") Integer month);
}