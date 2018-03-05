package org.summerframework.experimental.data.model.definition.provider.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Unit {
    private BigDecimal quantity;
    private String code;

    @Override
    public String toString() {
        if (quantity == null) {
            return null;
        }
        return (code == null) ? quantity.toPlainString() : quantity.toPlainString() + "\u00A0" + code;
    }

}
