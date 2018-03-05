package org.summerframework.experimental.data.model.definition.provider.product;

import lombok.Data;
import org.summerframework.core.lang.Localized;
import org.summerframework.core.model.price.Price;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class Product {
    private Long id;
    @Localized
    private String title;
    @Localized
    private String description;
    @Localized
    private String perex;

    private Price price;
    private Map<String, Price> prices;
    private LocalDateTime created;
    private LocalDateTime updated;
}
