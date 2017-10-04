package org.summerframework.experimental.data.model.definition.provider.order;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.summerframework.experimental.data.model.definition.provider.Address;

/**
 * @author Josef Boukal
 */
@Data
public class OrderCustomer {
    @Id
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Address address;
}
