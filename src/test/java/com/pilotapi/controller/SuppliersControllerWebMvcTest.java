package com.pilotapi.controller;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.SuppliersDto;
import com.pilotapi.service.SupplierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SuppliersController.class)
class SuppliersControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupplierService supplierService;

    @Test
    void SuppliersControllerWebMvcTest_getAll_returns_ok_Test() throws Exception {
        SuppliersDto dto = new SuppliersDto();
        dto.setSupplierID(1);
        dto.setCompanyName("Exotic Liquids");
        when(supplierService.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/suppliers/get-all").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].supplierID").value(1));
    }

    @Test
    void SuppliersControllerWebMvcTest_getById_returns_ok_Test() throws Exception {
        SuppliersDto dto = new SuppliersDto();
        dto.setSupplierID(1);
        dto.setCompanyName("Exotic Liquids");
        when(supplierService.getById(1)).thenReturn(dto);

        mockMvc.perform(get("/suppliers/get/1").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.supplierID").value(1));
    }

    @Test
    void SuppliersControllerWebMvcTest_add_returns_ok_Test() throws Exception {
        when(supplierService.add(any(SuppliersDto.class))).thenReturn(new AddResponseIntDto(107L));

        mockMvc.perform(post("/suppliers/add")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"supplierID\":1,\"companyName\":\"Exotic Liquids\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(107));
    }

    @Test
    void SuppliersControllerWebMvcTest_update_returns_ok_Test() throws Exception {
        doNothing().when(supplierService).update(any(SuppliersDto.class));

        mockMvc.perform(put("/suppliers/update")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"supplierID\":1,\"companyName\":\"Exotic Liquids\"}"))
            .andExpect(status().isOk());
    }

    @Test
    void SuppliersControllerWebMvcTest_delete_returns_no_content_Test() throws Exception {
        doNothing().when(supplierService).delete(1);

        mockMvc.perform(delete("/suppliers/delete/1").header("ApiVersion", "1"))
            .andExpect(status().isNoContent());
    }
}
