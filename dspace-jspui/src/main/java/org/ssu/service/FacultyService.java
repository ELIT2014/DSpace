package org.ssu.service;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Service;
import org.ssu.entity.FacultyEntity;

import javax.annotation.Resource;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FacultyService {
    private static final org.ssu.entity.jooq.Faculty FACULTY = org.ssu.entity.jooq.Faculty.TABLE;
    @Resource
    private DSLContext dsl;

    public List<FacultyEntity> getFacultyList() {
        Function<Record, FacultyEntity> extractFacultyEntityInformation = (record) -> new FacultyEntity.Builder().withId(record.get(FACULTY.facultyId)).withName(record.get(FACULTY.facultyName)).build();
        return dsl.select(FACULTY.asterisk())
                .from(FACULTY)
                .fetch()
                .stream()
                .map(extractFacultyEntityInformation)
                .collect(Collectors.toList());

    }
}
