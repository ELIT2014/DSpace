package org.ssu.entity.jooq;

import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

public class GeneralStatistics extends TableImpl<Record> {
    public static final GeneralStatistics TABLE = new GeneralStatistics();

    public final TableField<Record, Integer> year = createField("year", SQLDataType.INTEGER);
    public final TableField<Record, Integer> month = createField("month", SQLDataType.INTEGER);
    public final TableField<Record, Integer> viewCount = createField("count_views", SQLDataType.INTEGER);
    public final TableField<Record, Integer> downloadsCount = createField("count_downloads", SQLDataType.INTEGER);


    public GeneralStatistics() {
        super("general_statistics");
    }

    public GeneralStatistics(GeneralStatistics statistics, String alias) {
        super(alias, null, statistics);
    }

    @Override
    public GeneralStatistics as(String alias) {
        return new GeneralStatistics(this, alias);
    }
}
