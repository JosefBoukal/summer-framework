package org.summerframework.experimental.data.model.definition.provider.order;

import lombok.Data;
import org.summerframework.core.model.price.MonetaryAmount;

import java.time.LocalDateTime;

@Data
public class OrderPayment {
    private String paymentNumber;
    private LocalDateTime paymentTime;
    private MonetaryAmount amount;

    private String status;
    private String message;
}
