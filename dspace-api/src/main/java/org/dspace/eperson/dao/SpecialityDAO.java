package org.dspace.eperson.dao;

import org.dspace.content.dao.DSpaceObjectDAO;
import org.dspace.core.Context;
import org.dspace.eperson.FacultyEntity;
import org.dspace.eperson.Speciality;

import java.sql.SQLException;
import java.util.List;

public interface SpecialityDAO  extends DSpaceObjectDAO<Speciality> {
    List<Speciality> findAll(Context context) throws SQLException;
    Speciality findById(Context context, Integer id) throws SQLException;
}