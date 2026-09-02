package com.pilotapi.controller.v1;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.CustomersDto;
import com.pilotapi.service.CustomerService;
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

@WebMvcTest(CustomersController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.security.active=false")
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

        mockMvc.perform(get("/v1/customers/get-all").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].customerID").value("ALFKI"));
    }

    @Test
    void CustomersControllerWebMvcTest_getById_returns_ok_Test() throws Exception {
        CustomersDto dto = new CustomersDto();
        dto.setCustomerID("ALFKI");
        dto.setCompanyName("Alfreds");
        when(customerService.getById("ALFKI")).thenReturn(dto);

        mockMvc.perform(get("/v1/customers/get/ALFKI").header("ApiVersion", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerID").value("ALFKI"));
    }

    @Test
    void CustomersControllerWebMvcTest_add_returns_created_Test() throws Exception {
        when(customerService.add(any(CustomersDto.class))).thenReturn(new AddResponseIntDto(101L));

        mockMvc.perform(post("/v1/customers/add")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerID\":\"ALFKI\",\"companyName\":\"Alfreds\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(101));
    }

    @Test
    void CustomersControllerWebMvcTest_update_returns_json_object_Test() throws Exception {
        when(customerService.update(any(CustomersDto.class))).thenReturn(true);

        mockMvc.perform(put("/v1/customers/update")
                .header("ApiVersion", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerID\":\"ALFKI\",\"companyName\":\"Alfreds\"}"))
            .andExpect(status().isNoContent());
    }

    @Test
    void CustomersControllerWebMvcTest_delete_returns_json_object_Test() throws Exception {
        when(customerService.delete("ALFKI")).thenReturn(true);

        mockMvc.perform(delete("/v1/customers/delete/ALFKI").header("ApiVersion", "1"))
            .andExpect(status().isNoContent());
    }
}
