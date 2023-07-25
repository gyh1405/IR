package com.example.demo.controller;


import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;

@ControllerAdvice
public class CustomErrorController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(PATH)
    public ResponseEntity<String> handleError() {
        // You can customize the error message or redirect to an error page here
        String errorMessage = "Sorry, something went wrong. Please try again later.";
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public String getErrorPath() {
        return PATH;
    }
}
