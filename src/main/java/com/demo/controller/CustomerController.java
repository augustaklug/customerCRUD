package com.demo.controller;

import com.demo.model.Customer;
import com.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CustomerController {

    @Autowired
    CustomerRepository customerRepository;

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers(@RequestParam(required = false) String firstName) {
        try {
            List<Customer> customers = new ArrayList<>();

            if (firstName == null) customers.addAll(customerRepository.findAll());
            else customers.addAll(customerRepository.findByFirstName(firstName));

            if (customers.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(customers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable("id") long id) {
        Optional<Customer> customerData = customerRepository.findById(id);

        return customerData.map(customer -> new ResponseEntity<>(customer, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/customers")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        try {
            Optional<Customer> doesCustomerExist = customerRepository.findByEmail(customer.getEmail());
            if (doesCustomerExist.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            Customer savedCustomer = customerRepository.save(customer);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable("id") long id, @RequestBody Customer customer) {
        Optional<Customer> customerData = customerRepository.findById(id);

        if (customerData.isPresent()) {
            Customer _customer = customerData.get();
            _customer.setFirstName(customer.getFirstName());
            _customer.setLastName(customer.getLastName());
            _customer.setEmail(customer.getEmail());
            _customer.setCity(customer.getCity());
            return new ResponseEntity<>(customerRepository.save(_customer), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("id") long id) {
        try {
            if (customerRepository.existsById(id)) {
                customerRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/customers")
    public ResponseEntity<Void> deleteAllCustomers() {
        try {
            customerRepository.deleteAll();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/customers/by-city/{city}")
    public ResponseEntity<List<Customer>> getCustomersByCity(@PathVariable("city") String city) {
        try {
            List<Customer> customers = customerRepository.findByCity(city);
            if (customers.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
