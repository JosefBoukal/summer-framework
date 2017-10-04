package org.summerframework.experimental.data.model.definition.provider.order;


import lombok.Data;
import org.summerframework.core.model.price.Price;
import org.summerframework.experimental.data.model.definition.provider.product.ProductItem;

import java.math.BigDecimal;

/**
 * @author Josef Boukal
 */
@Data
public class OrderItem {
    private BigDecimal quantity;
    private Price price;
    private ProductItem product;
}
