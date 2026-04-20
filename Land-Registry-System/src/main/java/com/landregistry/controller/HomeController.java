package com.landregistry.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({
            "/land-registry-blockchain/static/index.html",
            "/land-registry-blockchain_00/land-registry-blockchain/static/index.html"
    })
    public String home() {
        return "redirect:/";
    }
}
