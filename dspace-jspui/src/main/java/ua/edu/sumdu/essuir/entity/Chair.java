package ua.edu.sumdu.essuir.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;
import org.json.simple.JSONArray;

import java.util.*;

public class Chair {
    @JsonProperty("name")
    private String chairName;
    @JsonProperty("data")
    private List<Person> staff;

    @JsonProperty("submission_count")
    public Integer getSubmissionCount() {
        Integer result = 0;
        for(Person person : staff) {
            result += person.getSubmissionCount();
        }
        return result;
    }
    public Chair(String chairName) {
        this.chairName = chairName;
        staff = new LinkedList<>();
    }

    public void addSubmission(String personName, Integer submissionCount) {
            staff.add(new Person(personName, submissionCount));
    }

}
