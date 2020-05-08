package org.dspace.eperson.dao.impl;

import org.dspace.core.AbstractHibernateDSODAO;
import org.dspace.core.Context;
import org.dspace.eperson.FacultyEntity;
import org.dspace.eperson.Speciality;
import org.dspace.eperson.dao.SpecialityDAO;
import org.hibernate.Query;

import java.sql.SQLException;
import java.util.List;

public class SpecialityDAOImpl extends AbstractHibernateDSODAO<Speciality> implements SpecialityDAO {
    protected SpecialityDAOImpl() {
        super();
    }

    @Override
    public List<Speciality> findAll(Context context) throws SQLException {
        Query query = createQuery(context,
                "SELECT f FROM Speciality f ORDER BY f.name ASC");
        query.setCacheable(true);

        return list(query);
    }

    @Override
    public Speciality findById(Context context, Integer id) throws SQLException {
        Query query = createQuery(context, "select s from Speciality s where s.specialityId = :speciality_id");
        query.setParameter("speciality_id", id);
        query.setCacheable(true);

        return singleResult(query);
    }
}
