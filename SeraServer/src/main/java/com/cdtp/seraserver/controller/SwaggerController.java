package com.cdtp.seraserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {

    @GetMapping("/swagger")
    public String home() {
        return "redirect:/swagger-ui.html";
    }


    @GetMapping("")
    public String monitorPage() {
        return "redirect:/Client.html";
    }
}
