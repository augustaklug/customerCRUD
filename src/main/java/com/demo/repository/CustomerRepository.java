package com.demo.repository;

import com.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByCity(String city);
    List<Customer> findByFirstName(String firstName);
    Optional<Customer> findByEmail(String email);
}
