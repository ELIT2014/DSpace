package org.dspace.eperson.dao;

import org.dspace.eperson.ChairEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChairRepository extends JpaRepository<ChairEntity, Integer> {
}