package pl.edu.bikerental.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class BikeSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("serial_number", table, columnPrefix + "_serial_number"));
        columns.add(Column.aliased("bike_type", table, columnPrefix + "_bike_type"));
        columns.add(Column.aliased("price_per_hour", table, columnPrefix + "_price_per_hour"));
        columns.add(Column.aliased("available", table, columnPrefix + "_available"));
        columns.add(Column.aliased("production_date", table, columnPrefix + "_production_date"));

        return columns;
    }
}
