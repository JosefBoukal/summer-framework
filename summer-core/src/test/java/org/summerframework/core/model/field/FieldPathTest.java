package org.summerframework.core.model.field;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FieldPathTest {

    @Test
    public void testBasics() throws Exception {
        FieldPath path;
        path = FieldPath.valueOf((String) null);
        assertNull(path);
        path = FieldPath.valueOf("");
        assertNull(path);

        path = FieldPath.valueOf("product");
        assertNull(path.parent());
        assertEquals("product", path.value());
        assertEquals("product", path.field());
        assertNull(path.parent());
        assertTrue(path.isDirect());

        path = FieldPath.valueOf("test.some.long[path]");
        assertEquals("test.some.long.path", path.value());
        assertEquals("path", path.field());
        assertEquals("test.some.long", path.parent().value());
        assertEquals("path", path.field());
        assertTrue(path.isDirect());

        path = FieldPath.valueOf("test.some.long[*]");
        assertEquals("test.some.long.*", path.value());
        assertEquals("test.some.long", path.parent().value());
        assertEquals("*", path.field());
        assertFalse(path.isDirect());

        path = FieldPath.valueOf("variants[*].prices.purchasePrice");
        assertEquals(4, path.length());
        assertEquals("variants", path.head().value());
        assertTrue("purchasePrice".equals(path.tail().value()));
        assertEquals(PathElement.ANY_FIELD, path.elements()[1]);

        path = FieldPath.valueOf("variants[0].prices.purchasePrice");
        assertEquals(4, path.length());
        assertEquals("variants", path.head().value());
        assertTrue("purchasePrice".equals(path.tail().value()));
        assertEquals(0, path.elements()[1].value());
        assertTrue(path.isDirect());
    }

}