package com.log.serviceribbon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 史上最简单的SpringCloud教程 | 第二篇: 服务消费者（rest+ribbon）(Finchley版本)
 * Ribbon是用来做负载均衡的，通过restTemplate.getForObject(“http://SERVICE-HI/hi?name=”+name,String.class)
 * 方法实现轮流调用SERVICE-HI服务的提供者。
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
@EnableHystrix          //开启Hystrix：断路器
public class ServiceRibbonApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceRibbonApplication.class, args);
    }


    @Bean
    @LoadBalanced
    RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
