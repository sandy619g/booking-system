package com.doodle.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/booking")
public class BookingController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}
