package com.pilotapi.controller;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.CustomersDto;
import com.pilotapi.service.CustomerService;
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

@WebMvcTest(CustomersController.class)
class CustomersControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Test
    void CustomersControllerWebMvcTest_getAll_returns_ok_Test() throws Exception {
        CustomersDto dto = new CustomersDto();
        dto.setCustomerID("ALFKI");
        dto.setCompanyName("Alfreds");
        when(customerService.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/customers/get-all").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].customerID").value("ALFKI"));
    }

    @Test
    void CustomersControllerWebMvcTest_getById_returns_ok_Test() throws Exception {
        CustomersDto dto = new CustomersDto();
        dto.setCustomerID("ALFKI");
        dto.setCompanyName("Alfreds");
        when(customerService.getById("ALFKI")).thenReturn(dto);

        mockMvc.perform(get("/customers/get/ALFKI").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerID").value("ALFKI"));
    }

    @Test
    void CustomersControllerWebMvcTest_add_returns_ok_Test() throws Exception {
        when(customerService.add(any(CustomersDto.class))).thenReturn(new AddResponseIntDto(101L));

        mockMvc.perform(post("/customers/add")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerID\":\"ALFKI\",\"companyName\":\"Alfreds\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(101));
    }

    @Test
    void CustomersControllerWebMvcTest_update_returns_ok_Test() throws Exception {
        doNothing().when(customerService).update(any(CustomersDto.class));

        mockMvc.perform(put("/customers/update")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerID\":\"ALFKI\",\"companyName\":\"Alfreds\"}"))
            .andExpect(status().isOk());
    }

    @Test
    void CustomersControllerWebMvcTest_delete_returns_no_content_Test() throws Exception {
        doNothing().when(customerService).delete("ALFKI");

        mockMvc.perform(delete("/customers/delete/ALFKI").header("ApiVersion", "1"))
            .andExpect(status().isNoContent());
    }
}
