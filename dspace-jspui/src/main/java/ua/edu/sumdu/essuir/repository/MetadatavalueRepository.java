package ua.edu.sumdu.essuir.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.edu.sumdu.essuir.entity.Metadatavalue;

import java.util.List;

@Repository
public interface MetadatavalueRepository extends JpaRepository<Metadatavalue, Integer> {

    List<Metadatavalue> findDistinctByTextValueContaining(String speciality);

    @Query("select distinct(resourceId) from Metadatavalue where textValue = 'Bachelous paper'")
    List<Integer> selectBachelousWorkIds();

    List<Metadatavalue> findDistinctByTextValue(String textValue);
    List<Metadatavalue> findByResourceIdIn(List<Integer> resourceIds);

    @Query("SELECT mv1 " +
            "FROM Metadatavalue as mv1 " +
            "LEFT JOIN Metadatavalue as mv2 ON mv1.resourceId = mv2.resourceId " +
            "LEFT JOIN Item as item on item.itemId = mv2.resourceId "+
            "WHERE item.inArchive = true AND mv2.textValue LIKE CONCAT('%', :text_value, '%')")
    List<Metadatavalue> selectItemMetadataByTextValue(@Param("text_value") String textValue);
}
