package com.pricetracker.engine;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SelectorConfigTest {

    @Test
    void getReturnsSelectorForKnownSiteAndField() {
        String selector = SelectorConfig.get("amazon", "title");
        assertNotNull(selector);
        assertFalse(selector.isEmpty());
    }

    @Test
    void getReturnsEmptyForUnknownSite() {
        String selector = SelectorConfig.get("unknownSite", "container");
        assertEquals("", selector);
    }

    @Test
    void getReturnsEmptyForUnknownField() {
        String selector = SelectorConfig.get("amazon", "nonexistentField");
        assertEquals("", selector);
    }
}
