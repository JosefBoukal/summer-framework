package org.summerframework.experimental.data.model.definition.provider.product;

import lombok.Data;
import org.summerframework.core.model.price.Price;

/**
 * @author Josef Boukal
 */
@Data
public class ProductItem {
    private Long id;
    private String code;
    private String title;
    private Price price;
    private Unit unit;
    private String image;
}
