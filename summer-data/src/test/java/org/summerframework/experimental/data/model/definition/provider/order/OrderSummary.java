package org.summerframework.experimental.data.model.definition.provider.order;

import lombok.Data;
import org.summerframework.core.model.price.Price;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class OrderSummary {
    private BigDecimal totalQuantity;
    private Price totalPrice;
    private Price grandTotalPrice;
    private Map<String, Price> priceByVat = new LinkedHashMap<>();
}
