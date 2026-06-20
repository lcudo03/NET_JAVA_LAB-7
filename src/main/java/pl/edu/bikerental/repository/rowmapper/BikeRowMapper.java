package pl.edu.bikerental.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import pl.edu.bikerental.domain.Bike;
import pl.edu.bikerental.domain.enumeration.BikeType;

/**
 * Converter between {@link Row} to {@link Bike}, with proper type conversions.
 */
@Service
public class BikeRowMapper implements BiFunction<Row, String, Bike> {

    private final ColumnConverter converter;

    public BikeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Bike} stored in the database.
     */
    @Override
    public Bike apply(Row row, String prefix) {
        Bike entity = new Bike();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setSerialNumber(converter.fromRow(row, prefix + "_serial_number", String.class));
        entity.setBikeType(converter.fromRow(row, prefix + "_bike_type", BikeType.class));
        entity.setPricePerHour(converter.fromRow(row, prefix + "_price_per_hour", Float.class));
        entity.setAvailable(converter.fromRow(row, prefix + "_available", Boolean.class));
        entity.setProductionDate(converter.fromRow(row, prefix + "_production_date", LocalDate.class));
        return entity;
    }
}
