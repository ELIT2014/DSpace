package ua.edu.sumdu.essuir.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class Faculty {
    @JsonProperty("facultyName")
    private String facultyName;
    @JsonProperty("chairs")
    private HashMap<String, Chair> chairs;

    public Faculty(String facultyName) {
        this.facultyName = facultyName;
        chairs = new HashMap<>();
    }

    public void addSubmission(String chair, String person, Integer submissionCount) {
        if(!chairs.containsKey(chair)) {
            chairs.put(chair, new Chair(chair));
        }
        chairs.get(chair).addSubmission(person, submissionCount);
    }
}
