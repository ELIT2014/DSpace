package org.ssu.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.dspace.content.Item;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.core.Context;
import org.dspace.eperson.ChairEntity;
import org.dspace.eperson.FacultyEntity;
import org.dspace.eperson.Speciality;
import org.jooq.lambda.Seq;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.ssu.entity.SpecialityDetailedInfo;
import org.ssu.repository.MetadatavalueRepository;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SpecialityReportFetcher {
    private static Logger log = Logger.getLogger(SpecialityReportFetcher.class);
    transient private final org.dspace.content.service.ItemService itemService = ContentServiceFactory.getInstance().getItemService();
    @Resource
    private ItemService essuirItemService;
    @Resource
    private MetadatavalueRepository metadatavalueRepository;
    private BiPredicate<LocalDate, Pair<LocalDate, LocalDate>> isDateInRange = (date, range) -> date.isAfter(range.getLeft().minusDays(1)) && date.isBefore(range.getRight().plusDays(1));
    private BiFunction<Context, UUID, Optional<Item>> getItemByUUID = (context, uuid) -> {
        try {
            return Optional.ofNullable(itemService.find(context, uuid));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    };

    private Speciality extractSpecialityCode(String data) {
        FacultyEntity defaultFacultyEntity = new FacultyEntity.Builder().withId(-1).withName("-").build();
        ChairEntity defaultChairEntity = new ChairEntity.Builder().withId(-1).withChairName("-").withFacultyEntityName(defaultFacultyEntity).build();
        Speciality defaultSpecialityEntity = new Speciality.Builder().withId(-1).withName("-").withCode("-1").withChairEntity(defaultChairEntity).build();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(data);
            FacultyEntity.Builder facultyBuilder = new FacultyEntity.Builder(defaultFacultyEntity);
            if (jsonNode.has(0)) {
                facultyBuilder
                        .withId(jsonNode.get(0).get("code").asInt())
                        .withName(jsonNode.get(0).get("name").asText());
            }

            ChairEntity.Builder chairBuilder = new ChairEntity.Builder(defaultChairEntity)
                    .withFacultyEntityName(facultyBuilder.build());
            if (jsonNode.has(1)) {
                chairBuilder
                        .withId(jsonNode.get(1).get("code").asInt())
                        .withChairName(jsonNode.get(1).get("name").asText());
            }

            Speciality.Builder speciality = new Speciality.Builder(defaultSpecialityEntity)
                    .withChairEntity(chairBuilder.build());
            if (jsonNode.has(2)) {
                speciality.withName(jsonNode.get(2).get("name").asText())
                        .withCode(jsonNode.get(2).get("code").asText());
            } else {
                speciality.withName(chairBuilder.build().getName())
                        .withCode(chairBuilder.build().getId().toString());
            }
            return speciality.build();

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return defaultSpecialityEntity;
    }

    @Transactional
    public List<Speciality> getBachelorsPapersMetadata(Context context, LocalDate from, LocalDate to) throws IOException, SQLException {
        Function<String, Speciality> facultyEntityObjectMapper = (jsonData) -> {
            try {
                List<SpecialityDetailedInfo> specialityDetailedInfoList = new ObjectMapper().readValue(jsonData, new TypeReference<List<SpecialityDetailedInfo>>() {
                });
                if (specialityDetailedInfoList.size() == 3) {
                    FacultyEntity faculty = new FacultyEntity.Builder()
                            .withId(specialityDetailedInfoList.get(0).getCode())
                            .withName(specialityDetailedInfoList.get(0).getName())
                            .build();

                    ChairEntity chair = new ChairEntity.Builder()
                            .withFacultyEntityName(faculty)
                            .withChairName(specialityDetailedInfoList.get(1).getName())
                            .withId(specialityDetailedInfoList.get(1).getCode())
                            .build();

                    return new Speciality.Builder()
                            .withChairEntity(chair)
                            .withId(specialityDetailedInfoList.get(2).getCode())
                            .withName(specialityDetailedInfoList.get(2).getName())
                            .build();
                }
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        };
        Map<UUID, LocalDate> allDatesAvailable = essuirItemService.getAllDatesAvailable(context);
        return essuirItemService.fetchMastersAndBachelorsPapers()
                .entrySet()
                .stream()
                .filter(submission -> isDateInRange.test(allDatesAvailable.getOrDefault(submission.getKey(), LocalDate.MIN), Pair.of(from, to)))
                .map(Map.Entry::getValue)
                .map(facultyEntityObjectMapper)
                .collect(Collectors.toList());
    }

    public List<Pair<Speciality, Long>> getSpecialitySubmissionCountBetweenDates(Context context, LocalDate from, LocalDate to) throws IOException, SQLException {
        return Seq.seq(getBachelorsPapersMetadata(context, from, to))
                .filter(Objects::nonNull)
                .grouped(item -> item, Collectors.counting())
                .map(item -> Pair.of(item.v1(), item.v2()))
                .toList();
    }

    public List<Item> getBachelorsWithoutSpeciality(Context context) {
        Map<UUID, String> papersWithData = essuirItemService.fetchMastersAndBachelorsPapers();
        Map<UUID, String> itemTypes = essuirItemService.fetchItemType();

        return itemTypes.entrySet()
                .stream()
                .filter(item -> item.getValue().equals("Bachelous paper") || item.getValue().equals("Masters thesis"))
                .filter(item -> !papersWithData.containsKey(item.getKey()))
                .map(item -> getItemByUUID.apply(context, item.getKey()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Item> getItemsInSpeciality(Context context, String pattern, LocalDate from, LocalDate to) throws IOException, SQLException {
        String[] depositor = pattern.split("//");
        Predicate<String> isItemInNeededSpeciality = (speciality) -> Stream.of(depositor).allMatch(speciality::contains);
        return essuirItemService.fetchMastersAndBachelorsPapers()
                .keySet()
                .stream()
                .map(item -> getItemByUUID.apply(context, item))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(item -> isDateInRange.test(essuirItemService.getDateAvailableForItem(item), Pair.of(from, to)))
                //TODO: need some fixes
//                .filter(item -> isItemInNeededSpeciality.test(essuirItemService.getSpecialityForItem(context, item)))
                .collect(Collectors.toList());
    }
}
