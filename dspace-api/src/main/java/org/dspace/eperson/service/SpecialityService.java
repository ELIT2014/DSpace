package org.dspace.eperson.service;

import org.dspace.content.service.DSpaceObjectService;
import org.dspace.core.Context;
import org.dspace.eperson.Speciality;

import java.sql.SQLException;
import java.util.List;

public interface SpecialityService extends DSpaceObjectService<Speciality> {
    List<Speciality> findAll(Context context) throws SQLException;
    Speciality findById(Context context, Integer id) throws SQLException;
}
