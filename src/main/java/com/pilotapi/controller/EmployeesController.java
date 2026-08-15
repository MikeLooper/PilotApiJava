package com.pilotapi.controller;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.EmployeesDto;
import com.pilotapi.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employees")
public class EmployeesController {

    private final EmployeeService service;

    public EmployeesController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping("/get-all")
    public List<EmployeesDto> getAll(
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.getAll();
    }

    @GetMapping("/get/{employeeId}")
    public EmployeesDto getById(
        @PathVariable Integer employeeId,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.getById(employeeId);
    }

    @PostMapping("/add")
    public AddResponseIntDto add(
        @Valid @RequestBody EmployeesDto request,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.add(request);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(
        @Valid @RequestBody EmployeesDto request,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.update(request)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/delete/{employeeId}")
    public ResponseEntity<Void> delete(
        @PathVariable Integer employeeId,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.delete(employeeId)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.badRequest().build();
    }
}
