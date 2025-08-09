package com.example.service;

public class GreetingService {

    /**
     * Return greeting message. If name is null or blank, returns default greeting.
     */
    public String greet(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Hello, World!";
        }
        return "Hello, " + name.trim() + "!";
    }
}

