package com.example.servlet;

import com.example.service.GreetingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "GreetingServlet", urlPatterns = {"/greet"})
public class GreetingServlet extends HttpServlet {

    private final GreetingService greetingService = new GreetingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String message = greetingService.greet(name);

        resp.setContentType("text/plain");
        try (PrintWriter out = resp.getWriter()) {
            out.println(message);
        }
    }
}

