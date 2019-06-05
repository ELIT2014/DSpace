package ua.edu.sumdu.essuir.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.sumdu.essuir.entity.*;
import ua.edu.sumdu.essuir.repository.ItemRepository;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SpecialityReportFetcher {
    private static Logger log = Logger.getLogger(SpecialityReportFetcher.class);

    @Resource
    private ItemRepository itemRepository;

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
                speciality.withName(chairBuilder.build().getChairName())
                        .withCode(chairBuilder.build().getId().toString());
            }
            return speciality.build();

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return defaultSpecialityEntity;
    }

    private List<Item> getBachelorsPapersMetadata() {
        return itemRepository.selectBachelousAndMastersPapersWithMetadataFields();
    }

    private boolean isSpecialityNameAndPresentationDatePresented(Item item) {
        return "".equals(item.getSpecialityName()) && "".equals(item.getPresentationDate());
    }

    public List<Faculty> getSpecialitySubmissionCountBetweenDates(LocalDate from, LocalDate to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy dd", Locale.US);
        Predicate<String> isDateInRange = (date) -> {
            LocalDate localDate = LocalDate.parse(date + " 01", formatter);
            return localDate.isAfter(from) && localDate.isBefore(to);
        };

        Map<Speciality, Long> specialityStatistics = getBachelorsPapersMetadata()
                .stream()
                .filter(this::isSpecialityNameAndPresentationDatePresented)
                .filter(item -> isDateInRange.test(item.getPresentationDate()))
                .collect(Collectors.groupingBy(Item::getSpecialityName, Collectors.counting()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(item -> extractSpecialityCode(item.getKey()), Map.Entry::getValue));

        Map<String, Faculty> result = new HashMap<>();
        for (Map.Entry<Speciality, Long> submission : specialityStatistics.entrySet()) {
            String facultyName = submission.getKey().getChairEntity().getFacultyEntityName();
            String chairName = submission.getKey().getChairEntity().getChairName();
            String specialityName = submission.getKey().getName();
            result.putIfAbsent(facultyName, new Faculty(facultyName));
            result.get(facultyName).addSubmission(chairName, specialityName, submission.getValue().intValue());
        }

        return new ArrayList<>(result.values());
    }

    public List<Item> getBachelorsWithoutSpeciality() {
        List<Item> items = getBachelorsPapersMetadata();
        return items.stream()
                .filter(item -> !isSpecialityNameAndPresentationDatePresented(item))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Item> getItemsInSpeciality(String pattern) {
        String[] depositor = pattern.split("//");
        List<Item> items = getBachelorsPapersMetadata();
        Predicate<String> isSpecialityNameContainsPattern = (specialityName) -> Stream.of(depositor).allMatch(specialityName::contains);
        return items.stream()
                .filter(this::isSpecialityNameAndPresentationDatePresented)
                .filter(item -> isSpecialityNameContainsPattern.test(item.getSpecialityName()))
                .collect(Collectors.toList());
    }
}

