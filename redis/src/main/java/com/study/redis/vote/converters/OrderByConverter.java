package com.study.redis.vote.converters;

import com.study.redis.vote.schemas.OrderBy;
import org.springframework.core.convert.converter.Converter;

public class OrderByConverter implements Converter<String, OrderBy> {
    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     *
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    @Override
    public OrderBy convert(String source) {
        return OrderBy.valueOf(source.toUpperCase());
    }
}
