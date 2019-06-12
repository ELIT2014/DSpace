package ua.edu.sumdu.essuir.entity.jooq;

import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

public class Metadatavalue extends TableImpl<Record> {

    public static final Metadatavalue TABLE = new Metadatavalue();

    public final TableField<Record, Integer> resourceId = createField("resource_id", SQLDataType.INTEGER);
    public final TableField<Record, Integer> place = createField("place", SQLDataType.INTEGER);
    public final TableField<Record, Integer> metadataFieldId = createField("metadata_field_id", SQLDataType.INTEGER);
    public final TableField<Record, Integer> resourceTypeId = createField("resource_type_id", SQLDataType.INTEGER);
    public final TableField<Record, String> value = createField("text_value", SQLDataType.VARCHAR);

    public Metadatavalue() {
        super("metadatavalue");
    }

    public Metadatavalue(Metadatavalue item, String alias) {
        super(alias, null, item);
    }

    @Override
    public Metadatavalue as(String alias) {
        return new Metadatavalue(this, alias);
    }
}
