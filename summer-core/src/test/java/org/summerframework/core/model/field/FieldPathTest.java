package org.summerframework.core.model.field;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FieldPathTest {

    @Test
    public void shouldReturnNull_whenEmptyPathIsGiven() {
        FieldPath path;
        path = FieldPath.valueOf((String) null);
        assertNull(path);
        path = FieldPath.valueOf("");
        assertNull(path);
    }

    @Test
    public void shouldReturnSimplePath_whenSinglePathIsGiven() {
        FieldPath path = FieldPath.valueOf("product");
        assertNotNull(path);
        assertNull(path.parent());
        assertEquals("product", path.value());
        assertEquals("product", path.field());
        assertNull(path.parent());
        assertTrue(path.isDirect());
    }

    @Test
    public void shouldProcessComplexPath_whenPathWithMapIsGiven() {
        FieldPath path = FieldPath.valueOf("test.some.long[path]");
        assertNotNull(path);
        assertEquals("test.some.long.path", path.value());
        assertEquals("path", path.field());
        assertEquals("test.some.long", path.parent().value());
        assertEquals("path", path.field());
        assertTrue(path.isDirect());
    }

    @Test
    public void shouldReturnIndirectPath_whenWildcardPathIsGiven() {
        FieldPath path = FieldPath.valueOf("test.some.long[*]");
        assertNotNull(path);
        assertEquals("test.some.long.*", path.value());
        assertEquals("test.some.long", path.parent().value());
        assertEquals("*", path.field());
        assertFalse(path.isDirect());

        path = FieldPath.valueOf("variants[*].prices.purchasePrice");
        assertNotNull(path);
        assertEquals(4, path.length());
        assertEquals("variants", path.head().value());
        assertTrue("purchasePrice".equals(path.tail().value()));
        assertEquals(PathElement.ANY_FIELD, path.elements()[1]);
    }

    @Test
    public void shouldReturnDirectPath_whenArrayPathIsGiven() {
        FieldPath path = FieldPath.valueOf("variants[0].prices.purchasePrice");
        assertNotNull(path);
        assertEquals(4, path.length());
        assertEquals("variants", path.head().value());
        assertTrue("purchasePrice".equals(path.tail().value()));
        assertEquals(0, path.elements()[1].value());
        assertTrue(path.isDirect());

    }

}