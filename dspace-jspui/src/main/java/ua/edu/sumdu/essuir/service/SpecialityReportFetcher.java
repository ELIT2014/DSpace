package ua.edu.sumdu.essuir.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.sumdu.essuir.entity.*;
import ua.edu.sumdu.essuir.repository.ItemRepository;
import ua.edu.sumdu.essuir.repository.MetadatavalueRepository;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SpecialityReportFetcher {
    private static Logger log = Logger.getLogger(SpecialityReportFetcher.class);

    @Resource
    private ItemRepository itemRepository;

    @Resource
    private MetadatavalueRepository metadatavalueRepository;

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
            log.error(ex.getStackTrace());
        }
        return defaultSpecialityEntity;
    }

    public Map<Speciality, Long> getSpecialityStatistics(LocalDate from, LocalDate to) {
        return getBachelorsPapers()
                .stream()
                .filter(paper -> paper.getAdded().isAfter(from) && paper.getAdded().isBefore(to))
                .collect(Collectors.groupingBy(PaperDescription::getSpeciality, Collectors.counting()));
    }

    public Map<String, Faculty> getSpecialitySubmissionCountBetweenDates(LocalDate from, LocalDate to) {
        List<PaperDescription> bachelousPapers = getBachelorsPapers();
        Map<String, Long> submissionInspeciality = bachelousPapers
                .stream()
                .filter(item -> item.getSpeciality() != null)
                .collect(Collectors.groupingBy(item -> item.getSpeciality().getName(), Collectors.counting()));

        Map<String, Faculty> result = new HashMap<>();

        for (PaperDescription paper : bachelousPapers) {
            if (paper.getAdded().isAfter(from) && paper.getAdded().isBefore(to)) {
                String faculty = paper.getSpeciality().getChairEntity().getFacultyEntityName();
                String chair = paper.getSpeciality().getChairEntity().getChairName();
                String speciality = paper.getSpeciality().getName();
                String specialityId = paper.getSpeciality().getName();
                if(!"-".equals(specialityId)) {
                    Long submissionCount = submissionInspeciality.get(specialityId);
                    result.putIfAbsent(faculty, new Faculty(faculty));
                    result.get(faculty).addSubmission(chair, speciality, submissionCount.intValue());
                }
            }
        }
        return result;
    }

    private List<Item> getBachelorsPapersMetadata() {
        return itemRepository.selectBachelousAndMastersPapersWithMetadataFields();
    }

    private List<PaperDescription> getBachelorsPapers() {
        List<Item> bachelorsPapersDescription = getBachelorsPapersMetadata();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy dd", Locale.US);
        return bachelorsPapersDescription.stream()
                .filter(item -> !"".equals(item.getSpecialityName()) && !"".equals(item.getPresentationDate()))
                .map(item -> new PaperDescription.Builder()
                        .withResourceId(item.getItemId())
                        .withSpeciality(extractSpecialityCode(item.getSpecialityName()))
                        .withAdded(LocalDate.parse(item.getPresentationDate() + " 01", formatter))
                        .build())
                .filter(paper -> paper.getSpeciality() != null)
                .collect(Collectors.toList());
    }

    public List<Item> getBachelorsWithoutSpeciality() {
        List<Item> items = getBachelorsPapersMetadata();
        return items.stream()
                .filter(item -> "".equals(item.getSpecialityName()) || "".equals(item.getPresentationDate()))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Item> getItemsInSpeciality(String pattern) {
        String[] depositor = pattern.split("//");
        List<Item> items = getBachelorsPapersMetadata();
        Predicate<String> isSpecialityNameContainsPattern = (specialityName) -> Stream.of(depositor).allMatch(specialityName::contains);
        return items.stream()
                .filter(item -> isSpecialityNameContainsPattern.test(item.getSpecialityName()))
                .collect(Collectors.toList());
    }
}

