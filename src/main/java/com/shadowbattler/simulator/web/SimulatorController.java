package com.shadowbattler.simulator.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimulatorController {
    @GetMapping("/")
    public String hello() {
        return "hello world";
    }
}
