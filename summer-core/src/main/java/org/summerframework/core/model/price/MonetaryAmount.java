package org.summerframework.core.model.price;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Josef Boukal
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MonetaryAmount {
    private String currency;
    private BigDecimal amount;
}
