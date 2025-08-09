package com.example.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GreetingServiceTest {

    private final GreetingService service = new GreetingService();

    @Test
    void greet_withName_returnsGreeting() {
        String result = service.greet("Alice");
        assertEquals("Hello, Alice!", result);
    }

    @Test
    void greet_withNull_returnsDefault() {
        String result = service.greet(null);
        assertEquals("Hello, World!", result);
    }

    @Test
    void greet_withBlank_returnsDefault() {
        String result = service.greet("   ");
        assertEquals("Hello, World!", result);
    }
}

