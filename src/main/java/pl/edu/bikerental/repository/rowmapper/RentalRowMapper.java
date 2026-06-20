package pl.edu.bikerental.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import pl.edu.bikerental.domain.Rental;
import pl.edu.bikerental.domain.enumeration.RentalStatus;

/**
 * Converter between {@link Row} to {@link Rental}, with proper type conversions.
 */
@Service
public class RentalRowMapper implements BiFunction<Row, String, Rental> {

    private final ColumnConverter converter;

    public RentalRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Rental} stored in the database.
     */
    @Override
    public Rental apply(Row row, String prefix) {
        Rental entity = new Rental();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setStartDate(converter.fromRow(row, prefix + "_start_date", LocalDate.class));
        entity.setEndDate(converter.fromRow(row, prefix + "_end_date", LocalDate.class));
        entity.setTotalPrice(converter.fromRow(row, prefix + "_total_price", Float.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", RentalStatus.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", Long.class));
        entity.setBikeId(converter.fromRow(row, prefix + "_bike_id", Long.class));
        return entity;
    }
}
