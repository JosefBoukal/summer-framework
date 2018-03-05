package org.summerframework.experimental.data.model.definition.provider.order;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.summerframework.core.model.price.Price;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderBilling {
    private String type;
    private String title;
    private Price cost;
}
