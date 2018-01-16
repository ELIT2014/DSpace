package ua.edu.sumdu.essuir.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Person {
    @JsonProperty("name")
    private String name;
    @JsonProperty("submissionCount")
    private Integer submissionCount;

    public Person(String name, Integer submissionCount) {
        this.name = name;
        this.submissionCount = submissionCount;
    }
}
