package com.torshovlabs.quote.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/api/user")
@RestController
public class UserController {


    /**
     * Some instances of the app is relying on a .json at the end of its API call,
     * hence we need to keep this endpoint for backwards compatibility.
     */
    @RequestMapping(value = {"/list", "/list.json"}, method = GET)
    public void register() {



    }

}
