package com.pilotapi.controller.v1;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.SuppliersDto;
import com.pilotapi.service.SupplierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import com.pilotapi.security.SecurityConfig;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SuppliersController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.security.active=false")
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
        when(supplierService.getAll(0, 20)).thenReturn(List.of(dto));

        mockMvc.perform(get("/v1/suppliers/get-all").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].supplierID").value(1));
    }

    @Test
    void SuppliersControllerWebMvcTest_getAll_withPageParams_passesThemToService_Test() throws Exception {
        SuppliersDto dto = new SuppliersDto();
        dto.setSupplierID(1);
        dto.setCompanyName("Exotic Liquids");
        when(supplierService.getAll(2, 10)).thenReturn(List.of(dto));

        mockMvc.perform(get("/v1/suppliers/get-all?page=2&pageSize=10").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].supplierID").value(1));
    }

    @Test
    void SuppliersControllerWebMvcTest_getById_returns_ok_Test() throws Exception {
        SuppliersDto dto = new SuppliersDto();
        dto.setSupplierID(1);
        dto.setCompanyName("Exotic Liquids");
        when(supplierService.getById(1)).thenReturn(dto);

        mockMvc.perform(get("/v1/suppliers/get/1").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.supplierID").value(1));
    }

    @Test
    void SuppliersControllerWebMvcTest_add_returns_created_Test() throws Exception {
        when(supplierService.add(any(SuppliersDto.class))).thenReturn(new AddResponseIntDto(107L));

        mockMvc.perform(post("/v1/suppliers/add")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"supplierID\":1,\"companyName\":\"Exotic Liquids\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(107));
    }

    @Test
    void SuppliersControllerWebMvcTest_update_returns_json_object_Test() throws Exception {
        when(supplierService.update(any(SuppliersDto.class))).thenReturn(true);

        mockMvc.perform(put("/v1/suppliers/update")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"supplierID\":1,\"companyName\":\"Exotic Liquids\"}"))
            .andExpect(status().isNoContent());
    }

    @Test
    void SuppliersControllerWebMvcTest_delete_returns_json_object_Test() throws Exception {
        when(supplierService.delete(1)).thenReturn(true);

        mockMvc.perform(delete("/v1/suppliers/delete/1").header("ApiVersion", "1"))
            .andExpect(status().isNoContent());
    }
}
