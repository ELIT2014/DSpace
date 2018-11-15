package ua.edu.sumdu.essuir.utils;

import org.springframework.stereotype.Controller;
import ua.edu.sumdu.essuir.entity.*;
import ua.edu.sumdu.essuir.repository.MetadatavalueRepository;
import ua.edu.sumdu.essuir.repository.SpecialityRepository;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class SpecialityStatisticsService {
    @Resource
    private MetadatavalueRepository metadatavalueRepository;
    @Resource
    private SpecialityRepository specialityRepository;

    private Speciality findSpeciality(String code) {
        FacultyEntity defaultFacultyEntity = new FacultyEntity.Builder().withId(-1).withName("-").build();
        ChairEntity defaultChairEntity = new ChairEntity.Builder().withId(-1).withChairName("-").withFacultyEntityName(defaultFacultyEntity).build();
        Speciality defaultSpecialityEntity= new Speciality.Builder().withId(-1).withName(code).withChairEntity(defaultChairEntity).build();
        return Optional.ofNullable(specialityRepository.findByCode(code)).orElse(defaultSpecialityEntity);
    }

    private String extractSpecialityCode(String data) {
        Pattern pattern = Pattern.compile("(\\d{1}[.]\\d{6})");
        Matcher matcher = pattern.matcher(data);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private List<PaperDescription> getBachelousPapers() {
        List<Integer> bachelousPaperIds = metadatavalueRepository.findDistinctByTextValue("Bachelous paper")
                .stream()
                .map(Metadatavalue::getResourceId)
                .collect(Collectors.toList());

        List<Metadatavalue> metadatavaluesForBachelousPapers = metadatavalueRepository.findByResourceIdIn(bachelousPaperIds);
        Map<Integer, Map<Integer, List<Metadatavalue>>> bachelousPapersDescription = metadatavaluesForBachelousPapers.stream()
                .collect(Collectors.groupingBy(Metadatavalue::getResourceId, Collectors.groupingBy(Metadatavalue::getMetadataFieldId)));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        return bachelousPaperIds.stream()
                .filter(id -> bachelousPapersDescription.containsKey(id) && bachelousPapersDescription.get(id).containsKey(12) && bachelousPapersDescription.get(id).containsKey(18))
                .map(id -> new PaperDescription.Builder()
                        .withResourceId(id)
                        .withSpeciality(extractSpecialityCode(bachelousPapersDescription.get(id).get(18).get(0).getTextValue()))
                        .withAdded(LocalDate.parse(bachelousPapersDescription.get(id).get(12).get(0).getTextValue(), formatter))
                        .build())
                .filter(paper -> paper.getSpeciality() != null)
                .collect(Collectors.toList());
    }

    public Map<Speciality, Integer> getSpecialityStatistics(LocalDate from, LocalDate to) {
        return getBachelousPapers()
                .stream()
                .filter(paper -> paper.getAdded().isAfter(from) && paper.getAdded().isBefore(to))
                .collect(Collectors.groupingBy(PaperDescription::getSpeciality))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(item -> findSpeciality(item.getKey()), item -> item.getValue().size()));
    }
}
