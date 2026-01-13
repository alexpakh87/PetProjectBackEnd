package org.alexpakh.diplomBackend.controllers;


import org.alexpakh.diplomBackend.entities.Customer;
import org.alexpakh.diplomBackend.services.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin( origins={"http://localhost:4200", "https://app.swift.netcraze.pro"})
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public Optional<Customer> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping ("/all_customers")
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PostMapping("/add user")
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @DeleteMapping
    public String deleteAllCustomers() {
        customerService.deleteAllCustomers();
        return "deleteAllCustomers";
    }

    @DeleteMapping("/{id}")
    public void deleteCustomerById(@PathVariable Long id) {
        customerService.deleteCustomerById(id);
    }

    @PutMapping("/{id}")
    public Customer changeCustomer(@PathVariable Long id, @RequestBody Customer details) {
        return customerService.changeCustomer(id, details);
    }

    @GetMapping("/public/hello")
    public ResponseEntity<String> publicHello() {
        return ResponseEntity.ok("Hello from public API!");
    }

    @GetMapping("/secured/hello")
    public ResponseEntity<String> securedHello() {
        return ResponseEntity.ok("Hello from secured API!");
    }

    @GetMapping("/manager/users-list")
    @PreAuthorize("hasRole('MANAGER')")
    public List<Customer> getUsersList() {
        // Ваша логика
        return customerService.getAllCustomers();
    }
}
