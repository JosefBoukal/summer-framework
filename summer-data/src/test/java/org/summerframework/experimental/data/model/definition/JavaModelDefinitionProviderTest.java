package org.summerframework.experimental.data.model.definition;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.data.util.TypeInformation;
import org.summerframework.core.model.AccessRight;
import org.summerframework.core.model.price.Price;
import org.summerframework.experimental.data.model.definition.provider.Address;
import org.summerframework.experimental.data.model.definition.provider.order.Order;
import org.summerframework.experimental.data.model.definition.provider.order.OrderBilling;
import org.summerframework.experimental.data.model.definition.provider.order.OrderComment;
import org.summerframework.experimental.data.model.definition.provider.order.OrderCustomer;
import org.summerframework.experimental.data.model.definition.provider.order.OrderItem;
import org.summerframework.experimental.data.model.definition.provider.order.OrderShipping;
import org.summerframework.experimental.data.model.definition.provider.order.OrderSummary;
import org.summerframework.util.DurationUtils;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Slf4j
public class JavaModelDefinitionProviderTest {

    @Test
    public void testGetModelDefinition() throws Exception {
        for (int i = 0; i < 3; i++) {
            JavaTypeDefinitionProvider provider = new JavaTypeDefinitionProvider();
            long time = System.currentTimeMillis();
            ModelDefinition<Order> definition = provider.getModelDefinition(Order.class);
            log.info("It took " + DurationUtils.milliDuration(System.currentTimeMillis() - time) + " to get " + definition);
            assertsOnOrderDefinition(definition);
        }
    }

    @Test
    public void testGetEntityDefinition() throws Exception {
        JavaTypeDefinitionProvider provider = new JavaTypeDefinitionProvider();
        long time = System.currentTimeMillis();
        EntityDefinition<Order, String> definition = provider.getEntityDefinition(Order.class, String.class);
        log.info("It took " + DurationUtils.milliDuration(System.currentTimeMillis() - time) + " to get " + definition);

        // assertions
        assertsOnOrderDefinition(definition);
    }

    @SuppressWarnings("unchecked")
    private void assertsOnOrderDefinition(ModelDefinition<Order> definition) {
        FieldDefinition<?> field;
        TypeInformation<?> componentType;
        CollectionDefinition<?> collectionDefinition;
        FieldDefinition<?> componentDefinition;

        assertEquals(EntityDefinition.class, definition.getClass());
        EntityDefinition<Order, String> entityDefinition = (EntityDefinition<Order, String>) definition;
        FieldDefinition<String> idField = entityDefinition.getIdField();
        assertEquals("id", idField.getName());
        assertEquals(String.class, idField.getTypeInformation().getType());
        assertEquals("String", idField.getType());
        assertEquals(String.class, idField.getTypeInformation().getType());
        assertEquals(AccessRight.ALL, definition.getAccessRights());
        assertEquals("order", definition.getName());
        assertEquals("Order", definition.getType());
        Map<String, FieldDefinition<?>> fields = definition.getFields();

        // token field
        field = fields.get("token");
        assertEquals(String.class, field.getTypeInformation().getType());

        // items field
        FieldDefinition<?> items = fields.get("items");
        assertTrue(items instanceof CollectionDefinition);
        componentType = items.getTypeInformation().getComponentType();
        assertNotNull(componentType);
        assertEquals(OrderItem.class, componentType.getType());

        // customer entity field
        FieldDefinition<?> customer = fields.get("customer");
        assertEquals(EntityDefinition.class, customer.getClass());
        assertOnCustomer((EntityDefinition) customer);

        // summary field
        FieldDefinition<?> summary = fields.get("summary");
        assertOnOrderSummary((ModelDefinition<?>) summary);

        // shipping field
        FieldDefinition<?> shipping = fields.get("shipping");
        assertEquals(ModelDefinition.class, shipping.getClass());
        assertEquals(OrderShipping.class, shipping.getTypeInformation().getType());
        assertEquals("OrderShipping", shipping.getType());
        assertsOnShippingOrBilling((ModelDefinition<?>) shipping);

        // billing field
        FieldDefinition<?> billing = fields.get("billing");
        assertEquals(ModelDefinition.class, billing.getClass());
        assertEquals(OrderBilling.class, billing.getTypeInformation().getType());
        assertEquals("OrderBilling", billing.getType());
        assertsOnShippingOrBilling((ModelDefinition<?>) billing);

        // comments field
        FieldDefinition<?> comments = fields.get("comments");
        assertTrue(comments instanceof CollectionDefinition);
        componentType = comments.getTypeInformation().getComponentType();
        assertNotNull(componentType);
        assertEquals(OrderComment.class, componentType.getType());

        // tags field
        FieldDefinition<?> tags = fields.get("tags");
        assertTrue(tags instanceof CollectionDefinition);
        componentType = tags.getTypeInformation().getComponentType();
        assertNotNull(componentType);
        assertEquals(String.class, componentType.getType());
        collectionDefinition = (CollectionDefinition<?>) tags;
        componentDefinition = collectionDefinition.getComponentDefinition();
        assertEquals(String.class, componentDefinition.getTypeInformation().getType());
    }

    @SuppressWarnings("unchecked")
    private void assertOnOrderSummary(ModelDefinition<?> summary) {
        assertEquals(OrderSummary.class, summary.getTypeInformation().getType());
        assertEquals("summary", summary.getName());
        assertEquals("OrderSummary", summary.getType());
        FieldDefinition<?> field;

        field = summary.getFields().get("totalQuantity");
        assertEquals(BigDecimal.class, field.getTypeInformation().getType());

        field = summary.getFields().get("totalPrice");
        assertOnPrice((ModelDefinition) field);

        field = summary.getFields().get("grandTotalPrice");
        assertOnPrice((ModelDefinition) field);

        field = summary.getFields().get("priceByVat");
        MapDefinition<?, ?> priceByVat = (MapDefinition<?, ?>) field;
        assertEquals(String.class, priceByVat.getKeyDefinition().getTypeInformation().getType());
        field = priceByVat.getValueDefinition();
        assertEquals(Price.class, field.getTypeInformation().getType());
        assertOnPrice((ModelDefinition<?>) field);
    }

    private void assertOnPrice(ModelDefinition<?> price) {
        FieldDefinition<?> field;

        assertEquals(Price.class, price.getTypeInformation().getType());

        field = price.getFields().get("currency");
        assertEquals(String.class, field.getTypeInformation().getType());

        field = price.getFields().get("amount");
        assertEquals(BigDecimal.class, field.getTypeInformation().getType());

        field = price.getFields().get("noVat");
        assertEquals(BigDecimal.class, field.getTypeInformation().getType());

        field = price.getFields().get("vat");
        assertEquals(BigDecimal.class, field.getTypeInformation().getType());

        field = price.getFields().get("tax");
        assertEquals(BigDecimal.class, field.getTypeInformation().getType());
    }

    private void assertOnCustomer(EntityDefinition<?, ?> customer) {
        assertEquals(OrderCustomer.class, customer.getTypeInformation().getType());
        assertEquals("OrderCustomer", customer.getType());
        FieldDefinition<?> idField = customer.getIdField();
        assertEquals(Long.class, idField.getTypeInformation().getType());
        FieldDefinition<?> fieldDefinition;

        fieldDefinition = customer.getFields().get("fullName");
        assertNotNull(fieldDefinition);
        assertEquals(String.class, fieldDefinition.getTypeInformation().getType());

        fieldDefinition = customer.getFields().get("email");
        assertNotNull(fieldDefinition);
        assertEquals(String.class, fieldDefinition.getTypeInformation().getType());

        fieldDefinition = customer.getFields().get("phone");
        assertNotNull(fieldDefinition);
        assertEquals(String.class, fieldDefinition.getTypeInformation().getType());

        fieldDefinition = customer.getFields().get("address");
        assertNotNull(fieldDefinition);
        assertEquals(ModelDefinition.class, fieldDefinition.getClass());
        assertEquals(Address.class, fieldDefinition.getTypeInformation().getType());

        assertOnAddress((ModelDefinition) fieldDefinition);
    }

    private void assertOnAddress(ModelDefinition<?> address) {
        FieldDefinition<?> fieldDefinition;

        assertEquals(Address.class, address.getTypeInformation().getType());
        assertEquals("Address", address.getType());

        fieldDefinition = address.getFields().get("street");
        assertNotNull(fieldDefinition);
        assertEquals(String.class, fieldDefinition.getTypeInformation().getType());

        fieldDefinition = address.getFields().get("city");
        assertNotNull(fieldDefinition);
        assertEquals(String.class, fieldDefinition.getTypeInformation().getType());

        fieldDefinition = address.getFields().get("zip");
        assertNotNull(fieldDefinition);
        assertEquals(String.class, fieldDefinition.getTypeInformation().getType());

        fieldDefinition = address.getFields().get("state");
        assertNotNull(fieldDefinition);
        assertEquals(String.class, fieldDefinition.getTypeInformation().getType());

        fieldDefinition = address.getFields().get("country");
        assertNotNull(fieldDefinition);
        assertEquals(String.class, fieldDefinition.getTypeInformation().getType());
    }

    private void assertsOnShippingOrBilling(ModelDefinition<?> definition) {
        Map<String, FieldDefinition<?>> fields = definition.getFields();
        FieldDefinition<?> field;

        field = fields.get("type");
        assertEquals(String.class, field.getTypeInformation().getType());

        field = fields.get("title");
        assertEquals(String.class, field.getTypeInformation().getType());

        field = fields.get("cost");
        assertEquals(Price.class, field.getTypeInformation().getType());
    }

}