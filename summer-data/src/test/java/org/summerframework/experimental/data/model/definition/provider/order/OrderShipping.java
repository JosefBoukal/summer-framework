package org.summerframework.experimental.data.model.definition.provider.order;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.summerframework.core.model.price.Price;

/**
 * @author Josef Boukal
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderShipping {
    private String type;
    private String title;
    private Price cost;
}
