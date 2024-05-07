package org.alexpakh.diplomBackend.services;

import org.alexpakh.diplomBackend.entities.Customer;
import org.alexpakh.diplomBackend.repositoryies.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {


    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteAllCustomers() {
        customerRepository.deleteAll();
    }

    public void deleteCustomerById(Long id) {
        customerRepository.deleteById(id);
    }

    public Customer changeCustomer(Long id, Customer details) {
        Optional<Customer> customer = customerRepository.findById(id);

        if (customer.isPresent()) {
            Customer existingCustomer = customer.get();

            existingCustomer.setFirstName(details.getFirstName());
            existingCustomer.setSecondName(details.getSecondName());
            existingCustomer.setLogin(details.getLogin());
            existingCustomer.setPassword(details.getPassword());
            existingCustomer.setTelephoneNumber(details.getTelephoneNumber());
            existingCustomer.setEmail(details.getEmail());

            return customerRepository.save(existingCustomer);
        }
        return null;
    }
}
