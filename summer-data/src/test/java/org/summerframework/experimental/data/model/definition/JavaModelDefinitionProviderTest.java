package org.summerframework.experimental.data.model.definition;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
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

/**
 * @author Josef Boukal
 */
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

    private void assertsOnOrderDefinition(ModelDefinition<Order> definition) {
        FieldDefinition<?> field;
        TypeInformation<?> componentType;
        CollectionDefinition<?> collectionDefinition;
        FieldDefinition<?> componentDefinition;

        Assert.assertEquals(EntityDefinition.class, definition.getClass());
        EntityDefinition<Order, String> entityDefinition = (EntityDefinition<Order, String>) definition;
        FieldDefinition<String> idField = entityDefinition.getIdField();
        Assert.assertEquals("id", idField.getName());
        Assert.assertEquals(String.class, idField.getTypeInformation().getType());
        Assert.assertEquals("String", idField.getType());
        Assert.assertEquals(String.class, idField.getTypeInformation().getType());
        Assert.assertEquals(AccessRight.ALL, definition.getAccessRights());
        Assert.assertEquals("order", definition.getName());
        Assert.assertEquals("Order", definition.getType());
        Map<String, FieldDefinition<?>> fields = definition.getFields();

        // token field
        field = fields.get("token");
        Assert.assertEquals(String.class, field.getTypeInformation().getType());

        // items field
        FieldDefinition<?> items = fields.get("items");
        Assert.assertTrue(items instanceof CollectionDefinition);
        componentType = items.getTypeInformation().getComponentType();
        Assert.assertNotNull(componentType);
        Assert.assertEquals(OrderItem.class, componentType.getType());

        // customer entity field
        FieldDefinition<?> customer = fields.get("customer");
        Assert.assertEquals(EntityDefinition.class, customer.getClass());
        assertOnCustomer((EntityDefinition) customer);

        // summary field
        FieldDefinition<?> summary = fields.get("summary");
        assertOnOrderSummary((ModelDefinition<?>) summary);

        // shipping field
        FieldDefinition<?> shipping = fields.get("shipping");
        Assert.assertEquals(ModelDefinition.class, shipping.getClass());
        Assert.assertEquals(OrderShipping.class, shipping.getTypeInformation().getType());
        Assert.assertEquals("OrderShipping", shipping.getType());
        assertsOnShippingOrBilling((ModelDefinition<?>) shipping);

        // billing field
        FieldDefinition<?> billing = fields.get("billing");
        Assert.assertEquals(ModelDefinition.class, billing.getClass());
        Assert.assertEquals(OrderBilling.class, billing.getTypeInformation().getType());
        Assert.assertEquals("OrderBilling", billing.getType());
        assertsOnShippingOrBilling((ModelDefinition<?>) billing);

        // comments field
        FieldDefinition<?> comments = fields.get("comments");
        Assert.assertTrue(comments instanceof CollectionDefinition);
        componentType = comments.getTypeInformation().getComponentType();
        Assert.assertNotNull(componentType);
        Assert.assertEquals(OrderComment.class, componentType.getType());

        // tags field
        FieldDefinition<?> tags = fields.get("tags");
        Assert.assertTrue(tags instanceof CollectionDefinition);
        componentType = tags.getTypeInformation().getComponentType();
        Assert.assertNotNull(componentType);
        Assert.assertEquals(String.class, componentType.getType());
        collectionDefinition = (CollectionDefinition<?>) tags;
        componentDefinition = collectionDefinition.getComponentDefinition();
        Assert.assertEquals(String.class, componentDefinition.getTypeInformation().getType());
    }

    private void assertOnOrderSummary(ModelDefinition<?> summary) {
        Assert.assertEquals(OrderSummary.class, summary.getTypeInformation().getType());
        Assert.assertEquals("summary", summary.getName());
        Assert.assertEquals("OrderSummary", summary.getType());
        FieldDefinition<?> field;

        field = summary.getFields().get("totalQuantity");
        Assert.assertEquals(BigDecimal.class, field.getTypeInformation().getType());

        field = summary.getFields().get("totalPrice");
        assertOnPrice((ModelDefinition) field);

        field = summary.getFields().get("grandTotalPrice");
        assertOnPrice((ModelDefinition) field);

        field = summary.getFields().get("priceByVat");
        MapDefinition<?, ?> priceByVat = (MapDefinition<?, ?>) field;
        Assert.assertEquals(String.class, priceByVat.getKeyDefinition().getTypeInformation().getType());
        field = priceByVat.getValueDefinition();
        Assert.assertEquals(Price.class, field.getTypeInformation().getType());
        assertOnPrice((ModelDefinition<?>) field);
    }

    private void assertOnPrice(ModelDefinition<?> price) {
        FieldDefinition<?> field;

        Assert.assertEquals(Price.class, price.getTypeInformation().getType());

        field = price.getFields().get("currency");
        Assert.assertEquals(String.class, field.getTypeInformation().getType());

        field = price.getFields().get("amount");
        Assert.assertEquals(BigDecimal.class, field.getTypeInformation().getType());

        field = price.getFields().get("noVat");
        Assert.assertEquals(BigDecimal.class, field.getTypeInformation().getType());

        field = price.getFields().get("vat");
        Assert.assertEquals(BigDecimal.class, field.getTypeInformation().getType());

        field = price.getFields().get("tax");
        Assert.assertEquals(BigDecimal.class, field.getTypeInformation().getType());
    }

    private void assertOnCustomer(EntityDefinition<?, ?> customer) {
        Assert.assertEquals(OrderCustomer.class, customer.getTypeInformation().getType());
        Assert.assertEquals("OrderCustomer", customer.getType());
        FieldDefinition<?> idField = customer.getIdField();
        Assert.assertEquals(Long.class, idField.getTypeInformation().getType());
        FieldDefinition<?> fieldDefinition;

        fieldDefinition = customer.getFields().get("fullName");
        Assert.assertNotNull(fieldDefinition);
        Assert.assertEquals(String.class, fieldDefinition.getTypeInformation().getType());

        fieldDefinition = customer.getFields().get("email");
        Assert.assertNotNull(fieldDefinition);
        Assert.assertEquals(String.class, fieldDefinition.getTypeInformation().getType());

        fieldDefinition = customer.getFields().get("phone");
        Assert.assertNotNull(fieldDefinition);
        Assert.assertEquals(String.class, fieldDefinition.getTypeInformation().getType());

        fieldDefinition = customer.getFields().get("address");
        Assert.assertNotNull(fieldDefinition);
        Assert.assertEquals(ModelDefinition.class, fieldDefinition.getClass());
        Assert.assertEquals(Address.class, fieldDefinition.getTypeInformation().getType());

        assertOnAddress((ModelDefinition) fieldDefinition);
    }

    private void assertOnAddress(ModelDefinition<?> address) {
        FieldDefinition<?> fieldDefinition;

        Assert.assertEquals(Address.class, address.getTypeInformation().getType());
        Assert.assertEquals("Address", address.getType());

        fieldDefinition = address.getFields().get("street");
        Assert.assertNotNull(fieldDefinition);
        Assert.assertEquals(String.class, fieldDefinition.getTypeInformation().getType());

        fieldDefinition = address.getFields().get("city");
        Assert.assertNotNull(fieldDefinition);
        Assert.assertEquals(String.class, fieldDefinition.getTypeInformation().getType());

        fieldDefinition = address.getFields().get("zip");
        Assert.assertNotNull(fieldDefinition);
        Assert.assertEquals(String.class, fieldDefinition.getTypeInformation().getType());

        fieldDefinition = address.getFields().get("state");
        Assert.assertNotNull(fieldDefinition);
        Assert.assertEquals(String.class, fieldDefinition.getTypeInformation().getType());

        fieldDefinition = address.getFields().get("country");
        Assert.assertNotNull(fieldDefinition);
        Assert.assertEquals(String.class, fieldDefinition.getTypeInformation().getType());
    }

    private void assertsOnShippingOrBilling(ModelDefinition<?> definition) {
        Map<String, FieldDefinition<?>> fields = definition.getFields();
        FieldDefinition<?> field;

        field = fields.get("type");
        Assert.assertEquals(String.class, field.getTypeInformation().getType());

        field = fields.get("title");
        Assert.assertEquals(String.class, field.getTypeInformation().getType());

        field = fields.get("cost");
        Assert.assertEquals(Price.class, field.getTypeInformation().getType());
    }

}