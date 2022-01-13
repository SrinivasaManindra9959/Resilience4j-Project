package com.resilience4j.poc.controller;

import com.resilience4j.poc.model.Availability;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@RestController
@RequestMapping("/booking")
public class BookingController {

    private RestTemplate restTemplate = new RestTemplate();

    @PostMapping(value = "/order",consumes = {MediaType.APPLICATION_JSON_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE})
    @CircuitBreaker(name="bookingCircuitBreaker",fallbackMethod = "bookingOrderFallBack")
    public String bookingOrder(@RequestBody Availability availability){
        System.out.println("Inside thhe bookingOrder Method");
        ResponseEntity<Boolean> response = restTemplate.exchange(new RequestEntity(availability, HttpMethod.POST,
                URI.create("http://localhost:8000/availability/check")),Boolean.class);
        if(response.getBody().equals(Boolean.TRUE)){
            System.out.println("Order available and successfully purchased");
            return "Order available and successfully purchased";
        }
        System.out.println("Order Not available");
        return "Order Not available";
    }

    public String bookingOrderFallBack(Availability availability,Throwable t){
        return "Fallback method called due to error occured";
    }
}
