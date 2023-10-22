package ee.taltech.iti0302.okapi.backend.services;

import ee.taltech.iti0302.okapi.backend.components.CustomerMapper;
import ee.taltech.iti0302.okapi.backend.components.CustomerServiceUpdate;
import ee.taltech.iti0302.okapi.backend.dto.CustomerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ee.taltech.iti0302.okapi.backend.entities.Customer;
import ee.taltech.iti0302.okapi.backend.repository.CustomerRepository;

import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public boolean customerExists(String username) {
        return customerRepository.existsByUsername(username);
    }

    private boolean validPassword(Customer customer, String testPassword) {
        return customer.getPassword().equals(testPassword);
    }

    private boolean validPassword(CustomerDTO customer) {
        return customerRepository.findByUsername(customer.getUsername()).getPassword().equals(customer.getPassword());
    }

    public boolean login(CustomerDTO customer) {
        return customerExists(customer.getUsername()) && validPassword(customer);
    }

    public CustomerDTO register(CustomerDTO customer) {
        if (!customerExists(customer.getUsername())) {
            customerRepository.save(CustomerMapper.INSTANCE.toEntity(customer));
            return customer;
        }
        return null;
    }

    public CustomerDTO update(CustomerDTO customer, CustomerServiceUpdate updateType) {
        if (customerExists(customer.getUsername())) {
            Customer dataShell = customerRepository.findByUsername(customer.getUsername());
            switch (updateType) {
                case CHANGE_USERNAME -> {
                    if (validPassword(dataShell, customer.getPassword())) {
                        dataShell.setUsername(customer.getNewUsername());
                        customer.setUsername(customer.getNewUsername());
                        customerRepository.save(dataShell);

                        return customer;
                    }
                }

                case CHANGE_PASSWORD -> {
                    if (validPassword(dataShell, customer.getPassword())) {
                        dataShell.setPassword(customer.getNewPassword());
                        customer.setPassword(customer.getNewPassword());
                        customerRepository.save(dataShell);

                        return customer;
                    }
                }

                default -> { }
            }
        }

        return null;
    }

    public CustomerDTO updateUsername(CustomerDTO customer) {
        // Check whether the desired username is taken
        if (customerExists(customer.getNewUsername())) {
            return null;
        }

        return update(customer, CustomerServiceUpdate.CHANGE_USERNAME);
    }

    public CustomerDTO updatePassword(CustomerDTO customer) {
        return update(customer, CustomerServiceUpdate.CHANGE_PASSWORD);
    }

    public CustomerDTO delete(CustomerDTO customer) {
        if (customerExists(customer.getUsername())) {
            Customer dataShell = customerRepository.findByUsername(customer.getUsername());
            if (validPassword(dataShell, customer.getPassword())) {
                customerRepository.delete(dataShell);
                return customer;
            }
        }

        return null;
    }
}
