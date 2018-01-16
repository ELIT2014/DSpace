package ua.edu.sumdu.essuir.service;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.essuir.entity.EPerson;
import ua.edu.sumdu.essuir.entity.Faculty;
import ua.edu.sumdu.essuir.entity.Person;
import ua.edu.sumdu.essuir.entity.Submission;
import ua.edu.sumdu.essuir.repository.PersonRepository;

import javax.annotation.Resource;
import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    @Resource
    private PersonRepository personRepository;

    @Resource
    private DatabaseService databaseService;

    public Map<String, Faculty> getUsersSubmissionCountBetweenDates(LocalDate from, LocalDate to) {
        String query = String.format("select eperson.eperson_id, email, chair_name, faculty_name, count(metadatavalue.resource_id) as submits " +
                "from eperson " +
                "left join chair on eperson.chair_id = chair.chair_id " +
                "left join faculty on faculty.faculty_id = chair.faculty_id " +
                "left join item on item.submitter_id = eperson_id and in_archive " +
                "left join metadatavalue on metadatavalue.resource_id = item.item_id and metadata_field_id = 11 " +
                "                      and text_value between '%d-%02d-%02d' and '%d-%02d-%02d'" +
                "group by eperson.eperson_id, chair_name, faculty_name", from.getYear(), from.getMonthOfYear(), from.getDayOfMonth(), to.getYear(), to.getMonthOfYear(), to.getDayOfMonth());

        CachedRowSet result = databaseService.executeQuery(query);
        Map<String, Faculty> submissions = new HashMap<String, Faculty>();
        try {
            while (result.next()) {
                if (!"null".equals(result.getString("email"))) {
                    String faculty = result.getString("faculty_name") == null ? " " : result.getString("faculty_name");
                    String chair = result.getString("chair_name") == null ? " " : result.getString("chair_name");
                    String person = result.getString("email");
                    Integer submissionCount = Integer.parseInt(result.getString("submits"));
                    if(!submissions.containsKey(faculty)) {
                        submissions.put(faculty, new Faculty(faculty));
                    }
                    submissions.get(faculty).addSubmission(chair, person, submissionCount);
                }
            }
        } catch (SQLException ex) {

        }
        return submissions;
    }
}
