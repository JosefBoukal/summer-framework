package org.summerframework.experimental.data.model.definition.provider;

import org.springframework.core.annotation.Order;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * @author Josef Boukal
 */
public class BasicEntity {
    @Id
    private String id;

    @Order
    private LocalDateTime created;
    @Order
    private LocalDateTime updated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

}
