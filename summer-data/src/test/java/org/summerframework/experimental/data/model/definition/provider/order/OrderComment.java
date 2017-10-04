package org.summerframework.experimental.data.model.definition.provider.order;

import lombok.Data;

import java.time.ZonedDateTime;

/**
 * @author Josef Boukal
 */
@Data
public class OrderComment {
    private ZonedDateTime timestamp;
    private String message;
}
