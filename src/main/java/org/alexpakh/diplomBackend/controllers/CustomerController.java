package org.alexpakh.diplomBackend.controllers;


import org.alexpakh.diplomBackend.entities.Customer;
import org.alexpakh.diplomBackend.services.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rent/customers")
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public Optional<Customer> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping
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
}
