package com.pilotapi.controller;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.CategoriesDto;
import com.pilotapi.service.CategoryService;
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

@RestController
@RequestMapping("/categories")
public class CategoriesController {

    private final CategoryService service;

    public CategoriesController(CategoryService service) {
        this.service = service;
    }

    @GetMapping("/get-all")
    public List<CategoriesDto> getAll(@RequestHeader(name = "ApiVersion", required = false) String apiVersion) {
        return service.getAll();
    }

    @GetMapping("/get/{categoryId}")
    public CategoriesDto getById(
        @PathVariable Integer categoryId,
        @RequestHeader(name = "ApiVersion", required = false) String apiVersion
    ) {
        return service.getById(categoryId);
    }

    @PostMapping("/add")
    public AddResponseIntDto add(
        @Valid @RequestBody CategoriesDto request,
        @RequestHeader(name = "ApiVersion", required = false) String apiVersion
    ) {
        return service.add(request);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(
        @Valid @RequestBody CategoriesDto request,
        @RequestHeader(name = "ApiVersion", required = false) String apiVersion
    ) {
        service.update(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<Void> delete(
        @PathVariable Integer categoryId,
        @RequestHeader(name = "ApiVersion", required = false) String apiVersion
    ) {
        service.delete(categoryId);
        return ResponseEntity.noContent().build();
    }
}
