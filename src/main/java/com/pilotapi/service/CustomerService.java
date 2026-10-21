package com.pilotapi.service;

import com.pilotapi.dto.AddResponseStringDto;
import com.pilotapi.dto.CustomersDto;
import com.pilotapi.mapper.CustomerMapper;
import com.pilotapi.model.Customer;
import com.pilotapi.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService extends AbstractCrudService<Customer, CustomersDto, String, AddResponseStringDto> {

    public CustomerService(CustomerRepository repository, CustomerMapper mapper) {
        super(repository, mapper, CustomersDto::getCustomerID, saved -> new AddResponseStringDto(saved.getCustomerID()), "Customer");
    }
}
