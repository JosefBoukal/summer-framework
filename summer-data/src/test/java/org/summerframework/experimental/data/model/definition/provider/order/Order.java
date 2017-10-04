package org.summerframework.experimental.data.model.definition.provider.order;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.summerframework.experimental.data.model.definition.provider.BasicEntity;

import java.util.List;
import java.util.Set;

/**
 * @author Josef Boukal
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Order extends BasicEntity {
    private String token;
    private String state;
    private List<OrderItem> items;
    private OrderCustomer customer;
    private OrderBilling billing;
    private OrderShipping shipping;
    private OrderPayment payment;
    private OrderSummary summary;
    private OrderComment[] comments;
    private Set<String> tags;
}
