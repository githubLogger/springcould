package com.log.serviceribbon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloControler {
    @Autowired
    HelloService helloService;
    @GetMapping(value = "/hi")
    public String hi(@RequestParam String name){
        System.out.println("hellocontrole.hi");
        return helloService.hiService(name);
    }

}
