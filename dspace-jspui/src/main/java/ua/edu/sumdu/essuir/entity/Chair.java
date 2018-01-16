package ua.edu.sumdu.essuir.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;
import org.json.simple.JSONArray;

import java.util.*;

public class Chair {
    @JsonProperty("chairName")
    private String chairName;
    @JsonProperty("staff")
    private List<Person> staff;

    public Chair(String chairName) {
        this.chairName = chairName;
        staff = new LinkedList<>();
    }

    public void addSubmission(String personName, Integer submissionCount) {
            staff.add(new Person(personName, submissionCount));
    }

}
