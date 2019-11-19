package org.ssu.service;


import com.amazonaws.transform.MapEntry;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.content.Item;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.stereotype.Service;

import org.ssu.entity.ChairEntity;
import org.ssu.entity.EssuirEperson;
import org.ssu.entity.FacultyEntity;
import org.ssu.entity.Speciality;
import org.ssu.entity.jooq.Faculty;
import org.ssu.entity.response.DepositorDivision;
import org.ssu.entity.response.DepositorSimpleUnit;
import org.ssu.entity.response.ItemDepositorResponse;
import org.ssu.entity.response.ItemResponse;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class ReportService {
    @Resource
    private ItemService essuirItemService;
    @Resource
    private EpersonService epersonService;

    transient private final org.dspace.content.service.ItemService itemService = ContentServiceFactory.getInstance().getItemService();

    private BiPredicate<LocalDate, Pair<LocalDate, LocalDate>> isDateInRange = (date, range) -> date.isAfter(range.getLeft().minusDays(1)) && date.isBefore(range.getRight().plusDays(1));

    private List<ItemDepositorResponse> collectStatistics(Map<DepositorSimpleUnit, Long> data) {
        Function<Map.Entry<DepositorSimpleUnit, Long>, ItemDepositorResponse> countContributionForSpeciality = (speciality) -> new ItemDepositorResponse.Builder()
                .withName(speciality.getKey().getName())
                .withCount(speciality.getValue().intValue())
                .build();

        BiFunction<DepositorDivision, List<ItemDepositorResponse>, ItemDepositorResponse> createSomething = (depositor, children) -> new ItemDepositorResponse.Builder()
                .withName(depositor.getName())
                .withDepositors(children)
                .withCount(((Long) children.stream().map(ItemDepositorResponse::getCount).count()).intValue())
                .build();

        Function<Map<ChairEntity, List<ItemDepositorResponse>>, List<ItemDepositorResponse>> create = (it) -> it.entrySet()
                .stream()
                .map(t -> createSomething.apply(t.getKey(), t.getValue()))
                .collect(Collectors.toList());


        return Seq.seq(data.entrySet())
                .grouped(item -> item.getKey().getChairEntity().getFacultyEntity(),
                        Collectors.groupingBy(it -> it.getKey().getChairEntity(),
                                Collectors.mapping(countContributionForSpeciality::apply, Collectors.toList())))
                .map(item -> Pair.of(item.v1, create.apply(item.v2)))
                .map(item -> createSomething.apply(item.getKey(), item.getValue()))
                .collect(Collectors.toList());
    }

    public List<ItemDepositorResponse> getUsersSubmissionCountBetweenDates(Context context, LocalDate from, LocalDate to) throws SQLException, IOException {
        long start = System.currentTimeMillis();
        System.out.println("=======================================================================");

        ArrayList<Item> items1 = Lists.newArrayList(itemService.findAll(context));
        System.out.println("fetch data from database");
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();

        Map<UUID, LocalDate> allDatesAvailable = essuirItemService.getAllDatesAvailable(context);
        System.out.println("fetch available dates from database");
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();

        List<Item> items = items1
                .stream()
                .filter(item -> allDatesAvailable.containsKey(item.getID()) && isDateInRange.test(allDatesAvailable.get(item.getID()), Pair.of(from, to)))
                .collect(Collectors.toList());
        System.out.println("first collect");
        System.out.println(System.currentTimeMillis() - start);
        Map<DepositorSimpleUnit, Long> data = Seq.seq(items)
                .grouped(Item::getSubmitter, Collectors.counting())
                .collect(Collectors.toMap(submission -> epersonService.extendEpersonInformation(submission.v1), submission -> submission.v2));
        System.out.println("create map");
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        List<ItemDepositorResponse> res = collectStatistics(data);
        System.out.println("collecting and grouping");
        System.out.println(System.currentTimeMillis() - start);
        System.out.println("=======================================================================");
        System.out.println();
        System.out.println();
        return res;
    }

//    public List<Item> getUploadedItemsByFacultyName(String faculty, LocalDate from, LocalDate to) {
//        List<Item> items = databaseService.fetchItemsInArchive();
//
//        return items
//                .stream()
//                .filter(item -> isDateInRange.test(item.getDateAvailable(), Pair.of(from, to)))
//                .filter(item -> faculty.equals(item.getSubmitter().getChairEntity().getFacultyEntityName()))
//                .collect(Collectors.toList());
//    }
//
//    public List<Item> getUploadedItemsByChairName(String chair, LocalDate from, LocalDate to) {
//        List<Item> items = databaseService.fetchItemsInArchive();
//
//        return items
//                .stream()
//                .filter(item -> isDateInRange.test(item.getDateAvailable(), Pair.of(from, to)))
//                .filter(item -> chair.equals(item.getSubmitter().getChairEntity().getChairName()))
//                .collect(Collectors.toList());
//    }
//
//    public List<Item> getUploadedItemsByPersonEmail(String person, LocalDate from, LocalDate to) {
//        List<Item> items = databaseService.fetchItemsInArchive();
//
//        return items
//                .stream()
//                .filter(item -> isDateInRange.test(item.getDateAvailable(), Pair.of(from, to)))
//                .filter(item -> person.equals(item.getSubmitter().getEmail()))
//                .collect(Collectors.toList());
//    }
//
//    public List<Item> getItemsInSpeciality(String pattern, LocalDate from, LocalDate to) {
//        return specialityReportFetcher.getItemsInSpeciality(pattern, from, to);
//    }
//
//    public List<Item> getBacheoursWithoutSpeciality() {
//        return specialityReportFetcher.getBachelorsWithoutSpeciality();
//    }
//
//    public List<Faculty> getSpecialitySubmissionCountBetweenDates(LocalDate from, LocalDate to) {
//        return collectStatistics(specialityReportFetcher.getSpecialitySubmissionCountBetweenDates(from, to));
//    }
}