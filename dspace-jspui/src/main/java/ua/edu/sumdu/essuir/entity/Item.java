package ua.edu.sumdu.essuir.entity;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Entity
public class Item {
    @Id
    @Column(name = "item_id")
    private Integer itemId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "submitter_id", referencedColumnName = "eperson_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private EPerson submitter;

    @Column(name = "in_archive")
    private Boolean inArchive;

    @Column(name = "owning_collection")
    private Integer owningCollection;

    @Column(name = "withdrawn")
    private Boolean withdrawn;

    @OneToMany(
            mappedBy = "item"
    )
    @LazyCollection(LazyCollectionOption.FALSE)
    @Where(clause = "metadata_field_id = 133 and place = 1 and resource_type_id = 2")
    private List<Metadatavalue> metadataFieldsForSpeciality = new ArrayList<>();

    @OneToMany(
            mappedBy = "item"
    )
    @LazyCollection(LazyCollectionOption.FALSE)
    @Where(clause = "metadata_field_id = 134 and place = 1 and resource_type_id = 2")
    private List<Metadatavalue> metadataFieldsForPresentationDate = new ArrayList<>();

    @OneToMany(
            mappedBy = "item"
    )
    @LazyCollection(LazyCollectionOption.FALSE)
    @Where(clause = "metadata_field_id = 25 and place = 1 and resource_type_id = 2")
    private List<Metadatavalue> metadataFieldsForLink = new ArrayList<>();

    @OneToMany(
            mappedBy = "item"
    )
    @LazyCollection(LazyCollectionOption.FALSE)
    @Where(clause = "metadata_field_id = 64 and place = 1 and resource_type_id = 2")
    private List<Metadatavalue> metadataFieldsForTitle = new ArrayList<>();

    @OneToMany(
            mappedBy = "item"
    )
    @LazyCollection(LazyCollectionOption.FALSE)
    @Where(clause = "metadata_field_id = 12 and place = 1 and resource_type_id = 2")
    private List<Metadatavalue> metadataFieldsForDateAvailable = new ArrayList<>();

    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @Column(name = "discoverable")
    private Boolean discoverable;

    public Item() {
    }

    public EPerson getSubmitter() {
        return submitter;
    }

    public Integer getItemId() {
        return itemId;
    }

    public Boolean getInArchive() {
        return inArchive;
    }

    public Integer getOwningCollection() {
        return owningCollection;
    }

    public Boolean getWithdrawn() {
        return withdrawn;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public Boolean getDiscoverable() {
        return discoverable;
    }

    private String getMetadataFieldValue(List<Metadatavalue> values) {
        return values.stream()
                .findAny()
                .map(Metadatavalue::getTextValue)
                .orElse("");
    }

    public String getSpecialityName() {
        return getMetadataFieldValue(metadataFieldsForSpeciality);
    }

    public String getPresentationDate() {
        return getMetadataFieldValue(metadataFieldsForPresentationDate);
    }

    public String getTitle() {
        return getMetadataFieldValue(metadataFieldsForTitle);
    }

    public String getLink() {
        return getMetadataFieldValue(metadataFieldsForLink);
    }

    public LocalDate getDateAvailable() {
        return LocalDateTime.parse(getMetadataFieldValue(metadataFieldsForDateAvailable), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")).toLocalDate();
    }

}
