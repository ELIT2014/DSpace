package org.dspace.eperson;

import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObjectServiceImpl;
import org.dspace.core.Context;
import org.dspace.eperson.dao.SpecialityDAO;
import org.dspace.eperson.service.SpecialityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class SpecialityServiceImpl  extends DSpaceObjectServiceImpl<Speciality> implements SpecialityService {
    @Autowired(required = true)
    private SpecialityDAO specialityDAO;

    @Override
    public Speciality find(Context context, UUID id) throws SQLException {
        return null;
    }

    @Override
    public void updateLastModified(Context context, Speciality dso) throws SQLException, AuthorizeException {

    }

    @Override
    public void delete(Context context, Speciality dso) throws SQLException, AuthorizeException, IOException {

    }

    @Override
    public int getSupportsTypeConstant() {
        return 0;
    }

    @Override
    public List<Speciality> findAll(Context context) throws SQLException {
        return specialityDAO.findAll(context);
    }

    @Override
    public Speciality findById(Context context, Integer id) throws SQLException {
        return specialityDAO.findById(context, id);
    }
}