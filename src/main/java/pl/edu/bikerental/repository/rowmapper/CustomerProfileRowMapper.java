package pl.edu.bikerental.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import pl.edu.bikerental.domain.CustomerProfile;

/**
 * Converter between {@link Row} to {@link CustomerProfile}, with proper type conversions.
 */
@Service
public class CustomerProfileRowMapper implements BiFunction<Row, String, CustomerProfile> {

    private final ColumnConverter converter;

    public CustomerProfileRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link CustomerProfile} stored in the database.
     */
    @Override
    public CustomerProfile apply(Row row, String prefix) {
        CustomerProfile entity = new CustomerProfile();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setCity(converter.fromRow(row, prefix + "_city", String.class));
        entity.setLoyaltyPoints(converter.fromRow(row, prefix + "_loyalty_points", Integer.class));
        entity.setVerified(converter.fromRow(row, prefix + "_verified", Boolean.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", Long.class));
        return entity;
    }
}
