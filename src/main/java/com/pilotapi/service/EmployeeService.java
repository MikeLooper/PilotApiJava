package com.pilotapi.service;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.EmployeesDto;
import com.pilotapi.mapper.EmployeeMapper;
import com.pilotapi.model.Employee;
import com.pilotapi.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService extends AbstractCrudService<Employee, EmployeesDto, Integer, AddResponseIntDto> {

    public EmployeeService(EmployeeRepository repository, EmployeeMapper mapper) {
        super(repository, mapper, EmployeesDto::getEmployeeId, intIdResponse(Employee::getEmployeeID), "Employee");
    }
}
